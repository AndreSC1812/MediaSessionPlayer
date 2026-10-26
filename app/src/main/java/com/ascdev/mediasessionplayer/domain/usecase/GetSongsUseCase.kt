package com.ascdev.mediasessionplayer.domain.usecase

import com.ascdev.mediasessionplayer.domain.model.Song
import com.ascdev.mediasessionplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): List<Song> {
        return musicRepository.getSongs()
    }
}
