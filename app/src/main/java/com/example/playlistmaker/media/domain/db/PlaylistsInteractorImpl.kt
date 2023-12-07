package com.example.playlistmaker.media.domain.db

import android.net.Uri
import com.example.playlistmaker.media.data.entity.TracksInPlaylistEntity
import com.example.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {
    override fun playlistAdd(playlist: Playlist, tracks: List<Track>) {
        repository.putPlaylist(playlist, tracks)
    }

    override suspend fun playlistGet(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun playlistCheck(id: Long): Flow<Boolean> {
        return repository.checkPlaylist(id)
    }

    override suspend fun getTracks(id: Long): Flow<List<Track>> {
        return repository.getTracks(id)
    }

    override fun putTrack(track: TracksInPlaylistEntity) {
        return repository.putTrack(track)
    }

    override fun insertTrack(track: Track) {
        return repository.insertTrack(track)
    }

    override fun savePic(uri: Uri) {
        repository.savePic(uri)
    }

    override fun playlistDelete(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override fun checkIfAlreadyInPlaylist(track: Track, playlist: Playlist): Boolean {
        val e = repository.checkIfAlreadyInPlaylist(track, playlist)
        return e
    }

    override fun getPlaylistData(id: Long): Playlist {
        val pl = repository.getData(id)
        return pl
    }

    override fun deleteTrack(track: Track, playlist: Playlist) {
        repository.deleteTrack(track, playlist)
    }
}