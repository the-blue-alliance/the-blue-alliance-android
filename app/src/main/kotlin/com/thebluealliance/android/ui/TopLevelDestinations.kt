package com.thebluealliance.android.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.navigation.Screen

data class TopLevelDestination(
    val key: NavKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(Screen.Events, "Events", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    TopLevelDestination(Screen.Teams, "Teams", Icons.Filled.Groups, Icons.Outlined.Groups),
    TopLevelDestination(Screen.Districts, "Districts", Icons.Filled.Map, Icons.Outlined.Map),
    TopLevelDestination(Screen.More, "More", Icons.Filled.Menu, Icons.Outlined.Menu),
)
