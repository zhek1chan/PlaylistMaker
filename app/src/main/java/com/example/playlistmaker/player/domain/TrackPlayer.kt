package com.example.playlistmaker.player.domain

interface TrackPlayer {
    // 1
    fun play(trackId: String, statusObserver: StatusObserver)
    fun pause(trackId: String)
    fun seek(trackId: String, position: Float)

    fun release(trackId: String)

    // 2
    interface StatusObserver {
        fun onProgress(progress: Float)
        fun onStop()
        fun onPlay()
    }
}