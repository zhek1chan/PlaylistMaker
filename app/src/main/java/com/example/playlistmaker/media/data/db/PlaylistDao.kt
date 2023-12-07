package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.media.data.entity.PlaylistEntity
import com.example.playlistmaker.media.data.entity.TrackInsidePlaylistEntity
import com.example.playlistmaker.media.data.entity.TracksInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(pl: PlaylistEntity)

    @Query("SELECT * FROM playlists_table")
    fun getPlaylist(): List<PlaylistEntity>

    @Query("SELECT PlaylistId FROM playlists_table")
    fun getPlaylistId(): Flow<List<Int>>

    @Query("DELETE FROM TracksInPlaylist WHERE playlistId = :playlistsId AND trackId = :trackId")
    fun deleteTrack(trackId: Long, playlistsId: Long)

    @Query("DELETE FROM playlists_table WHERE PlaylistId = :playlistsId")
    fun deletePlaylist(playlistsId: Long)

    @Query("DELETE FROM TracksInPlaylist WHERE PlaylistId = :playlistId")
    fun deleteTracksInPlaylist(playlistId: Long)


    @Query("SELECT * FROM playlists_table WHERE :searchId = PlaylistId")
    fun queryPlaylistId(searchId: Long): PlaylistEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addingTrack(tracksInPlaylist: TracksInPlaylistEntity)

    @Insert(entity = TrackInsidePlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(track: TrackInsidePlaylistEntity)

    @Query("UPDATE playlists_table SET num = num - num WHERE playlistId = :playlistId")
    fun decreaseAllQuantity(playlistId: Long): Int?

    @Query("UPDATE playlists_table SET num = num - 1 WHERE playlistId = :playlistId")
    fun decreaseQuantity(playlistId: Long): Int?

    @Query("UPDATE playlists_table SET num = num + 1 WHERE playlistId = :playlistId")
    fun updateQuantity(playlistId: Long): Int?

    //отображение всех треков в плейлисте
    @Transaction
    @Query(
        """
        SELECT * FROM track_in_playlist_table
        JOIN TracksInPlaylist ON track_in_playlist_table.trackId = TracksInPlaylist.trackId
        WHERE TracksInPlaylist.playlistId = :playlistId;
        """
    )
    fun getTracksFromPlaylist(playlistId: Long): List<TrackInsidePlaylistEntity>


    @Query("SELECT EXISTS (SELECT * FROM TracksInPlaylist WHERE playlistId = :playlistId AND trackId = :trackId)")
    fun checkIfTrackIsInPlaylist(playlistId: Long, trackId: Long): Boolean

    @Query("SELECT * FROM playlists_table WHERE playlistId=:id")
    fun getdata(id: Long): PlaylistEntity

    @Transaction
    fun addTrackToPlaylist(tInP: TracksInPlaylistEntity): Boolean {
        //проверка на добавление трека до текущего момента
        return if (!checkIfTrackIsInPlaylist(tInP.playlistId, tInP.trackId)) {
            updateQuantity(tInP.playlistId)
            addingTrack(tInP)
            true
        } else {
            false
        }
    }


    @Query("INSERT INTO restored_tracks_in_playlist (playlistId, TrackId) VALUES (:pl, :tr)")
    fun insertRestoredTrack(pl: Long, tr: Long)

    @Query("INSERT INTO TracksInPlaylist SELECT * FROM restored_tracks_in_playlist")
    fun restoreTrack()

    @Query("DELETE FROM restored_tracks_in_playlist WHERE playlistId = :playlistId")
    fun clearRestoredTracks(playlistId: Long)
}