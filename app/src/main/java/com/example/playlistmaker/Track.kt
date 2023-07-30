package com.example.playlistmaker

import java.util.Date

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    var trackId: String,
    var collectionName: String,
    var releaseDate: Date,
    var primaryGenreName: String,
    var country: String
)
