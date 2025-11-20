package com.example.mediasessionplayer.domain.repository

import com.example.mediasessionplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getSongs(): List<Song>
    fun getSongById(id: String): Song?
}
