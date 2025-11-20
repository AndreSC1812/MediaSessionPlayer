package com.example.mediasessionplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.mediasessionplayer.presentation.ui.MusicPlayerScreen
import com.example.mediasessionplayer.presentation.ui.NowPlayingScreen
import com.example.mediasessionplayer.presentation.viewmodel.MusicPlayerViewModel
import com.example.mediasessionplayer.ui.theme.MediaSessionPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MusicPlayerViewModel by viewModels()
    private var hasPermission by mutableStateOf(false)
    private var showNowPlaying by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.loadSongs()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkAndRequestPermission()

        setContent {
            MediaSessionPlayerTheme {
                val songs by viewModel.songs.collectAsState()
                val playerState by viewModel.playerState.collectAsState()
                val sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
                val scope = rememberCoroutineScope()

                MusicPlayerScreen(
                    songs = songs,
                    playerState = playerState,
                    onSongClick = { song -> viewModel.playSong(song) },
                    onPlayPauseClick = { viewModel.playPause() },
                    onSkipNext = { viewModel.skipToNext() },
                    onSkipPrevious = { viewModel.skipToPrevious() },
                    onSeek = { position -> viewModel.seekTo(position) },
                    onExpandPlayer = {
                        scope.launch {
                            sheetState.show()
                            showNowPlaying = true
                        }
                    }
                )

                // Bottom Sheet for Now Playing like spotify
                if (showNowPlaying && playerState.currentSong != null) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            scope.launch {
                                sheetState.hide()
                                showNowPlaying = false
                            }
                        },
                        sheetState = sheetState,
                        modifier = Modifier.fillMaxSize(),
                        dragHandle = null, // handle not visible
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        scrimColor = androidx.compose.ui.graphics.Color.Transparent
                    ) {
                        NowPlayingScreen(
                            playerState = playerState,
                            onDismiss = {
                                scope.launch {
                                    sheetState.hide()
                                    showNowPlaying = false
                                }
                            },
                            onPlayPauseClick = { viewModel.playPause() },
                            onSkipNext = { viewModel.skipToNext() },
                            onSkipPrevious = { viewModel.skipToPrevious() },
                            onSeek = { position -> viewModel.seekTo(position) }
                        )
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                hasPermission = true
                viewModel.loadSongs()
            }

            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }
}