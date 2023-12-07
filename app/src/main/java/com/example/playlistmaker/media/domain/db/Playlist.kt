package com.example.playlistmaker.media.domain.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    var playlistId: Long,
    var name: String,
    var description: String,
    var artworkUrl100: String,
    var trackId: Long,
    var num: Long
) : Parcelable