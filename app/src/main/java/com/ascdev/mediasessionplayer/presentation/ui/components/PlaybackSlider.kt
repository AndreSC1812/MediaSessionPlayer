package com.ascdev.mediasessionplayer.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Seek bar that follows the playback position, except while the user is dragging it,
 * so periodic position updates don't make the thumb jump under the user's finger.
 */
@Composable
fun PlaybackSlider(
    position: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    showTimestamps: Boolean = false
) {
    var dragPosition by remember { mutableStateOf<Float?>(null) }
    val maxValue = duration.toFloat().coerceAtLeast(1f)
    val displayedPosition = (dragPosition ?: position.toFloat()).coerceIn(0f, maxValue)

    Column(modifier = modifier) {
        Slider(
            value = displayedPosition,
            onValueChange = { dragPosition = it },
            onValueChangeFinished = {
                dragPosition?.let { onSeek(it.toLong()) }
                dragPosition = null
            },
            valueRange = 0f..maxValue,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        )

        if (showTimestamps) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(displayedPosition.toLong()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatDuration(millis: Long): String {
    val totalSeconds = millis.coerceAtLeast(0L) / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
