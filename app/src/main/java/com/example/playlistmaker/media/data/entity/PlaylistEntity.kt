package com.example.playlistmaker.media.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long,
    val name: String,
    val description: String,
    val artworkUrl100: String,
    val trackId: Long,
    val num: Long
)