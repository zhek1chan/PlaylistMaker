package com.example.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.playlistmaker.media.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.converters.TrackInPlaylistConvertor
import com.example.playlistmaker.media.data.db.AppDataBase
import com.example.playlistmaker.media.data.entity.PlaylistEntity
import com.example.playlistmaker.media.data.entity.TrackInsidePlaylistEntity
import com.example.playlistmaker.media.data.entity.TracksInPlaylistEntity
import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.domain.db.PlaylistsRepository
import com.example.playlistmaker.media.ui.fragments.playlists.CreatingPlaylistFragment
import com.example.playlistmaker.player.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val db: AppDataBase,
    private val convertor: PlaylistDbConvertor,
    private val convertorForTrack: TrackInPlaylistConvertor,
    private val context: Context
) : PlaylistsRepository {
    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = db.playlistDao().getPlaylist()
        emit(convertFromPlaylistEntity(playlists.reversed()))
    }

    override fun putPlaylist(playlist: Playlist, tracks: List<Track>) {
        //Восстанавливаем таблицу треков
        Log.d("getting tracks", "$tracks")
        for (track in tracks) {
            if (track.trackId != 0.toLong()) {
                insertTrack(track)
                db.playlistDao().insertRestoredTrack(playlist.playlistId, track.trackId)
            }
        }
        var converted: PlaylistEntity = convertor.map(playlist)
        Log.d("changed playlist", "successfully")
        db.playlistDao().insertPlaylist(converted)
        Log.d("inserted to restored tracks", "successfully")
        db.playlistDao().restoreTrack()
        Log.d("restoring tracks", "successfully")
        db.playlistDao().clearRestoredTracks(playlist.playlistId)
        Log.d("clearing restored tracks", "successfully")
    }

    override fun deletePlaylist(playlist: Playlist) {
        db.playlistDao().deletePlaylist(playlist.playlistId)
        Log.d("Playlist ${playlist.playlistId}", "was deleted")
        db.playlistDao().decreaseAllQuantity(playlist.playlistId)
    }

    override fun checkPlaylist(id: Long): Flow<Boolean> = flow {
        emit(db.playlistDao().queryPlaylistId(id) != null)
    }

    override fun getTracks(id: Long): Flow<List<Track>> = flow {
        val tracks = db.playlistDao().getTracksFromPlaylist(id)
        emit(convertFromTrackEntity(tracks.reversed()))
    }

    override fun putTrack(tracks: TracksInPlaylistEntity) {
        db.playlistDao().addTrackToPlaylist(tracks)
    }

    override fun insertTrack(track: Track) {
        var converted: TrackInsidePlaylistEntity = convertorForTrack.map(track)
        db.playlistDao().insertTrack(converted)
        Log.d("DB", "Inserted $track to DB_playlist")
    }

    override fun savePic(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                CreatingPlaylistFragment.album
            )
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, CreatingPlaylistFragment.jpg)
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = context.contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }


    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlists -> convertor.map(playlists) }
    }

    private fun convertFromTrackEntity(tracks: List<TrackInsidePlaylistEntity>): List<Track> {
        return tracks.map { track -> convertorForTrack.map(track) }
    }

    override fun checkIfAlreadyInPlaylist(track: Track, playlist: Playlist): Boolean {
        val e = db.playlistDao().checkIfTrackIsInPlaylist(playlist.playlistId, track.trackId)
        return e
    }

    override fun getData(id: Long): Playlist {
        val playlist = db.playlistDao().queryPlaylistId(id)
        val list = listOf(playlist)
        val pl = convertFromPlaylistEntity(list)
        return pl.first()
    }

    override fun deleteTrack(track: Track, playlist: Playlist) {
        db.playlistDao().deleteTrack(track.trackId, playlist.playlistId)
        db.playlistDao().decreaseQuantity(playlist.playlistId)
    }


}