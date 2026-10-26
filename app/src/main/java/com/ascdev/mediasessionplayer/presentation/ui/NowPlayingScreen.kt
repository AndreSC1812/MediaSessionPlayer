package com.ascdev.mediasessionplayer.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ascdev.mediasessionplayer.R
import com.ascdev.mediasessionplayer.domain.model.PlayerState
import com.ascdev.mediasessionplayer.presentation.ui.components.AlbumArt
import com.ascdev.mediasessionplayer.presentation.ui.components.PlaybackSlider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    playerState: PlayerState,
    onDismiss: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val song = playerState.currentSong ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.cd_close_player),
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Album art
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            AlbumArt(
                model = song.imageUrl,
                contentDescription = stringResource(R.string.cd_album_art),
                modifier = Modifier.fillMaxSize(),
                cornerRadius = 8.dp,
                placeholderIconSize = 96.dp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Song info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progress Bar
        PlaybackSlider(
            position = playerState.currentPosition,
            duration = playerState.duration,
            onSeek = onSeek,
            showTimestamps = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onSkipPrevious,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = stringResource(R.string.cd_previous),
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            FilledIconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = if (playerState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = stringResource(if (playerState.isPlaying) R.string.cd_pause else R.string.cd_play),
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(
                onClick = onSkipNext,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = stringResource(R.string.cd_next),
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
