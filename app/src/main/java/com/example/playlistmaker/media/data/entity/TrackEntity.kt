package com.example.playlistmaker.media.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tracks_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: Long,
    val artworkUrl100: String,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val trackTimeMillis: String,
    val previewUrl: String
)
