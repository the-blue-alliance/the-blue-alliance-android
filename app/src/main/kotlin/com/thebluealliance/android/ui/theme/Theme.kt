package com.thebluealliance.android.ui.theme

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

// Indigo tonal scale — TBA brand palette
val TBABlue = Color(0xFF3F51B5)           // Indigo 500 — canonical TBA brand color
private val TBABlueDark = Color(0xFF303F9F)    // Indigo 700 — dark-mode container
private val TBABlueLight = Color(0xFF9FA8DA)   // Indigo 200 — dark-mode primary
private val TBAPastelBlue = Color(0xFFC5CAE9)  // Indigo 100 — light-mode primaryContainer
private val TBAIndigo900 = Color(0xFF1A237E)   // Indigo 900 — onPrimaryContainer

private val LightColorScheme = lightColorScheme(
    primary = TBABlue,
    onPrimary = Color.White,
    primaryContainer = TBAPastelBlue,
    onPrimaryContainer = TBAIndigo900,
    surfaceTint = TBABlue,
)

private val DarkColorScheme = darkColorScheme(
    primary = TBABlueLight,
    onPrimary = Color(0xFF00174D),
    primaryContainer = TBABlueDark,
    onPrimaryContainer = TBAPastelBlue,
    surfaceTint = TBABlueLight,
)

@Composable
fun TBATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        content = content,
    )
}
