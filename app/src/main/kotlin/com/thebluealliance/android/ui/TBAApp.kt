package com.thebluealliance.android.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavKey
import com.google.firebase.Firebase
import com.thebluealliance.android.config.ThemePreferences
import com.thebluealliance.android.ui.theme.ThemeMode
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.thebluealliance.android.MainActivity
import com.thebluealliance.android.navigation.NavigationState
import com.thebluealliance.android.navigation.Navigator
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.navigation.TBANavigation
import com.thebluealliance.android.navigation.rememberNavigationState
import com.thebluealliance.android.ui.theme.TBATheme
import kotlinx.coroutines.flow.collectLatest

data class TopLevelDestination(
    val key: NavKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(Screen.Events(), "Events", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    TopLevelDestination(Screen.Teams, "Teams", Icons.Filled.Groups, Icons.Outlined.Groups),
    TopLevelDestination(Screen.Districts, "Districts", Icons.Filled.Map, Icons.Outlined.Map),
    TopLevelDestination(Screen.More, "More", Icons.Filled.Menu, Icons.Outlined.Menu),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TBAApp(
    startRoute: NavKey,
    isNewTask: Boolean,
    themePreferences: ThemePreferences? = null,
) {
    val navState = rememberNavigationState(
        startRoute = startRoute,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.map { it.key },
        startTopLevelRoute = TOP_LEVEL_DESTINATIONS.first().key,
        isNewTask = isNewTask,
    )
    val activity = LocalActivity.current as MainActivity
    val navigator = remember { Navigator(navState, activity) }
    FirebaseAnalyticsEffect(navState)

    val themeMode by (themePreferences?.themeModeFlow
        ?.collectAsStateWithLifecycle(ThemeMode.AUTO)
        ?: remember { mutableStateOf(ThemeMode.AUTO) })
    val darkTheme = when (themeMode) {
        ThemeMode.AUTO -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    TBATheme(darkTheme = darkTheme) {
        val currentRoute = navState.currentRoute

        var scrollToTopTrigger by remember { mutableIntStateOf(0) }
        var eventsSelectedYear by remember { mutableIntStateOf(0) }
        var eventsMaxYear by remember { mutableIntStateOf(0) }
        var eventsYearDropdownExpanded by remember { mutableStateOf(false) }
        var onEventsYearSelected by remember { mutableStateOf<((Int) -> Unit)?>(null) }
        var districtsSelectedYear by remember { mutableIntStateOf(0) }
        var districtsMaxYear by remember { mutableIntStateOf(0) }
        var districtsYearDropdownExpanded by remember { mutableStateOf(false) }
        var onDistrictsYearSelected by remember { mutableStateOf<((Int) -> Unit)?>(null) }

        val moreSubScreens = listOf(Screen.MyTBA, Screen.Settings, Screen.About, Screen.Thanks)
        val isOnMoreSubScreen = moreSubScreens.any { it == currentRoute }
        val showBottomBar = TOP_LEVEL_DESTINATIONS.any { dest -> currentRoute::class == dest.key::class } || isOnMoreSubScreen

        Scaffold(
            topBar = {
                if (showBottomBar) {
                    TopAppBar(
                        navigationIcon = {
                            if (isOnMoreSubScreen) {
                                IconButton(onClick = { navigator.goBack() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        },
                        title = {
                            val isEvents = currentRoute is Screen.Events
                            val isDistricts = currentRoute == Screen.Districts
                            if (isOnMoreSubScreen) {
                                val title = when (currentRoute) {
                                    Screen.MyTBA -> "myTBA"
                                    Screen.Settings -> "Settings"
                                    Screen.About -> "About"
                                    Screen.Thanks -> "Thanks"
                                    else -> "More"
                                }
                                Text(title)
                            } else if (isEvents && eventsSelectedYear > 0) {
                                Row(
                                    modifier = Modifier.clickable {
                                        eventsYearDropdownExpanded = true
                                    },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("$eventsSelectedYear Events")
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Select year",
                                    )
                                    DropdownMenu(
                                        expanded = eventsYearDropdownExpanded,
                                        onDismissRequest = { eventsYearDropdownExpanded = false },
                                    ) {
                                        (eventsMaxYear downTo 1992).forEach { year ->
                                            DropdownMenuItem(
                                                text = { Text(year.toString()) },
                                                onClick = {
                                                    onEventsYearSelected?.invoke(year)
                                                    eventsYearDropdownExpanded = false
                                                },
                                            )
                                        }
                                    }
                                }
                            } else if (isDistricts && districtsSelectedYear > 0) {
                                Row(
                                    modifier = Modifier.clickable {
                                        districtsYearDropdownExpanded = true
                                    },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("$districtsSelectedYear Districts")
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Select year",
                                    )
                                    DropdownMenu(
                                        expanded = districtsYearDropdownExpanded,
                                        onDismissRequest = { districtsYearDropdownExpanded = false },
                                    ) {
                                        (districtsMaxYear downTo 2009).forEach { year ->
                                            DropdownMenuItem(
                                                text = { Text(year.toString()) },
                                                onClick = {
                                                    onDistrictsYearSelected?.invoke(year)
                                                    districtsYearDropdownExpanded = false
                                                },
                                            )
                                        }
                                    }
                                }
                            } else {
                                val dest = TOP_LEVEL_DESTINATIONS.firstOrNull { dest ->
                                    currentRoute == dest.key
                                }
                                Text(dest?.label ?: "")
                            }
                        },
                        actions = {
                            IconButton(onClick = { navigator.navigate(Screen.Search) }) {
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
                            val selected = currentRoute::class == dest.key::class || (dest.key is Screen.More && isOnMoreSubScreen)
                            NavigationBarItem(
                                selected = selected,
                                onClick = dropUnlessResumed {
                                    if (selected) {
                                        scrollToTopTrigger++
                                    } else {
                                        navigator.navigate(dest.key)
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
            val activity = LocalActivity.current as MainActivity
            TBANavigation(
                navState = navState,
                navigator = navigator,
                onSignIn = { activity.startGoogleSignIn() },
                scrollToTopTrigger = scrollToTopTrigger,
                onEventsYearState = { selectedYear, maxYear, onYearSelected ->
                    eventsSelectedYear = selectedYear
                    eventsMaxYear = maxYear
                    onEventsYearSelected = onYearSelected
                },
                onDistrictsYearState = { selectedYear, maxYear, onYearSelected ->
                    districtsSelectedYear = selectedYear
                    districtsMaxYear = maxYear
                    onDistrictsYearSelected = onYearSelected
                },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun FirebaseAnalyticsEffect(
    navState: NavigationState
) {
    val firebaseAnalytics = remember { Firebase.analytics }
    LaunchedEffect(navState) {
        snapshotFlow { navState.currentRoute }
            .collectLatest { currentRoute ->
                val screenName = when (currentRoute) {
                    is Screen.Events -> "Events"
                    Screen.Teams -> "Teams"
                    Screen.Districts -> "Districts"
                    Screen.More -> "More"
                    is Screen.EventDetail -> "EventDetail"
                    is Screen.TeamDetail -> "TeamDetail"
                    is Screen.MatchDetail -> "MatchDetail"
                    is Screen.TeamEventDetail -> "TeamEventDetail"
                    is Screen.DistrictDetail -> "DistrictDetail"
                    Screen.MyTBA -> "MyTBA"
                    Screen.Search -> "Search"
                    Screen.Settings -> "Settings"
                    Screen.About -> "About"
                    Screen.Thanks -> "Thanks"
                    else -> null
                }
                if (screenName != null) {
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                        param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                        param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
                    }
                }
            }
    }
}
