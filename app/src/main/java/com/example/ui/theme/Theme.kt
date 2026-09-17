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
    primary = ElectricYellow,
    onPrimary = Color(0xFF221A00),
    primaryContainer = Color(0xFF423500),
    onPrimaryContainer = ElectricYellow,
    secondary = ElectricCyan,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF004F58),
    onSecondaryContainer = ElectricCyan,
    tertiary = NeonGreen,
    onTertiary = Color(0xFF003915),
    tertiaryContainer = Color(0xFF005322),
    onTertiaryContainer = NeonGreen,
    background = DarkNavyBackground,
    onBackground = DarkNavyOnBackground,
    surface = DarkNavySurface,
    onSurface = DarkNavyOnSurface,
    surfaceVariant = DarkNavySurfaceVariant,
    onSurfaceVariant = Color(0xFFC4D0E0),
    surfaceContainer = DarkNavySurfaceContainer,
    error = DangerRed,
    onError = Color.White,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ElectricYellowDark,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFFF1AA),
    onPrimaryContainer = Color(0xFF4E3E00),
    secondary = Color(0xFF006874),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF9EEFFD),
    onSecondaryContainer = Color(0xFF001F24),
    tertiary = Color(0xFF006D30),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF9BF6AA),
    onTertiaryContainer = Color(0xFF00210A),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF454E5A),
    surfaceContainer = LightSurfaceContainer,
    error = PowerOffRed,
    onError = Color.White,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep intentional electric branding by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

