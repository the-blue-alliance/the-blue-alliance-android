package com.thebluealliance.android.ui

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.thebluealliance.android.config.ThemePreferences
import com.thebluealliance.android.navigation.NavigationState
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.navigation.TBANavigation
import com.thebluealliance.android.navigation.rememberNavigationState
import com.thebluealliance.android.ui.components.LocalRefreshTargets
import com.thebluealliance.android.ui.components.RefreshTargets
import com.thebluealliance.android.ui.components.primaryClicksOnly
import com.thebluealliance.android.ui.theme.TBATheme
import com.thebluealliance.android.ui.theme.ThemeMode
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TBAApp(
    launchBackStack: List<NavKey>,
    isNewTask: Boolean,
    themePreferences: ThemePreferences? = null,
) {
    val navState =
        rememberNavigationState(
            launchBackStack = launchBackStack,
            topLevelRoutes = TOP_LEVEL_DESTINATIONS.map { it.key },
            startTopLevelRoute = TOP_LEVEL_DESTINATIONS.first().key,
            isNewTask = isNewTask,
        )
    FirebaseAnalyticsEffect(navState)

    val themeMode by (
        themePreferences
            ?.themeModeFlow
            ?.collectAsStateWithLifecycle(ThemeMode.AUTO)
            ?: remember { mutableStateOf(ThemeMode.AUTO) }
    )
    val darkTheme =
        when (themeMode) {
            ThemeMode.AUTO -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }

    val activity = LocalActivity.current as? ComponentActivity
    LaunchedEffect(darkTheme) {
        // Keep system bars in sync with the in-app theme override, not just the system
        // night mode; the status bar stays dark-style because the top bar is primaryContainer, the brand
        // blue (#3F51B5) in both light and dark, and beside a rail the status bar sits on the gray
        // strip NavigationFrame paints.
        // enableEdgeToEdge is a one-shot call with no teardown, so there's nothing to dispose.
        activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle =
                SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
        )
    }

    // F5 and Ctrl+R refresh whichever screen is in front (see RefreshTargets).
    val refreshTargets = remember { RefreshTargets() }
    TBATheme(darkTheme = darkTheme) {
        CompositionLocalProvider(LocalRefreshTargets provides refreshTargets) {
            // A right-click is for context menus: it never acts as a click anywhere in the app.
            Box(Modifier.fillMaxSize().primaryClicksOnly()) {
                TBANavigation(
                    navState = navState,
                )
            }
        }
    }
}

@Composable
private fun FirebaseAnalyticsEffect(navState: NavigationState) {
    val firebaseAnalytics = remember { Firebase.analytics }
    LaunchedEffect(navState) {
        snapshotFlow { navState.currentRoute }
            .collectLatest { currentRoute ->
                val screenName =
                    when (currentRoute) {
                        is Screen.Events -> "Events"
                        Screen.Teams -> "Teams"
                        Screen.Districts -> "Districts"
                        is Screen.RegionalAdvancement -> "RegionalAdvancement"
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
