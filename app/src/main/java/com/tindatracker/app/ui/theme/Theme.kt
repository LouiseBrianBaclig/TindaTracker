package com.tindatracker.app.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TindaColorScheme = lightColorScheme(
    primary              = GreenPrimary,
    onPrimary            = Color.White,
    primaryContainer     = GreenContainer,
    onPrimaryContainer   = OnGreenContainer,
    secondary            = AmberPrimary,
    onSecondary          = Color.White,
    secondaryContainer   = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    tertiary             = Color(0xFF1565C0),
    onTertiary           = Color.White,
    error                = SaleRed,
    onError              = Color.White,
    background           = SurfaceBackground,
    onBackground         = Color(0xFF1A1C1E),
    surface              = CardSurface,
    onSurface            = Color(0xFF1A1C1E),
    surfaceVariant       = Color(0xFFDCEDE4),
    onSurfaceVariant     = Color(0xFF3E4A42),
    outline              = Color(0xFF6E8A76),
    outlineVariant       = DividerColor
)

@Composable
fun TindaTrackerTheme(content: @Composable () -> Unit) {
    val colorScheme = TindaColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
