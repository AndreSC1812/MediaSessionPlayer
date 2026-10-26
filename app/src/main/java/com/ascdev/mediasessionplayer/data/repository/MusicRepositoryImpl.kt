package com.ascdev.mediasessionplayer.data.repository

import com.ascdev.mediasessionplayer.data.source.LocalMusicDataSource
import com.ascdev.mediasessionplayer.domain.model.Song
import com.ascdev.mediasessionplayer.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val localMusicDataSource: LocalMusicDataSource
) : MusicRepository {

    override suspend fun getSongs(): List<Song> = localMusicDataSource.getLocalSongs()

    override fun observeLibraryChanges(): Flow<Unit> = localMusicDataSource.observeMediaStoreChanges()
}
