package com.example.playlistmaker.media.domain.db

import android.app.Activity
import android.net.Uri
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.media.data.entity.TracksInPlaylistEntity
import com.example.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>

    fun putPlaylist(playlist: Playlist)

    fun deletePlaylist(playlist: Playlist)

    fun checkPlaylist(id: Long): Flow<Boolean>

    fun getTracks(id: Long): Flow<List<Track>>

    fun putTrack(track: TracksInPlaylistEntity)

    fun insertTrack(track: Track)

    fun savePic(uri: Uri, activity: Activity)
}
