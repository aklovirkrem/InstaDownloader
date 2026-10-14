package com.instagrab.app.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Colors ───────────────────────────────────────────────────
val Purple80 = Color(0xFFE040FB)
val Violet80 = Color(0xFF7C4DFF)
val Cyan80   = Color(0xFF00E5FF)

val BgDark      = Color(0xFF0A0A0F)
val SurfaceDark = Color(0xFF111118)
val Surface2    = Color(0xFF1A1A24)
val BorderDark  = Color(0x18FFFFFF)
val MutedText   = Color(0xFF7070A0)
val SuccessColor= Color(0xFF00E676)
val ErrorColor  = Color(0xFFFF5252)

private val DarkColorScheme = darkColorScheme(
    primary          = Purple80,
    secondary        = Violet80,
    tertiary         = Cyan80,
    background       = BgDark,
    surface          = SurfaceDark,
    surfaceVariant   = Surface2,
    onPrimary        = Color.White,
    onBackground     = Color(0xFFF0F0F8),
    onSurface        = Color(0xFFF0F0F8),
    onSurfaceVariant = MutedText,
    outline          = BorderDark,
    error            = ErrorColor,
)

@Composable
fun InstaGrabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
