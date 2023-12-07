package com.example.playlistmaker.media.domain.db

import android.net.Uri
import com.example.playlistmaker.media.data.entity.TracksInPlaylistEntity
import com.example.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun playlistAdd(playlist: Playlist, tracks: List<Track>)
    fun playlistDelete(playlist: Playlist)
    suspend fun playlistGet(): Flow<List<Playlist>>
    suspend fun playlistCheck(id: Long): Flow<Boolean>
    suspend fun getTracks(id: Long): Flow<List<Track>>
    fun putTrack(track: TracksInPlaylistEntity)
    fun insertTrack(track: Track)
    fun savePic(uri: Uri)
    fun checkIfAlreadyInPlaylist(track: Track, playlist: Playlist): Boolean
    fun getPlaylistData(id: Long): Playlist
    fun deleteTrack(track: Track, playlist: Playlist)
}
