package com.thebluealliance.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS

/**
 * Side-rail counterpart of [TBABottomBar] for medium and expanded window widths. Unlike the bar it
 * also shows on detail screens, where it marks [stackRoot], the tab the screen was opened from.
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
    // The strip behind the status bar matches the brand-blue top app bars, which the
    // light status-bar icons are styled for.
    Box(modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
        NavigationRail(
            modifier =
                Modifier.windowInsetsPadding(
                    WindowInsets.statusBars.only(WindowInsetsSides.Top),
                ),
            windowInsets =
                WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Bottom),
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
                    )
                }
            }
        }
    }
}
