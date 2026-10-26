package com.ascdev.mediasessionplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ascdev.mediasessionplayer.presentation.ui.MusicPlayerScreen
import com.ascdev.mediasessionplayer.presentation.ui.NowPlayingScreen
import com.ascdev.mediasessionplayer.presentation.viewmodel.MusicPlayerViewModel
import com.ascdev.mediasessionplayer.ui.theme.MediaSessionPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MusicPlayerViewModel by viewModels()
    private var hasPermission by mutableStateOf(false)
    private var isPermissionPermanentlyDenied by mutableStateOf(false)
    private var showNowPlaying by mutableStateOf(false)

    private val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        // After a denial the system stops showing the dialog once no rationale should be shown
        isPermissionPermanentlyDenied =
            !isGranted && !shouldShowRequestPermissionRationale(audioPermission)
        if (isGranted) {
            viewModel.loadSongs()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // The app only has a dark theme, so keep light system bar icons on every device
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            checkAndRequestPermission()
        } else {
            hasPermission = isPermissionGranted()
        }

        setContent {
            MediaSessionPlayerTheme {
                val libraryState by viewModel.libraryState.collectAsStateWithLifecycle()
                val playerState by viewModel.playerState.collectAsStateWithLifecycle()
                val sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
                val scope = rememberCoroutineScope()
                val dismissNowPlaying: () -> Unit = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showNowPlaying = false
                    }
                }

                MusicPlayerScreen(
                    libraryState = libraryState,
                    playerState = playerState,
                    hasPermission = hasPermission,
                    isPermissionPermanentlyDenied = isPermissionPermanentlyDenied,
                    onRequestPermission = ::onRequestPermissionClick,
                    onSongClick = { song -> viewModel.playSong(song) },
                    onPlayPauseClick = { viewModel.playPause() },
                    onSkipNext = { viewModel.skipToNext() },
                    onSkipPrevious = { viewModel.skipToPrevious() },
                    onSeek = { position -> viewModel.seekTo(position) },
                    onExpandPlayer = { showNowPlaying = true }
                )

                // Full screen "Now Playing" bottom sheet, Spotify style
                if (showNowPlaying && playerState.currentSong != null) {
                    ModalBottomSheet(
                        onDismissRequest = { showNowPlaying = false },
                        sheetState = sheetState,
                        modifier = Modifier.fillMaxSize(),
                        dragHandle = null, // handle not visible
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        contentWindowInsets = { WindowInsets(0) },
                        scrimColor = androidx.compose.ui.graphics.Color.Transparent
                    ) {
                        // The sheet lives in its own dialog window, which needs light icons too
                        val view = LocalView.current
                        (view.parent as? DialogWindowProvider)?.window?.let { dialogWindow ->
                            SideEffect {
                                WindowCompat.getInsetsController(dialogWindow, view).apply {
                                    isAppearanceLightStatusBars = false
                                    isAppearanceLightNavigationBars = false
                                }
                            }
                        }

                        NowPlayingScreen(
                            playerState = playerState,
                            onDismiss = dismissNowPlaying,
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

    override fun onResume() {
        super.onResume()
        // The user may have granted the permission from the system settings
        if (!hasPermission && isPermissionGranted()) {
            hasPermission = true
            isPermissionPermanentlyDenied = false
            viewModel.loadSongs()
        }
    }

    private fun isPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(this, audioPermission) ==
            PackageManager.PERMISSION_GRANTED

    private fun checkAndRequestPermission() {
        if (isPermissionGranted()) {
            hasPermission = true
        } else {
            permissionLauncher.launch(audioPermission)
        }
    }

    private fun onRequestPermissionClick() {
        if (isPermissionPermanentlyDenied) {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
            )
        } else {
            permissionLauncher.launch(audioPermission)
        }
    }
}
