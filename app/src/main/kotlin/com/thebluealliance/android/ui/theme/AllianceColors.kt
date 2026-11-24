package com.thebluealliance.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.thebluealliance.android.core.theme.TbaAllianceColors

/** FRC alliance custom colors for the current light/dark mode; see [TbaAllianceColors]. */
@Immutable
data class AllianceColors(
    val red: Color,
    val onRed: Color,
    val redContainer: Color,
    val onRedContainer: Color,
    val blue: Color,
    val onBlue: Color,
    val blueContainer: Color,
    val onBlueContainer: Color,
)

internal val LightAllianceColors =
    with(TbaAllianceColors.Light) {
        AllianceColors(
            Red,
            OnRed,
            RedContainer,
            OnRedContainer,
            Blue,
            OnBlue,
            BlueContainer,
            OnBlueContainer,
        )
    }

internal val DarkAllianceColors =
    with(TbaAllianceColors.Dark) {
        AllianceColors(
            Red,
            OnRed,
            RedContainer,
            OnRedContainer,
            Blue,
            OnBlue,
            BlueContainer,
            OnBlueContainer,
        )
    }

internal val LocalAllianceColors = staticCompositionLocalOf { LightAllianceColors }

/** Alliance colors provided by [TBATheme], read like a color-scheme role. */
@Suppress("UnusedReceiverParameter")
val MaterialTheme.allianceColors: AllianceColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAllianceColors.current
