package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldPastel,
    secondary = EmeraldDarkSecondary,
    tertiary = AccentGold,
    background = DeepCharcoal,
    surface = SurfaceDark,
    onPrimary = DeepCharcoal,
    onSecondary = TextDarkOnLight,
    onBackground = TextDarkOnLight,
    onSurface = TextDarkOnLight
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldGreen,
    secondary = EmeraldLight,
    tertiary = AccentGold,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = LuxuryCharcoal,
    onBackground = LuxuryCharcoal,
    onSurface = LuxuryCharcoal
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set to false to prioritize our signature luxury brand colors over wallpaper-based colors
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
