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

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = CharcoalElevated,
  onPrimaryContainer = TextPrimaryDark,
  secondary = AccentTeal,
  onSecondary = Color.White,
  secondaryContainer = CharcoalElevated,
  onSecondaryContainer = TextPrimaryDark,
  tertiary = AccentEmerald,
  background = CharcoalBackground,
  surface = CharcoalSurface,
  surfaceVariant = CharcoalElevated,
  surfaceContainer = CharcoalElevated,
  surfaceContainerHigh = CharcoalBorder,
  onBackground = TextPrimaryDark,
  onSurface = TextPrimaryDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = CharcoalBorder,
  outlineVariant = CharcoalSubtle
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFEFF6FF),
  onPrimaryContainer = PrimaryBlueDark,
  secondary = AccentTeal,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFCCFBF1),
  onSecondaryContainer = Color(0xFF115E59),
  tertiary = AccentEmerald,
  background = Slate50,
  surface = Color.White,
  surfaceVariant = Slate100,
  onBackground = Slate900,
  onSurface = Slate900,
  onSurfaceVariant = Slate600,
  outline = Slate300
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use intentional branded palette by default
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

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

