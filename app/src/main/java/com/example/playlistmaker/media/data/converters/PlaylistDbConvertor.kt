package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.domain.db.Playlist
import com.example.playlistmaker.media.data.entity.PlaylistEntity

class PlaylistDbConvertor {
    fun map(pl: Playlist): PlaylistEntity {
        return PlaylistEntity(
            pl.playlistId,
            pl.name,
            pl.description,
            pl.artworkUrl100,
            pl.trackId,
            pl.num
        )
    }

    fun map(pl: PlaylistEntity): Playlist {
        return Playlist(
            pl.playlistId,
            pl.name,
            pl.description,
            pl.artworkUrl100,
            pl.trackId,
            pl.num
        )
    }
}