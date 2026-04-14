package com.thebluealliance.android.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.navigation.Screen

data class TopLevelDestination(
    val key: NavKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val MORE_SUB_SCREENS =
    setOf(Screen.MyTBA, Screen.Settings, Screen.About, Screen.Thanks, Screen.RegionalAdvancement)

fun isDestinationSelected(
    dest: TopLevelDestination,
    currentRoute: NavKey,
): Boolean =
    currentRoute == dest.key || (dest.key == Screen.More && currentRoute in MORE_SUB_SCREENS)

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            Screen.Events,
            "Events",
            Icons.Filled.CalendarMonth,
            Icons.Outlined.CalendarMonth,
        ),
        TopLevelDestination(Screen.Teams, "Teams", Icons.Filled.Groups, Icons.Outlined.Groups),
        TopLevelDestination(Screen.Districts, "Districts", Icons.Filled.Map, Icons.Outlined.Map),
        TopLevelDestination(Screen.More, "More", Icons.Filled.Menu, Icons.Outlined.Menu),
    )

val RAIL_PRIMARY_DESTINATIONS =
    listOf(
        TopLevelDestination(
            Screen.Events,
            "Events",
            Icons.Filled.CalendarMonth,
            Icons.Outlined.CalendarMonth,
        ),
        TopLevelDestination(Screen.Teams, "Teams", Icons.Filled.Groups, Icons.Outlined.Groups),
        TopLevelDestination(Screen.Districts, "Districts", Icons.Filled.Map, Icons.Outlined.Map),
        TopLevelDestination(Screen.MyTBA, "myTBA", Icons.Filled.Star, Icons.Outlined.StarOutline),
    )

val RAIL_SECONDARY_DESTINATIONS =
    listOf(
        TopLevelDestination(Screen.About, "About", Icons.Filled.Info, Icons.Outlined.Info),
        TopLevelDestination(Screen.Thanks, "Thanks", Icons.Filled.Info, Icons.Outlined.Info),
    )

val RAIL_BOTTOM_DESTINATIONS =
    listOf(
        TopLevelDestination(
            Screen.Settings,
            "Settings",
            Icons.Filled.Settings,
            Icons.Outlined.Settings,
        ),
    )
