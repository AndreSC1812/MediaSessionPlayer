package com.ascdev.mediasessionplayer.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Album cover with a music note placeholder that stays visible
 * while loading or when the song has no embedded artwork.
 */
@Composable
fun AlbumArt(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 4.dp,
    placeholderIconSize: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(placeholderIconSize),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
