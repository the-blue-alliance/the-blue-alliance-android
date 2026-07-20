package com.thebluealliance.android.wear.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

/**
 * TBA brand theme for Wear. Wear renders dark-only, so this is the STYLE_GUIDE.md
 * dark-scheme mapping; other roles stay at the Wear M3 defaults. Dynamic color
 * (Material You) is disabled repo-wide — the app always uses TBA brand colors.
 */
private val TbaWearColorScheme =
    ColorScheme(
        primary = Color(0xFF9FA8DA), // Indigo 200
        primaryDim = Color(0xFF7986CB), // Indigo 300
        onPrimary = Color(0xFF00174D),
        primaryContainer = Color(0xFF303F9F), // Indigo 700
        onPrimaryContainer = Color(0xFFC5CAE9), // Indigo 100
    )

@Composable
fun TbaWearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TbaWearColorScheme,
        content = content,
    )
}
