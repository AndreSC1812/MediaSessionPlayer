package com.ascdev.mediasessionplayer.presentation.viewmodel

import com.ascdev.mediasessionplayer.domain.model.Song

sealed interface LibraryState {
    data object Loading : LibraryState
    data class Loaded(val songs: List<Song>) : LibraryState
}
