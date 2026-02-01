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
import com.thebluealliance.android.ui.mytba.MyTBAScreen
import com.thebluealliance.android.ui.teams.TeamDetailScreen
import com.thebluealliance.android.ui.teams.TeamsScreen

@Composable
fun TBANavHost(
    navController: NavHostController,
    onSignIn: () -> Unit,
    scrollToTopTrigger: Int = 0,
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
            )
        }
        composable<Route.MyTBA> {
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
            )
        }
        composable<Screen.MatchDetail>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "https://www.thebluealliance.com/match/{matchKey}" },
            ),
        ) {
            MatchDetailScreen(
                onNavigateBack = { navController.popBackStack() },
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
            )
        }
    }
}
