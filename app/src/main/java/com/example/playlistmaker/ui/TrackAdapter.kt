package com.example.playlistmaker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.data.Track

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder> () {
    val tracks = mutableListOf<Track>()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TrackViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_track,p0, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(p0: TrackViewHolder, p1: Int) {
        p0.bind(tracks[p1])

    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}