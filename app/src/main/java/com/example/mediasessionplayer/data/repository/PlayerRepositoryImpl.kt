package com.example.mediasessionplayer.data.repository

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mediasessionplayer.data.service.MusicService
import com.example.mediasessionplayer.domain.model.PlayerState
import com.example.mediasessionplayer.domain.model.PlaybackState
import com.example.mediasessionplayer.domain.model.Song
import com.example.mediasessionplayer.domain.repository.PlayerRepository
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerRepository {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: Flow<PlayerState> = _playerState.asStateFlow()

    private var currentPlaylist: List<Song> = emptyList()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var positionUpdateJob: Job? = null

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            setupPlayerListener()
            startPositionUpdates()
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
                if (isPlaying) {
                    startPositionUpdates()
                } else {
                    stopPositionUpdates()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val state = when (playbackState) {
                    Player.STATE_IDLE -> PlaybackState.IDLE
                    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                    Player.STATE_READY -> PlaybackState.READY
                    Player.STATE_ENDED -> PlaybackState.ENDED
                    else -> PlaybackState.IDLE
                }
                _playerState.value = _playerState.value.copy(
                    playbackState = state,
                    duration = mediaController?.duration ?: 0L
                )
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val currentIndex = mediaController?.currentMediaItemIndex ?: -1
                if (currentIndex >= 0 && currentIndex < currentPlaylist.size) {
                    _playerState.value = _playerState.value.copy(
                        currentSong = currentPlaylist[currentIndex],
                        duration = mediaController?.duration ?: 0L
                    )
                }
            }
        })
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = coroutineScope.launch {
            while (isActive) {
                val currentPosition = mediaController?.currentPosition ?: 0L
                val duration = mediaController?.duration ?: 0L

                _playerState.value = _playerState.value.copy(
                    currentPosition = currentPosition,
                    duration = if (duration > 0) duration else _playerState.value.duration
                )

                delay(1000) // Update every second
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override fun play() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
        _playerState.value = _playerState.value.copy(currentPosition = position)
    }

    override fun skipToNext() {
        mediaController?.seekToNext()
    }

    override fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }

    override fun playSong(song: Song) {
        val mediaItem = createMediaItem(song)
        mediaController?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        _playerState.value = _playerState.value.copy(
            currentSong = song,
            duration = song.duration
        )
    }

    override fun setPlaylist(songs: List<Song>, startIndex: Int) {
        currentPlaylist = songs
        val mediaItems = songs.map { createMediaItem(it) }

        mediaController?.apply {
            setMediaItems(mediaItems, startIndex, 0)
            prepare()
            play()
        }

        if (startIndex < songs.size) {
            _playerState.value = _playerState.value.copy(
                currentSong = songs[startIndex],
                duration = songs[startIndex].duration
            )
        }
    }

    private fun createMediaItem(song: Song): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)
            .setArtworkUri(song.imageUrl.toUri())
            .build()

        return MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(song.mediaUri)
            .setMediaMetadata(metadata)
            .build()
    }

    override fun release() {
        stopPositionUpdates()
        MediaController.releaseFuture(controllerFuture ?: return)
        mediaController = null
    }
}
