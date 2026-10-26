package com.ascdev.mediasessionplayer.domain.usecase

import com.ascdev.mediasessionplayer.domain.model.Song
import com.ascdev.mediasessionplayer.domain.repository.PlayerRepository
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(song: Song) {
        playerRepository.playSong(song)
    }
}
