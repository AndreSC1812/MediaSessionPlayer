package com.example.mediasessionplayer.domain.model

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playbackState: PlaybackState = PlaybackState.IDLE
)

enum class PlaybackState {
    IDLE,
    BUFFERING,
    READY,
    ENDED
}
