package com.thebluealliance.android.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

// The full Material 3 dark scheme generated from the TBA blue seed (#3F51B5), tonal spot,
// as Material Theme Builder would produce it. Every role is set, so nothing falls back to baseline.
private val TbaColorScheme =
    darkColorScheme(
        primary = Color(0xFFBAC3FF),
        onPrimary = Color(0xFF222C61),
        primaryContainer = Color(0xFF394379),
        onPrimaryContainer = Color(0xFFDEE0FF),
        inversePrimary = Color(0xFF515B92),
        secondary = Color(0xFFC3C5DD),
        onSecondary = Color(0xFF2D2F42),
        secondaryContainer = Color(0xFF434659),
        onSecondaryContainer = Color(0xFFE0E1F9),
        tertiary = Color(0xFFE6BAD7),
        onTertiary = Color(0xFF44263D),
        tertiaryContainer = Color(0xFF5D3C55),
        onTertiaryContainer = Color(0xFFFFD7F1),
        background = Color(0xFF121318),
        onBackground = Color(0xFFE4E1E9),
        surface = Color(0xFF121318),
        onSurface = Color(0xFFE4E1E9),
        surfaceVariant = Color(0xFF46464F),
        onSurfaceVariant = Color(0xFFC7C5D0),
        surfaceTint = Color(0xFFBAC3FF),
        inverseSurface = Color(0xFFE4E1E9),
        inverseOnSurface = Color(0xFF303036),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        border = Color(0xFF90909A),
        borderVariant = Color(0xFF46464F),
        scrim = Color(0xFF000000),
    )

@Composable
fun TbaTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TbaColorScheme,
        typography = TbaTypography,
        content = content,
    )
}
