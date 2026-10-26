package com.ascdev.mediasessionplayer.domain.repository

import com.ascdev.mediasessionplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getSongs(): List<Song>

    /** Emits every time the device's audio library changes. */
    fun observeLibraryChanges(): Flow<Unit>
}
