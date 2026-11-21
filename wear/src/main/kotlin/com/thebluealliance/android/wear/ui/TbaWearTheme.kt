package com.thebluealliance.android.wear.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.thebluealliance.android.core.theme.TbaPaletteTokens

/**
 * Alliance game colors for the tracker UI. Fixed on purpose: red and blue are the FRC alliances,
 * not theme roles, so they don't follow the TBA scheme.
 */
object TbaColors {
    // Alliance accents used in match sections (light fill / strong avatar backdrop).
    val AllianceRed = Color(0xFFF2B8B5)
    val AllianceBlue = Color(0xFF9FA8DA) // Indigo 200
    val AllianceRedBg = Color(0xFFC62828) // Red 800
    val AllianceBlueBg = Color(0xFF1565C0) // Blue 800

    /** Avatar backdrop when the tracked alliance is unknown — a neutral, not an arbitrary color. */
    val AllianceNeutralBg = Color(0xFF424242)
}

/**
 * The shared TBA color scheme (`:core-theme`) on Wear. Every role uses the same palette tone as
 * wear-compose-material3's default scheme (its `ColorTokens`), so only the palette differs.
 * Wear is dark-only, and dynamic color (Material You) is disabled repo-wide.
 */
private val TbaWearColorScheme =
    with(TbaPaletteTokens) {
        ColorScheme(
            primary = Primary90,
            primaryDim = Primary80,
            primaryContainer = Primary30,
            onPrimary = Primary10,
            onPrimaryContainer = Primary95,
            secondary = Secondary90,
            secondaryDim = Secondary80,
            secondaryContainer = Secondary30,
            onSecondary = Secondary10,
            onSecondaryContainer = Secondary95,
            tertiary = Tertiary90,
            tertiaryDim = Tertiary80,
            tertiaryContainer = Tertiary30,
            onTertiary = Tertiary10,
            onTertiaryContainer = Tertiary95,
            surfaceContainerLow = Neutral15,
            surfaceContainer = Neutral20,
            surfaceContainerHigh = Neutral30,
            onSurface = Neutral95,
            onSurfaceVariant = NeutralVariant80,
            outline = NeutralVariant60,
            outlineVariant = NeutralVariant40,
            background = Neutral0,
            onBackground = Neutral100,
            error = Error80,
            errorDim = Error70,
            errorContainer = Error30,
            onError = Error10,
            onErrorContainer = Error95,
        )
    }

@Composable
fun TbaWearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TbaWearColorScheme,
        content = content,
    )
}
