package com.example.playlistmaker.media.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "TracksInPlaylist",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["playlistId"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = TrackInsidePlaylistEntity::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class TracksInPlaylistEntity(
    val playlistId: Long,
    val trackId: Long,
)