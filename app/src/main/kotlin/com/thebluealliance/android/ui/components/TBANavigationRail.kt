package com.thebluealliance.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.R
import com.thebluealliance.android.core.theme.TbaBrandColors
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS
import com.thebluealliance.android.ui.theme.OnBrand

// The header's brand circle, inside its 56dp slot.
private val RailLampCircleSize = 40.dp

/** True while [TBANavigationRail] shows beside the screen. */
val LocalNavigationRailShown = staticCompositionLocalOf { false }

/**
 * Side-rail counterpart of [TBABottomBar] for medium and expanded window widths. Unlike the bar it
 * also shows on detail screens, where it marks [stackRoot], the tab the screen was opened from.
 *
 * It runs full height below the status strip, with the TBA lamp in its header in place of the top
 * app bar's.
 */
@Composable
fun TBANavigationRail(
    modifier: Modifier = Modifier,
    currentRoute: NavKey,
    stackRoot: NavKey,
    onTopLevelScreen: Boolean,
    onNavigate: (NavKey) -> Unit,
    onReselect: () -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        // The top container step, apart from both the page and its margins.
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        windowInsets =
            WindowInsets.safeDrawing.only(
                WindowInsetsSides.Start + WindowInsetsSides.Top + WindowInsetsSides.Bottom,
            ),
        header = { RailLamp() },
    ) {
        // Scrolls only when the window is too short for every item.
        Column(
            modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TOP_LEVEL_DESTINATIONS.forEach { dest ->
                val selected = dest.isRailSelectedFor(currentRoute, stackRoot, onTopLevelScreen)
                NavigationRailItem(
                    selected = selected,
                    onClick =
                        topLevelDestinationClick(
                            dest,
                            currentRoute,
                            onNavigate,
                            onReselect,
                        ),
                    icon = { TopLevelDestinationIcon(dest, selected) },
                    label = { Text(dest.label) },
                    // Brand blue marks the selected item.
                    colors =
                        NavigationRailItemDefaults.colors(
                            selectedIconColor = OnBrand,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                        ),
                    modifier = Modifier.handCursor(),
                )
            }
        }
    }
}

/**
 * The launcher glyph: a white lamp on a brand-blue circle in both modes, since a blue lamp alone
 * falls under 3:1 on the dark rail. The lamp spans 64% of the circle, as in the adaptive icon. A
 * plain Box keeps it out of the semantics tree.
 */
@Composable
private fun RailLamp() {
    Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier
                    .size(RailLampCircleSize)
                    .background(TbaBrandColors.Blue, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.tba_lamp),
                contentDescription = null,
                modifier = Modifier.size(RailLampCircleSize * 0.64f),
                tint = TbaBrandColors.OnBlue,
            )
        }
    }
}
