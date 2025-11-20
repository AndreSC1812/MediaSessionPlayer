package com.example.mediasessionplayer.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediasessionplayer.data.repository.MusicRepositoryImpl
import com.example.mediasessionplayer.data.repository.PlayerRepositoryImpl
import com.example.mediasessionplayer.domain.repository.MusicRepository
import com.example.mediasessionplayer.domain.repository.PlayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
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
