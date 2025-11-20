package com.example.mediasessionplayer.domain.usecase

import com.example.mediasessionplayer.domain.model.Song
import com.example.mediasessionplayer.domain.repository.PlayerRepository
import javax.inject.Inject

class ControlPlaybackUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    fun play() = playerRepository.play()

    fun pause() = playerRepository.pause()

    fun seekTo(position: Long) = playerRepository.seekTo(position)

    fun skipToNext() = playerRepository.skipToNext()

    fun skipToPrevious() = playerRepository.skipToPrevious()

    fun setPlaylist(songs: List<Song>, startIndex: Int) =
        playerRepository.setPlaylist(songs, startIndex)
}
