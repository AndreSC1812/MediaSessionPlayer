package com.example.mediasessionplayer.domain.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val imageUrl: String,
    val mediaUri: String
)
