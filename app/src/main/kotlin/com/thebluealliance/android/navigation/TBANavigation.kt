package com.thebluealliance.android.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.thebluealliance.android.MainActivity
import com.thebluealliance.android.ui.districts.DistrictDetailScreen
import com.thebluealliance.android.ui.districts.DistrictDetailViewModel
import com.thebluealliance.android.ui.districts.DistrictsScreen
import com.thebluealliance.android.ui.events.EventsScreen
import com.thebluealliance.android.ui.events.detail.EventDetailScreen
import com.thebluealliance.android.ui.events.detail.EventDetailViewModel
import com.thebluealliance.android.ui.matches.MatchDetailScreen
import com.thebluealliance.android.ui.matches.MatchDetailViewModel
import com.thebluealliance.android.ui.more.AboutScreen
import com.thebluealliance.android.ui.more.MoreScreen
import com.thebluealliance.android.ui.more.ThanksScreen
import com.thebluealliance.android.ui.mytba.MyTBAScreen
import com.thebluealliance.android.ui.search.SearchScreen
import com.thebluealliance.android.ui.settings.SettingsScreen
import com.thebluealliance.android.ui.teamevent.TeamEventDetailScreen
import com.thebluealliance.android.ui.teamevent.TeamEventDetailViewModel
import com.thebluealliance.android.ui.teams.TeamDetailScreen
import com.thebluealliance.android.ui.teams.TeamDetailViewModel
import com.thebluealliance.android.ui.teams.TeamsScreen

