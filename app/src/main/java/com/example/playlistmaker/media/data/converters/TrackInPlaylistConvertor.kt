package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.entity.TrackInsidePlaylistEntity
import com.example.playlistmaker.player.domain.Track

class TrackInPlaylistConvertor {
    fun map(track: TrackInsidePlaylistEntity): Track {
        return Track(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavourite
        )
    }
    fun map(track: Track): TrackInsidePlaylistEntity {
        return TrackInsidePlaylistEntity(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavourite
        )
    }
}