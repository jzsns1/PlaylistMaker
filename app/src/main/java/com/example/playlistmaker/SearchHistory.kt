package com.example.playlistmaker

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.data.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val gson = Gson()

    fun getHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    fun addTrack(track: Track) {
        val history = getHistory()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.subList(MAX_HISTORY_SIZE, history.size).clear()
        }
        sharedPrefs.edit { putString(SEARCH_HISTORY_KEY, gson.toJson(history)) }
    }

    fun clearHistory() {
        sharedPrefs.edit { remove(SEARCH_HISTORY_KEY) }
    }

    companion object {
        const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}
