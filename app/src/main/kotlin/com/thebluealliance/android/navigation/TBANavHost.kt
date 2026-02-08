package com.thebluealliance.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.thebluealliance.android.ui.districts.DistrictDetailScreen
import com.thebluealliance.android.ui.districts.DistrictsScreen
import com.thebluealliance.android.ui.matches.MatchDetailScreen
import com.thebluealliance.android.ui.search.SearchScreen
import com.thebluealliance.android.ui.events.EventDetailScreen
import com.thebluealliance.android.ui.events.EventsScreen
import com.thebluealliance.android.ui.more.MoreScreen
import com.thebluealliance.android.ui.mytba.MyTBAScreen
import com.thebluealliance.android.ui.settings.SettingsScreen
import com.thebluealliance.android.ui.teams.TeamDetailScreen
import com.thebluealliance.android.ui.teams.TeamsScreen

@Composable
fun TBANavHost(
    navController: NavHostController,
    onSignIn: () -> Unit,
    scrollToTopTrigger: Int = 0,
    onEventsYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
    onDistrictsYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Events,
        modifier = modifier,
    ) {
        composable<Route.Events> {
            EventsScreen(
                onNavigateToEvent = { eventKey ->
                    navController.navigate(Screen.EventDetail(eventKey))
                },
                scrollToTopTrigger = scrollToTopTrigger,
                onYearState = onEventsYearState,
            )
        }
        composable<Route.Teams> {
            TeamsScreen(
                onNavigateToTeam = { teamKey ->
                    navController.navigate(Screen.TeamDetail(teamKey))
                },
                scrollToTopTrigger = scrollToTopTrigger,
            )
        }
        composable<Route.Districts> {
            DistrictsScreen(
                onNavigateToDistrict = { districtKey ->
                    navController.navigate(Screen.DistrictDetail(districtKey))
                },
                scrollToTopTrigger = scrollToTopTrigger,
                onYearState = onDistrictsYearState,
            )
        }
        composable<Route.More> {
            MoreScreen(
                onNavigateToMyTBA = { navController.navigate(Screen.MyTBA) },
                onNavigateToSettings = { navController.navigate(Screen.Settings) },
            )
        }
        composable<Screen.MyTBA> {
            MyTBAScreen(
                onSignIn = onSignIn,
                onNavigateToTeam = { teamKey ->
                    navController.navigate(Screen.TeamDetail(teamKey))
                },
                onNavigateToEvent = { eventKey ->
                    navController.navigate(Screen.EventDetail(eventKey))
                },
                scrollToTopTrigger = scrollToTopTrigger,
            )
        }
        composable<Screen.Settings> {
            SettingsScreen()
        }
        composable<Screen.Search> {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTeam = { teamKey ->
                    navController.navigate(Screen.TeamDetail(teamKey))
                },
                onNavigateToEvent = { eventKey ->
                    navController.navigate(Screen.EventDetail(eventKey))
                },
            )
        }
        composable<Screen.EventDetail>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://www.thebluealliance.com/event/{eventKey}" },
            ),
        ) {
            EventDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTeam = { teamKey ->
                    navController.navigate(Screen.TeamDetail(teamKey))
                },
                onNavigateToMatch = { matchKey ->
                    navController.navigate(Screen.MatchDetail(matchKey))
                },
                onNavigateToMyTBA = { navController.navigate(Screen.MyTBA) },
            )
        }
        composable<Screen.MatchDetail>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://www.thebluealliance.com/match/{matchKey}" },
            ),
        ) {
            MatchDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTeam = { teamKey ->
                    navController.navigate(Screen.TeamDetail(teamKey))
                },
            )
        }
        composable<Screen.DistrictDetail> {
            DistrictDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEvent = { eventKey ->
                    navController.navigate(Screen.EventDetail(eventKey))
                },
            )
        }
        composable<Screen.TeamDetail>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://www.thebluealliance.com/team/{teamKey}" },
            ),
        ) {
            TeamDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEvent = { eventKey ->
                    navController.navigate(Screen.EventDetail(eventKey))
                },
                onNavigateToMyTBA = { navController.navigate(Screen.MyTBA) },
            )
        }
    }
}
