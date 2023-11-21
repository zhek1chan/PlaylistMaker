package com.example.playlistmaker.media.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.data.TracksState
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.player.domain.Track
import kotlinx.coroutines.launch
import java.lang.Math.round

class PlaylistViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData
    fun getTracks(id: Long) {
        Log.d("getTracksMethod", "started")
        viewModelScope.launch {
            interactor
                .getTracks(id)
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            stateLiveData.postValue(TracksState.Empty(R.string.nothing_in_favourite))
            Log.d("Tracks in playlist", "NULL")
        } else {
            stateLiveData.postValue(TracksState.Tracks(tracks))
            Log.d("Tracks in playlist", "$tracks")

        }
    }

    fun deletePlaylist(pl: Playlist) {
        interactor.playlistDelete(pl)
    }

    fun deleteTrack(t: Track, pl: Playlist) {
        interactor.deleteTrack(t, pl)
    }

    fun getTimeSum(tracks: List<Track>): String {
        var sum = 0
        for (track in tracks) {
            Log.d("Sum", "$sum")
            sum += Character.getNumericValue(track.trackTimeMillis[0]) * 600
            Log.d("Sum", "$sum")
            sum += Character.getNumericValue(track.trackTimeMillis[1]) * 60
            Log.d("Sum", "$sum")
            sum += Character.getNumericValue(track.trackTimeMillis[3]) * 10
            Log.d("Sum", "$sum")
            sum += Character.getNumericValue(track.trackTimeMillis[4])
            Log.d("Sum", "$sum")


        }
        var res = "${round((sum / 60).toDouble())}"
        return res
    }
}