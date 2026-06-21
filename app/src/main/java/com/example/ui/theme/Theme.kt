package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GlowBlueAccent,
    secondary = GlowCyanAccent,
    tertiary = GlowSilverAccent,
    background = GlowBlack,
    surface = GlowDarkCard,
    onPrimary = GlowBlack,
    onSecondary = GlowBlack,
    onBackground = GlowSilverAccent,
    onSurface = GlowSilverAccent
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force Dark mode for GlowUpX 2026 luxury operating system
  dynamicColor: Boolean = false, // Force brand colors
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
