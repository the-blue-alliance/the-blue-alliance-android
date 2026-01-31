package com.thebluealliance.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thebluealliance.android.ui.districts.DistrictsScreen
import com.thebluealliance.android.ui.events.EventDetailScreen
import com.thebluealliance.android.ui.events.EventsScreen
import com.thebluealliance.android.ui.mytba.MyTBAScreen
import com.thebluealliance.android.ui.teams.TeamsScreen

@Composable
fun TBANavHost(
    navController: NavHostController,
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
            )
        }
        composable<Route.Teams> {
            TeamsScreen()
        }
        composable<Route.Districts> {
            DistrictsScreen()
        }
        composable<Route.MyTBA> {
            MyTBAScreen()
        }
        composable<Screen.EventDetail> {
            EventDetailScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
