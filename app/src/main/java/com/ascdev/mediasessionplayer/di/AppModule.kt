package com.ascdev.mediasessionplayer.di

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import com.ascdev.mediasessionplayer.data.repository.MusicRepositoryImpl
import com.ascdev.mediasessionplayer.data.repository.PlayerRepositoryImpl
import com.ascdev.mediasessionplayer.domain.repository.MusicRepository
import com.ascdev.mediasessionplayer.domain.repository.PlayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }

    @Provides
    @Singleton
    fun provideMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository = musicRepositoryImpl

    @Provides
    @Singleton
    fun providePlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository = playerRepositoryImpl
}
