package com.example.playlistmaker.history

import android.content.SharedPreferences
import com.example.playlistmaker.TRACKS_LIST_KEY
import com.example.playlistmaker.Track
import com.google.gson.Gson

class SearchHistory(val sharedPreferences: SharedPreferences) {


    val searchedTrackList = mutableListOf<Track>()

    init {
        val searchedTrack = sharedPreferences.getString(TRACKS_LIST_KEY, "") ?: ""
        if (searchedTrack.isNotEmpty()) {
            searchedTrackList.addAll(createTrackListFromJson(searchedTrack))
        }
    }

    fun addNewTrack(track: Track) {
        if (searchedTrackList.contains(track)) searchedTrackList.remove(track)
        searchedTrackList.add(0, track)
        while (searchedTrackList.size > 10) {
            searchedTrackList.removeAt(10)
        }
        sharedPreferences.edit()
            .putString(
                TRACKS_LIST_KEY,
                createJsonFromTrackList(searchedTrackList.toTypedArray())
            ) // передаёт данные в adapter, а createJsonFromFactsList() их сериализует
            .apply()
    }

    fun clearHistory() {
        searchedTrackList.clear()
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    private fun createTrackListFromJson(json: String?): Array<Track> {
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    private fun createJsonFromTrackList(facts: Array<Track>): String {
        return Gson().toJson(facts)
    }


}