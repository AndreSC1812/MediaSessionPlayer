package com.ascdev.mediasessionplayer.data.repository

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.ascdev.mediasessionplayer.data.service.MusicService
import com.ascdev.mediasessionplayer.domain.model.PlaybackState
import com.ascdev.mediasessionplayer.domain.model.PlayerState
import com.ascdev.mediasessionplayer.domain.model.Song
import com.ascdev.mediasessionplayer.domain.repository.PlayerRepository
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerRepository {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    // Commands issued before the controller is connected are replayed once it is ready
    private val pendingCommands = mutableListOf<(MediaController) -> Unit>()

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: Flow<PlayerState> = _playerState.asStateFlow()

    private var currentPlaylist: List<Song> = emptyList()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var positionUpdateJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startPositionUpdates()
            } else {
                stopPositionUpdates()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _playerState.update {
                it.copy(
                    playbackState = playbackState.toPlaybackState(),
                    duration = mediaController.durationOr(it.duration)
                )
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val song = findSong(mediaItem) ?: return
            _playerState.update {
                it.copy(
                    currentSong = song,
                    currentPosition = 0L,
                    duration = mediaController.durationOr(song.duration)
                )
            }
        }
    }

    init {
        connect()
    }

    private fun connect() {
        if (controllerFuture != null) return

        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        val future = MediaController.Builder(context, sessionToken)
            .setListener(object : MediaController.Listener {
                override fun onDisconnected(controller: MediaController) {
                    onControllerDisconnected()
                }
            })
            .buildAsync()
        controllerFuture = future

        future.addListener({
            val controller = runCatching { future.get() }.getOrNull()
            if (controller == null) {
                controllerFuture = null
                pendingCommands.clear()
                return@addListener
            }
            mediaController = controller
            controller.addListener(playerListener)
            syncStateFrom(controller)
            pendingCommands.forEach { command -> command(controller) }
            pendingCommands.clear()
        }, ContextCompat.getMainExecutor(context))
    }

    // The service is stopped when the app is removed from recents, so the controller
    // has to be rebuilt the next time the user interacts with the player
    private fun onControllerDisconnected() {
        stopPositionUpdates()
        mediaController?.removeListener(playerListener)
        mediaController = null
        controllerFuture = null
        _playerState.value = PlayerState()
    }

    private fun withController(command: (MediaController) -> Unit) {
        val controller = mediaController
        if (controller != null && controller.isConnected) {
            command(controller)
        } else {
            pendingCommands += command
            connect()
        }
    }

    private fun syncStateFrom(controller: MediaController) {
        _playerState.update {
            it.copy(
                currentSong = findSong(controller.currentMediaItem) ?: it.currentSong,
                isPlaying = controller.isPlaying,
                currentPosition = controller.currentPosition,
                duration = controller.durationOr(it.duration),
                playbackState = controller.playbackState.toPlaybackState()
            )
        }
        if (controller.isPlaying) startPositionUpdates()
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = coroutineScope.launch {
            while (isActive) {
                val controller = mediaController ?: break
                _playerState.update {
                    it.copy(
                        currentPosition = controller.currentPosition,
                        duration = controller.durationOr(it.duration)
                    )
                }
                delay(POSITION_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override fun play() = withController { it.play() }

    override fun pause() = withController { it.pause() }

    override fun seekTo(position: Long) {
        withController { it.seekTo(position) }
        _playerState.update { it.copy(currentPosition = position) }
    }

    override fun skipToNext() = withController { it.seekToNext() }

    override fun skipToPrevious() = withController { it.seekToPrevious() }

    override fun playSong(song: Song) = setPlaylist(listOf(song), 0)

    override fun setPlaylist(songs: List<Song>, startIndex: Int) {
        val startSong = songs.getOrNull(startIndex) ?: return
        currentPlaylist = songs
        val mediaItems = songs.map { createMediaItem(it) }

        withController {
            it.setMediaItems(mediaItems, startIndex, 0)
            it.prepare()
            it.play()
        }

        _playerState.update {
            it.copy(
                currentSong = startSong,
                currentPosition = 0L,
                duration = startSong.duration
            )
        }
    }

    private fun findSong(mediaItem: MediaItem?): Song? {
        val mediaId = mediaItem?.mediaId ?: return null
        return currentPlaylist.find { it.id == mediaId }
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
        pendingCommands.clear()
        mediaController?.removeListener(playerListener)
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        mediaController = null
    }

    private fun Int.toPlaybackState(): PlaybackState = when (this) {
        Player.STATE_BUFFERING -> PlaybackState.BUFFERING
        Player.STATE_READY -> PlaybackState.READY
        Player.STATE_ENDED -> PlaybackState.ENDED
        else -> PlaybackState.IDLE
    }

    // Player.duration is C.TIME_UNSET (negative) until the media is prepared
    private fun MediaController?.durationOr(fallback: Long): Long {
        val duration = this?.duration ?: return fallback
        return if (duration > 0) duration else fallback
    }

    private companion object {
        const val POSITION_UPDATE_INTERVAL_MS = 500L
    }
}
