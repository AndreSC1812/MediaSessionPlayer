package com.ascdev.mediasessionplayer.domain.usecase

import com.ascdev.mediasessionplayer.domain.model.PlayerState
import com.ascdev.mediasessionplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayerStateUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<PlayerState> {
        return playerRepository.playerState
    }
}
