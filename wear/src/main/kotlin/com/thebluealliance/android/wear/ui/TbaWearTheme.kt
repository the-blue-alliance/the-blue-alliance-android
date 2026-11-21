package com.thebluealliance.android.wear.ui

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import com.thebluealliance.android.core.theme.TbaPaletteTokens

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