@Composable
fun TBANavigation(
    navState: NavigationState,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current as MainActivity
    val navigator = remember { Navigator(navState, activity) }

    NavDisplay(
        modifier = modifier,
        onBack = { navigator.goBack() },
        entries = navState.toEntries(
            entryProvider = entryProvider {
                entry<Screen.Events> {
                    EventsScreen(
                        onNavigateToEvent = { eventKey ->
                            navigator.navigate(Screen.EventDetail(eventKey))
                        },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                        onNavigateToTopLevel = { navigator.navigate(it) },
                        currentRoute = navState.currentRoute,
                    )
                }
                entry<Screen.Teams> {
                    TeamsScreen(
                        onNavigateToTeam = { teamKey ->
                            navigator.navigate(Screen.TeamDetail(teamKey))
                        },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                        onNavigateToTopLevel = { navigator.navigate(it) },
                        currentRoute = navState.currentRoute,
                    )
                }
                entry<Screen.Districts> {
                    DistrictsScreen(
                        onNavigateToDistrict = { districtKey ->
                            navigator.navigate(Screen.DistrictDetail(districtKey))
                        },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                        onNavigateToTopLevel = { navigator.navigate(it) },
                        currentRoute = navState.currentRoute,
                    )
                }
                entry<Screen.More> {
                    MoreScreen(
                        onNavigateToMyTBA = { navigator.navigate(Screen.MyTBA) },
                        onNavigateToSettings = { navigator.navigate(Screen.Settings) },
                        onNavigateToAbout = { navigator.navigate(Screen.About) },
                        onNavigateToThanks = { navigator.navigate(Screen.Thanks) },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                        onNavigateToTopLevel = { navigator.navigate(it) },
                        currentRoute = navState.currentRoute,
                    )
                }
                entry<Screen.MyTBA> {
                    MyTBAScreen(
                        onSignIn = { activity.startGoogleSignIn() },
                        onNavigateToTeam = { teamKey ->
                            navigator.navigate(Screen.TeamDetail(teamKey))
                        },
                        onNavigateToEvent = { eventKey ->
                            navigator.navigate(Screen.EventDetail(eventKey))
                        },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                        onNavigateToTopLevel = { navigator.navigate(it) },
                        currentRoute = navState.currentRoute,
                        onNavigateUp = { navigator.navigateUp() },
                    )
                }
                entry<Screen.Settings> {
                    SettingsScreen(
                        onNavigateUp = { navigator.navigateUp() },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                        onNavigateToTopLevel = { navigator.navigate(it) },
                        currentRoute = navState.currentRoute,
                    )
                }
                entry<Screen.About> {
                    AboutScreen(
                        onNavigateUp = { navigator.navigateUp() },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                        onNavigateToTopLevel = { navigator.navigate(it) },
                        currentRoute = navState.currentRoute,
                    )
                }
                entry<Screen.Thanks> {
                    ThanksScreen(
                        onNavigateUp = { navigator.navigateUp() },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                        onNavigateToTopLevel = { navigator.navigate(it) },
                        currentRoute = navState.currentRoute,
                    )
                }
                entry<Screen.Search> {
                    SearchScreen(
                        onNavigateUp = { navigator.navigateUp() },
                        onNavigateToTeam = { teamKey ->
                            navigator.navigate(Screen.TeamDetail(teamKey))
                        },
                        onNavigateToEvent = { eventKey ->
                            navigator.navigate(Screen.EventDetail(eventKey))
                        },
                    )
                }
                entry<Screen.EventDetail> { eventDetail ->
                    val viewModel =
                        hiltViewModel<EventDetailViewModel, EventDetailViewModel.Factory>(
                            creationCallback = { factory -> factory.create(eventDetail) }
                        )
                    EventDetailScreen(
                        viewModel = viewModel,
                        onNavigateUp = { navigator.navigateUp() },
                        onNavigateToTeam = { teamKey ->
                            navigator.navigate(Screen.TeamDetail(teamKey))
                        },
                        onNavigateToMatch = { matchKey ->
                            navigator.navigate(Screen.MatchDetail(matchKey))
                        },
                        onNavigateToMyTBA = { navigator.navigate(Screen.MyTBA) },
                        onNavigateToTeamEvent = { teamKey, eventKey ->
                            navigator.navigate(Screen.TeamEventDetail(teamKey, eventKey))
                        },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                    )
                }
                entry<Screen.MatchDetail> { matchDetail ->
                    val viewModel =
                        hiltViewModel<MatchDetailViewModel, MatchDetailViewModel.Factory>(
                            creationCallback = { factory -> factory.create(matchDetail) }
                        )
                    MatchDetailScreen(
                        viewModel = viewModel,
                        onNavigateUp = { navigator.navigateUp() },
                        onNavigateToTeam = { teamKey ->
                            navigator.navigate(Screen.TeamDetail(teamKey))
                        },
                        onNavigateToEvent = { eventKey ->
                            navigator.navigate(Screen.EventDetail(eventKey))
                        },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                    )
                }
                entry<Screen.DistrictDetail> { districtDetail ->
                    val viewModel =
                        hiltViewModel<DistrictDetailViewModel, DistrictDetailViewModel.Factory>(
                            creationCallback = { factory -> factory.create(districtDetail) }
                        )
                    DistrictDetailScreen(
                        viewModel = viewModel,
                        onNavigateUp = { navigator.navigateUp() },
                        onNavigateToEvent = { eventKey ->
                            navigator.navigate(Screen.EventDetail(eventKey))
                        },
                        onNavigateToTeam = { teamKey ->
                            navigator.navigate(Screen.TeamDetail(teamKey))
                        },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                    )
                }
                entry<Screen.TeamDetail> { teamDetail ->
                    val viewModel = hiltViewModel<TeamDetailViewModel, TeamDetailViewModel.Factory>(
                        creationCallback = { factory -> factory.create(teamDetail) }
                    )
                    TeamDetailScreen(
                        viewModel = viewModel,
                        onNavigateUp = { navigator.navigateUp() },
                        onNavigateToEvent = { eventKey ->
                            navigator.navigate(Screen.EventDetail(eventKey))
                        },
                        onNavigateToMyTBA = { navigator.navigate(Screen.MyTBA) },
                        onNavigateToTeamEvent = { teamKey, eventKey ->
                            navigator.navigate(Screen.TeamEventDetail(teamKey, eventKey))
                        },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                    )
                }
                entry<Screen.TeamEventDetail> { teamEventDetail ->
                    val viewModel =
                        hiltViewModel<TeamEventDetailViewModel, TeamEventDetailViewModel.Factory>(
                            creationCallback = { factory -> factory.create(teamEventDetail) }
                        )
                    TeamEventDetailScreen(
                        viewModel = viewModel,
                        onNavigateUp = { navigator.navigateUp() },
                        onNavigateToMatch = { matchKey ->
                            navigator.navigate(Screen.MatchDetail(matchKey))
                        },
                        onNavigateToTeam = { teamKey ->
                            navigator.navigate(Screen.TeamDetail(teamKey))
                        },
                        onNavigateToEvent = { eventKey ->
                            navigator.navigate(Screen.EventDetail(eventKey))
                        },
                        onNavigateToSearch = { navigator.navigate(Screen.Search) },
                    )
                }
            },
        ),
    )
}
