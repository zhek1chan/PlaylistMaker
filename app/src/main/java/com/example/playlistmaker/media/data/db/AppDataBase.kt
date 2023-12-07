package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.entity.PlaylistEntity
import com.example.playlistmaker.media.data.entity.RestoredTracksInPlaylistEntity
import com.example.playlistmaker.media.data.entity.TrackEntity
import com.example.playlistmaker.media.data.entity.TrackInsidePlaylistEntity
import com.example.playlistmaker.media.data.entity.TracksInPlaylistEntity

@Database(
    version = 2,
    entities = [TrackEntity::class, PlaylistEntity::class, TracksInPlaylistEntity::class, TrackInsidePlaylistEntity::class, RestoredTracksInPlaylistEntity::class]
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}
