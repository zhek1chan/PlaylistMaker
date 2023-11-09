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
    fun getPlaylistId(): Flow<List<Integer>>

    @Query("DELETE FROM playlists_table WHERE :playlistsId = PlaylistId")
    fun deletePlaylist(playlistsId: Long): Integer

    @Query("SELECT * FROM playlists_table WHERE :searchId = PlaylistId")
    fun queryPlaylistId(searchId: Long): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addingTrack(tracksInPlaylist: TracksInPlaylistEntity)

    @Insert(entity = TrackInsidePlaylistEntity::class, onConflict = OnConflictStrategy.ABORT)
    fun insertTrack(track: TrackInsidePlaylistEntity)

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
    fun getTracksFromPlaylist(playlistId: Long): Flow<List<TrackInsidePlaylistEntity>>


    @Query("SELECT EXISTS (SELECT * FROM TracksInPlaylist WHERE playlistId = :playlistId AND trackId = :trackId)")
    fun checkIfTrackIsInPlaylist(playlistId: Long, trackId: Long): Boolean

    @Transaction
    fun addTrackToPlaylist(tInP: TracksInPlaylistEntity): Boolean {
        //проверка на добавление трека до текущего момента
        return if (!checkIfTrackIsInPlaylist(
                tInP.playlistId,
                tInP.trackId
            )
        ) {
            updateQuantity(tInP.playlistId)
            addingTrack(tInP)
            true
        } else {
            false
        }
    }
}