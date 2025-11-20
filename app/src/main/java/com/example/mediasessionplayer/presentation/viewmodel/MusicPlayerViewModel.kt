package com.example.mediasessionplayer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediasessionplayer.domain.model.PlayerState
import com.example.mediasessionplayer.domain.model.Song
import com.example.mediasessionplayer.domain.usecase.ControlPlaybackUseCase
import com.example.mediasessionplayer.domain.usecase.GetPlayerStateUseCase
import com.example.mediasessionplayer.domain.usecase.GetSongsUseCase
import com.example.mediasessionplayer.domain.usecase.PlaySongUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val playSongUseCase: PlaySongUseCase,
    private val controlPlaybackUseCase: ControlPlaybackUseCase,
    getPlayerStateUseCase: GetPlayerStateUseCase,
    private val musicRepository: com.example.mediasessionplayer.domain.repository.MusicRepository
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    val playerState: StateFlow<PlayerState> = getPlayerStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerState()
        )

    init {
        musicRepository.observeLibraryChanges {
            loadSongs()
        }
        loadSongs()
    }

    fun loadSongs() {
        viewModelScope.launch {
            _songs.value = getSongsUseCase()
        }
    }

    fun playSong(song: Song) {
        val currentSongs = _songs.value
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

    override fun onCleared() {
        super.onCleared()
        musicRepository.removeLibraryObserver()
    }
}
