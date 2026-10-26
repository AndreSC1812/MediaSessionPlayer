package com.ascdev.mediasessionplayer.domain.usecase

import com.ascdev.mediasessionplayer.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLibraryChangesUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(): Flow<Unit> {
        return musicRepository.observeLibraryChanges()
    }
}
