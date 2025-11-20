package com.example.mediasessionplayer.domain.usecase

import com.example.mediasessionplayer.domain.model.PlayerState
import com.example.mediasessionplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayerStateUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<PlayerState> {
        return playerRepository.playerState
    }
}
