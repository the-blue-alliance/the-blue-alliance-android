package com.thebluealliance.android.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val TbaColorScheme =
    darkColorScheme(
        primary = TbaBlue,
        onPrimary = TextPrimary,
        secondary = TbaBlueBright,
        onSecondary = TextPrimary,
        background = TbaBackground,
        onBackground = TextPrimary,
        surface = TbaSurface,
        onSurface = TextPrimary,
        surfaceVariant = TbaSurfaceVariant,
        onSurfaceVariant = TextSecondary,
    )

@Composable
fun TbaTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TbaColorScheme,
        typography = TbaTypography,
        content = content,
    )
}
