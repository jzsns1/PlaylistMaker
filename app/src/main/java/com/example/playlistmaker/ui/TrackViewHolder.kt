package com.example.playlistmaker.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.Track
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Locale.getDefault

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvArtistAndTime: TextView = itemView.findViewById(R.id.tvArtistAndTime)

    fun bind(track: Track) {
        tvTrackName.text = track.trackName ?: ""
        val time = if (track.trackTimeMillis != null) {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        } else {
            ""
        }
        tvArtistAndTime.text = "${track.artistName ?: ""} · $time"

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(ivArtwork)
    }
}