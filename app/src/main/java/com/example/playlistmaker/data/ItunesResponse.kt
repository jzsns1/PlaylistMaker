package com.example.playlistmaker.data


data class ItunesResponse(
    val resultCount: Int,
    val results: List<Track>,
)