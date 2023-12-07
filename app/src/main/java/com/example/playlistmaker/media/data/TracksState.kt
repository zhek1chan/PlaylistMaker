package com.example.playlistmaker.media.data

import com.example.playlistmaker.player.domain.Track

sealed interface TracksState {
    data class Tracks(val tracks: List<Track>) : TracksState
    data class Empty(val message: Int) : TracksState
}