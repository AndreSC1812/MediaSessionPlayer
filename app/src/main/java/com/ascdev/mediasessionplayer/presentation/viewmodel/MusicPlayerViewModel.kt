package com.ascdev.mediasessionplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ascdev.mediasessionplayer.domain.model.PlayerState
import com.ascdev.mediasessionplayer.domain.model.Song
import com.ascdev.mediasessionplayer.domain.usecase.ControlPlaybackUseCase
import com.ascdev.mediasessionplayer.domain.usecase.GetPlayerStateUseCase
import com.ascdev.mediasessionplayer.domain.usecase.GetSongsUseCase
import com.ascdev.mediasessionplayer.domain.usecase.ObserveLibraryChangesUseCase
import com.ascdev.mediasessionplayer.domain.usecase.PlaySongUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val playSongUseCase: PlaySongUseCase,
    private val controlPlaybackUseCase: ControlPlaybackUseCase,
    getPlayerStateUseCase: GetPlayerStateUseCase,
    observeLibraryChangesUseCase: ObserveLibraryChangesUseCase
) : ViewModel() {

    private val reloadRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // Reloads on demand (e.g. after the permission is granted) and whenever MediaStore changes.
    // MediaStore fires several change events per file, so they are debounced into a single query.
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val songs: StateFlow<List<Song>> = merge(reloadRequests, observeLibraryChangesUseCase())
        .onStart { emit(Unit) }
        .debounce(LIBRARY_RELOAD_DEBOUNCE_MS)
        .mapLatest { getSongsUseCase() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val playerState: StateFlow<PlayerState> = getPlayerStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerState()
        )

    fun loadSongs() {
        reloadRequests.tryEmit(Unit)
    }

    fun playSong(song: Song) {
        val currentSongs = songs.value
        val index = currentSongs.indexOf(song)
        if (index != -1) {
            // Set the entire playlist and start from the selected song
            controlPlaybackUseCase.setPlaylist(currentSongs, index)
        } else {
            // Fallback to playing just the single song
            playSongUseCase(song)
        }
    }

    fun playPause() {
        if (playerState.value.isPlaying) {
            controlPlaybackUseCase.pause()
        } else {
            controlPlaybackUseCase.play()
        }
    }

    fun seekTo(position: Long) {
        controlPlaybackUseCase.seekTo(position)
    }

    fun skipToNext() {
        controlPlaybackUseCase.skipToNext()
    }

    fun skipToPrevious() {
        controlPlaybackUseCase.skipToPrevious()
    }

    private companion object {
        const val LIBRARY_RELOAD_DEBOUNCE_MS = 300L
    }
}
