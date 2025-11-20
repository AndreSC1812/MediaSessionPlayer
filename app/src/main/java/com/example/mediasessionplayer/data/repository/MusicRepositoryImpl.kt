package com.example.mediasessionplayer.data.repository

import com.example.mediasessionplayer.data.source.LocalMusicDataSource
import com.example.mediasessionplayer.domain.model.Song
import com.example.mediasessionplayer.domain.repository.MusicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val localMusicDataSource: LocalMusicDataSource
) : MusicRepository {

    private var cachedSongs: List<Song>? = null

    override suspend fun getSongs(): List<Song> {
        if (cachedSongs == null) {
            val localSongs = localMusicDataSource.getLocalSongs()
            cachedSongs = localSongs
        }
        return cachedSongs ?: emptyList()
    }

    override fun getSongById(id: String): Song? {
        return cachedSongs?.find { it.id == id }
    }
}
