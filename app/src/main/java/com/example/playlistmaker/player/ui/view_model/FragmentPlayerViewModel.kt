package com.example.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.data.PlaylistsState
import com.example.playlistmaker.media.data.entity.TracksInPlaylistEntity
import com.example.playlistmaker.media.domain.db.LikedTracksInteractor
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.Track
import com.example.playlistmaker.player.ui.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentPlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val likeInteractor: LikedTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var timeJob: Job? = null
    private var likeJob: Job? = null
    private var likeIndicator = MutableLiveData<Boolean>()
    private var timer = MutableLiveData("00:00")
    fun createPlayer(url: String, completion: () -> Unit) {
        playerInteractor.createPlayer(url, completion)
    }

    fun play() {
        playerInteractor.play()
        timeJob?.start()!!
    }

    fun pause() {
        playerInteractor.pause()
    }

    fun getTime(): LiveData<String> {
        timeJob = viewModelScope.launch {
            while (true) {
                delay(PLAYER_BUTTON_PRESSING_DELAY)
                playerInteractor.getTime().collect() {
                    timer.postValue(it)
                }
            }
        }
        return timer
    }

    fun putTime(): LiveData<String> {
        getTime()
        return timer
    }

    fun playerStateGetter(): PlayerState {
        return playerInteractor.playerStateGetter()
    }

    fun onLikeClick(track: Track) {
        val id = track.trackId
        if (track.isFavourite) {
            id.let {
                Log.d("Deleting track from favourites", "$track")
                likeInteractor.favouritesDelete(track)
                track.isFavourite = false
            }
        } else id.let {
            Log.d("Pressed like button with", "$track, \"adding to favourites track\"")
            likeInteractor.favouritesAdd(track)
            track.isFavourite = true
        }
    }

    fun onLikedCheck(track: Track): LiveData<Boolean> {
        likeJob = viewModelScope.launch {
            while (true) {
                delay(PLAYER_BUTTON_PRESSING_DELAY)
                track.trackId.let { id ->
                    likeInteractor.favouritesCheck(id)
                        .collect { value ->
                            likeIndicator.postValue(value)
                        }
                }
            }
        }
        return likeIndicator
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor
                .playlistGet()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData
    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            stateLiveData.postValue(PlaylistsState.Empty(R.string.nothing_in_favourite))
        } else {
            stateLiveData.postValue(PlaylistsState.Playlists(playlists))
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        val booleanType = playlistsInteractor.checkIfAlreadyInPlaylist(track, playlist)
        if (booleanType == true) {
            return false
        } else {
            Log.d("Viewm", "Click on adding to album")
            val playlistEnt = TracksInPlaylistEntity(playlist.playlistId, track.trackId)
            playlistsInteractor.insertTrack(track)
            Log.d("Viewm", "Inserting track")
            playlistsInteractor.putTrack(playlistEnt)
            return true
        }
    }

    companion object {
        const val PLAYER_BUTTON_PRESSING_DELAY = 300L
    }
}