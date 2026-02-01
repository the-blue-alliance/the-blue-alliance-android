package com.thebluealliance.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.thebluealliance.android.MainActivity
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thebluealliance.android.navigation.Route
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.navigation.TBANavHost
import com.thebluealliance.android.ui.theme.TBATheme

data class TopLevelDestination(
    val route: Route,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(Route.Events, "Events", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
    TopLevelDestination(Route.Teams, "Teams", Icons.Filled.Groups, Icons.Outlined.Groups),
    TopLevelDestination(Route.Districts, "Districts", Icons.Filled.Map, Icons.Outlined.Map),
    TopLevelDestination(Route.MyTBA, "myTBA", Icons.Filled.Star, Icons.Outlined.StarBorder),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TBAApp(activity: MainActivity? = null) {
    TBATheme {
        val navController = rememberNavController()
        activity?.setNavController(navController)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val showBottomBar = TOP_LEVEL_DESTINATIONS.any { dest ->
            currentDestination?.hasRoute(dest.route::class) == true
        }

        Scaffold(
            topBar = {
                if (showBottomBar) {
                    TopAppBar(
                        title = {
                            val dest = TOP_LEVEL_DESTINATIONS.firstOrNull { dest ->
                                currentDestination?.hasRoute(dest.route::class) == true
                            }
                            Text(dest?.label ?: "")
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.Search) }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        },
                    )
                }
            },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        TOP_LEVEL_DESTINATIONS.forEach { dest ->
                            val selected = currentDestination?.hasRoute(dest.route::class) == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label,
                                    )
                                },
                                label = { Text(dest.label) },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            TBANavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
