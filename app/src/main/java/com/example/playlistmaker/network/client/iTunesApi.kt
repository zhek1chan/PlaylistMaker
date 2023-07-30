package com.example.playlistmaker.network

import com.example.playlistmaker.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("search?entity=song")
    fun findTrack(@Query("term") text: String): Call<TrackResponse>
}