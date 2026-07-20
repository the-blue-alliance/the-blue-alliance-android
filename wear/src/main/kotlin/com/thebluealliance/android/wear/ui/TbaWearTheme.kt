package com.thebluealliance.android.wear.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

/**
 * TBA brand + alliance colors — a single source shared by the theme and the tracker UI, so the
 * palette isn't duplicated across files.
 */
object TbaColors {
    // Alliance accents used in match sections (light fill / strong avatar backdrop).
    val AllianceRed = Color(0xFFF2B8B5)
    val AllianceBlue = Color(0xFF9FA8DA) // same indigo as the brand primary
    val AllianceRedBg = Color(0xFFC62828) // Red 800
    val AllianceBlueBg = Color(0xFF1565C0) // Blue 800

    /** Avatar backdrop when the tracked alliance is unknown — a neutral, not an arbitrary color. */
    val AllianceNeutralBg = Color(0xFF424242)
}

/**
 * TBA brand theme for Wear. Wear renders dark-only, so this is the STYLE_GUIDE.md dark-scheme
 * mapping. Beyond the primary (indigo) family — which fills the EdgeButton — the secondary/tertiary
 * accents and the surface-container family are brand-tinted so the components that draw from
 * surface roles (FilledTonalButton and the config text field) carry the TBA indigo instead of a
 * neutral grey. Dynamic color (Material You) is disabled repo-wide.
 */
private val TbaWearColorScheme =
    ColorScheme(
        primary = Color(0xFF9FA8DA), // Indigo 200
        primaryDim = Color(0xFF7986CB), // Indigo 300
        onPrimary = Color(0xFF00174D),
        primaryContainer = Color(0xFF303F9F), // Indigo 700
        onPrimaryContainer = Color(0xFFC5CAE9), // Indigo 100
        // Secondary: muted indigo accent for tonal surfaces.
        secondary = Color(0xFFB4B9E0),
        secondaryDim = Color(0xFF8C93C4),
        secondaryContainer = Color(0xFF283593), // Indigo 800
        onSecondaryContainer = Color(0xFFC5CAE9), // Indigo 100
        // Tertiary: TBA blue, complementary to the indigo primary.
        tertiary = Color(0xFF90CAF9), // Blue 200
        tertiaryDim = Color(0xFF64B5F6), // Blue 300
        onTertiary = Color(0xFF002E5C),
        tertiaryContainer = Color(0xFF1565C0), // Blue 800
        onTertiaryContainer = Color(0xFFE3EEFF),
        // Brand-tinted dark surfaces so buttons/fields read as indigo, not grey.
        surfaceContainerLow = Color(0xFF15161F),
        surfaceContainer = Color(0xFF232538),
        surfaceContainerHigh = Color(0xFF2E3149),
    )

@Composable
fun TbaWearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TbaWearColorScheme,
        content = content,
    )
}
