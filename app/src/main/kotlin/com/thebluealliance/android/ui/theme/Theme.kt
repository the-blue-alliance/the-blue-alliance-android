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

private val TBABlue = Color(0xFF3F51B5)
private val TBABlueLight = Color(0xFF757DE8)
private val TBABlueDark = Color(0xFF002984)

private val LightColorScheme = lightColorScheme(
    primary = TBABlue,
    onPrimary = Color.White,
    primaryContainer = TBABlueLight,
    secondary = Color(0xFF625B71),
    tertiary = Color(0xFF7D5260),
)

private val DarkColorScheme = darkColorScheme(
    primary = TBABlueLight,
    onPrimary = Color(0xFF00174D),
    primaryContainer = TBABlueDark,
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8),
)

@Composable
fun TBATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
