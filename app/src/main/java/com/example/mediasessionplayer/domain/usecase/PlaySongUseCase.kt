package com.example.mediasessionplayer.domain.usecase

import com.example.mediasessionplayer.domain.model.Song
import com.example.mediasessionplayer.domain.repository.PlayerRepository
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(song: Song) {
        playerRepository.playSong(song)
    }
}
