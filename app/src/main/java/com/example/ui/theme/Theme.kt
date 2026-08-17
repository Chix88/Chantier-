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
    primary = VibrantBlueLight,
    onPrimary = Color(0xFF003258),
    primaryContainer = VibrantBlueDark,
    onPrimaryContainer = VibrantBlueContainer,
    secondary = VibrantPurpleLight,
    onSecondary = Color.White,
    background = SlateNavyDark,
    surface = SlateNavyCard,
    surfaceVariant = SlateNavyBorder,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = SlateNavyLight,
    outline = SlateNavyBorder
)

private val LightColorScheme = lightColorScheme(
    primary = VibrantBluePrimary,
    onPrimary = Color.White,
    primaryContainer = VibrantBlueContainer,
    onPrimaryContainer = OnVibrantBlueContainer,
    secondary = VibrantPurpleLight,
    onSecondary = Color.White,
    secondaryContainer = VibrantPurpleContainer,
    onSecondaryContainer = VibrantPurpleDeep,
    tertiary = VibrantOrangePrimary,
    onTertiary = Color.White,
    tertiaryContainer = VibrantOrangeContainer,
    onTertiaryContainer = OnVibrantOrangeContainer,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = OutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
