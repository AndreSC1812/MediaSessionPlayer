package com.ascdev.mediasessionplayer.domain.repository

import com.ascdev.mediasessionplayer.domain.model.PlayerState
import com.ascdev.mediasessionplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    val playerState: Flow<PlayerState>

    fun play()
    fun pause()
    fun seekTo(position: Long)
    fun skipToNext()
    fun skipToPrevious()
    fun playSong(song: Song)
    fun setPlaylist(songs: List<Song>, startIndex: Int = 0)
    fun release()
}
