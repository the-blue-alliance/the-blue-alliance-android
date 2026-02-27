package com.thebluealliance.android.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.thebluealliance.android.navigation.NavigationState
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.navigation.TBANavigation
import com.thebluealliance.android.navigation.rememberNavigationState
import com.thebluealliance.android.config.ThemePreferences
import com.thebluealliance.android.ui.theme.TBATheme
import com.thebluealliance.android.ui.theme.ThemeMode
import kotlinx.coroutines.flow.collectLatest

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
        TBANavigation(
            navState = navState,
        )
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
                    Screen.Events -> "Events"
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
