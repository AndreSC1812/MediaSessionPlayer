package com.example.mediasessionplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.mediasessionplayer.ui.theme.SpotifyGreen
import com.example.mediasessionplayer.ui.theme.SpotifyBlack
import com.example.mediasessionplayer.ui.theme.SpotifyGreenDark
import com.example.mediasessionplayer.ui.theme.SpotifyWhite
import com.example.mediasessionplayer.ui.theme.SpotifyGreenLight
import com.example.mediasessionplayer.ui.theme.SpotifyMediumGray
import com.example.mediasessionplayer.ui.theme.SpotifyLightText
import com.example.mediasessionplayer.ui.theme.SpotifyPurple
import com.example.mediasessionplayer.ui.theme.SpotifyBlue
import com.example.mediasessionplayer.ui.theme.SpotifyDarkGray
import com.example.mediasessionplayer.ui.theme.SpotifyLightGray
import com.example.mediasessionplayer.ui.theme.SpotifyRed

// Spotify-inspired Dark Color Scheme
private val SpotifyDarkColorScheme = darkColorScheme(
    // Primary colors - Spotify Green
    primary = SpotifyGreen,
    onPrimary = SpotifyBlack,
    primaryContainer = SpotifyGreenDark,
    onPrimaryContainer = SpotifyWhite,

    // Secondary colors
    secondary = SpotifyGreenLight,
    onSecondary = SpotifyBlack,
    secondaryContainer = SpotifyMediumGray,
    onSecondaryContainer = SpotifyLightText,

    // Tertiary colors
    tertiary = SpotifyPurple,
    onTertiary = SpotifyWhite,
    tertiaryContainer = SpotifyBlue,
    onTertiaryContainer = SpotifyWhite,

    // Background
    background = SpotifyBlack,
    onBackground = SpotifyWhite,

    // Surface
    surface = SpotifyDarkGray,
    onSurface = SpotifyWhite,
    surfaceVariant = SpotifyMediumGray,
    onSurfaceVariant = SpotifyLightText,

    // Surface container
    surfaceContainer = SpotifyMediumGray,
    surfaceContainerHigh = SpotifyLightGray,
    surfaceContainerHighest = SpotifyLightGray,

    // Outline
    outline = SpotifyLightGray,
    outlineVariant = SpotifyMediumGray,

    // Error
    error = SpotifyRed,
    onError = SpotifyWhite,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Inverse
    inverseSurface = SpotifyWhite,
    inverseOnSurface = SpotifyBlack,
    inversePrimary = SpotifyGreenDark,
)

@Composable
fun MediaSessionPlayerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = SpotifyDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}