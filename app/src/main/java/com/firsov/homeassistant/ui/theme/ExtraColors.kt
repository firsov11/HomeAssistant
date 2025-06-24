package com.firsov.homeassistant.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtraColors(
    val ventContainer: Color
)

val LocalExtraColors = staticCompositionLocalOf<ExtraColors> {
    error("No ExtraColors provided")
}
