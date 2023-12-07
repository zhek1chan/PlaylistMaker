package com.example.playlistmaker.media.ui.viewmodel.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.data.PlaylistsState
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val interactor: PlaylistsInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData
    fun fillData() {
        viewModelScope.launch {
            interactor
                .playlistGet()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            stateLiveData.postValue(PlaylistsState.Empty(R.string.nothing_in_favourite))
        } else {
            stateLiveData.postValue(PlaylistsState.Playlists(playlists))
        }
    }
}