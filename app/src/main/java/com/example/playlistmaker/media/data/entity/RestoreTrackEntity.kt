package com.example.playlistmaker.media.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "restored_tracks_in_playlist",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = TrackInsidePlaylistEntity::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class RestoredTracksInPlaylistEntity(
    val playlistId: Long,
    val trackId: Long,
)