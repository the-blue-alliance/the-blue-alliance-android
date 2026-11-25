package com.thebluealliance.android.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.navigation.isSameTab
import com.thebluealliance.android.ui.TopLevelDestination

private val MORE_SUB_SCREENS: Set<NavKey> =
    setOf(Screen.MyTBA, Screen.RegionalAdvancement, Screen.Settings, Screen.About, Screen.Thanks)

/** Whether [this] tab is highlighted while [currentRoute] is showing. */
internal fun TopLevelDestination.isSelectedFor(currentRoute: NavKey): Boolean =
    key.isSameTab(currentRoute) || (key == Screen.More && currentRoute in MORE_SUB_SCREENS)

/**
 * Top-level navigation moves from the bottom bar to a side rail once the window is not compact in
 * width, or is too short for a bottom bar to leave room for content.
 */
internal fun useNavigationRail(windowSizeClass: WindowSizeClass): Boolean =
    windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
        !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)

/** Tapping the current tab reselects it; any other tab navigates there. */
@Composable
internal fun topLevelDestinationClick(
    dest: TopLevelDestination,
    currentRoute: NavKey,
    onNavigate: (NavKey) -> Unit,
    onReselect: () -> Unit,
): () -> Unit =
    dropUnlessResumed {
        if (dest.key.isSameTab(currentRoute)) {
            onReselect()
        } else {
            onNavigate(dest.key)
        }
    }

@Composable
internal fun TopLevelDestinationIcon(
    dest: TopLevelDestination,
    selected: Boolean,
) {
    Icon(
        imageVector =
            ImageVector.vectorResource(
                if (selected) dest.selectedIcon else dest.unselectedIcon,
            ),
        // The item label already names the destination.
        contentDescription = null,
    )
}
