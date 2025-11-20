package com.example.mediasessionplayer.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Spotify-inspired Dark Color Scheme
private val SpotifyDarkColorScheme = darkColorScheme(
    // Primary colors - Spotify Green
    primary = Color(0xFF1DB954),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF38893E),
    onPrimaryContainer = Color(0xFFFFFFFF),

    // Secondary colors
    secondary = Color(0xFF66CC00),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF455A64),
    onSecondaryContainer = Color(0xFFC9E4CA),

    // Tertiary colors
    tertiary = Color(0xFF7C4DFF),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF560BAD),
    onTertiaryContainer = Color(0xFFFFFFFF),

    // Background
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),

    // Surface
    surface = Color(0xFF181818),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF333333),
    onSurfaceVariant = Color(0xFFC9E4CA),

    // Surface container
    surfaceContainer = Color(0xFF333333),
    surfaceContainerHigh = Color(0xFF4F4F4F),
    surfaceContainerHighest = Color(0xFF4F4F4F),

    // Outline
    outline = Color(0xFF808080),
    outlineVariant = Color(0xFF333333),

    // Error
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Inverse
    inverseSurface = Color(0xFFFFFFFF),
    inverseOnSurface = Color(0xFF000000),
    inversePrimary = Color(0xFF38893E),
)

@Composable
fun MediaSessionPlayerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = SpotifyDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}