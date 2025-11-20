package com.example.mediasessionplayer.domain.usecase

import com.example.mediasessionplayer.domain.model.Song
import com.example.mediasessionplayer.domain.repository.MusicRepository
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): List<Song> {
        return musicRepository.getSongs()
    }
}
