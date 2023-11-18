package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.domain.db.Playlist

sealed interface PlaylistsState {
    data class Playlists(val playlist: List<Playlist>) : PlaylistsState
    data class Empty(val message: Int) : PlaylistsState
}