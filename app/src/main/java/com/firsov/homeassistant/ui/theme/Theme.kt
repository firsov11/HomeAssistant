package com.firsov.homeassistant.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Основные цветовые схемы
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    primaryContainer = Color(0xFFFF0000),
    secondaryContainer = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFD5E3D5),
    onSurface = Color(0xFFE6F1E6)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary,
    primaryContainer = Color(0xFFFF0000),
    secondaryContainer = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun HomeAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extraColors = if (darkTheme) {
        ExtraColors(
            ventContainer = Color(0xFF008CDC),
            darkGrayBackground = Color(0xFF2C2C2C),
            lightGrayBackground = Color(0xFFF0F0F0)
        )
    } else {
        ExtraColors(
            ventContainer = Color(0xFF008CDC),
            darkGrayBackground = Color(0xFFBBBBBB),  // светло-серый для светлой темы
            lightGrayBackground = Color(0xFFF0F0F0)
        )
    }

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
