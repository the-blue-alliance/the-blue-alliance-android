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
import com.thebluealliance.android.ui.regionaladvancement.RegionalAdvancementScreen
import com.thebluealliance.android.ui.search.SearchScreen
import com.thebluealliance.android.ui.events.detail.EventDetailScreen
import com.thebluealliance.android.ui.events.EventsScreen
import com.thebluealliance.android.ui.more.AboutScreen
import com.thebluealliance.android.ui.more.MoreScreen
import com.thebluealliance.android.ui.more.ThanksScreen
import com.thebluealliance.android.ui.mytba.MyTBAScreen
import com.thebluealliance.android.ui.settings.SettingsScreen
import com.thebluealliance.android.ui.teamevent.TeamEventDetailScreen
import com.thebluealliance.android.ui.teams.TeamDetailScreen
import com.thebluealliance.android.ui.teams.TeamsScreen

@Composable
fun TBANavHost(
    navController: NavHostController,
    onSignIn: () -> Unit,
    scrollToTopTrigger: Int = 0,
    onEventsYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
    onDistrictsYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
    onRegionalAdvancementYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
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
        composable<Route.RegionalAdvancement> {
            RegionalAdvancementScreen(
                onNavigateToTeam = { teamKey ->
                    navController.navigate(Screen.TeamDetail(teamKey))
                },
                scrollToTopTrigger = scrollToTopTrigger,
                onYearState = onRegionalAdvancementYearState,
            )
        }
        composable<Route.More> {
            MoreScreen(
                onNavigateToMyTBA = { navController.navigate(Screen.MyTBA) },
                onNavigateToSettings = { navController.navigate(Screen.Settings) },
                onNavigateToAbout = { navController.navigate(Screen.About) },
                onNavigateToThanks = { navController.navigate(Screen.Thanks) },
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
        composable<Screen.About> {
            AboutScreen()
        }
        composable<Screen.Thanks> {
            ThanksScreen()
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
                onNavigateToTeamEvent = { teamKey, eventKey ->
                    navController.navigate(Screen.TeamEventDetail(teamKey, eventKey))
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
                onNavigateToTeam = { teamKey ->
                    navController.navigate(Screen.TeamDetail(teamKey))
                },
                onNavigateToEvent = { eventKey ->
                    navController.navigate(Screen.EventDetail(eventKey))
                },
            )
        }
        composable<Screen.DistrictDetail> {
            DistrictDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEvent = { eventKey ->
                    navController.navigate(Screen.EventDetail(eventKey))
                },
                onNavigateToTeam = { teamKey ->
                    navController.navigate(Screen.TeamDetail(teamKey))
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
                onNavigateToTeamEvent = { teamKey, eventKey ->
                    navController.navigate(Screen.TeamEventDetail(teamKey, eventKey))
                },
            )
        }
        composable<Screen.TeamEventDetail> {
            TeamEventDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMatch = { matchKey ->
                    navController.navigate(Screen.MatchDetail(matchKey))
                },
                onNavigateToTeam = { teamKey ->
                    navController.navigate(Screen.TeamDetail(teamKey))
                },
                onNavigateToEvent = { eventKey ->
                    navController.navigate(Screen.EventDetail(eventKey))
                },
            )
        }
    }
}
