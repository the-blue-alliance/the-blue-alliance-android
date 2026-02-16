package com.thebluealliance.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
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
    backStack: NavBackStack<NavKey>,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    scrollToTopTrigger: Int = 0,
    onEventsYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
    onDistrictsYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Screen.Events> {
                EventsScreen(
                    onNavigateToEvent = { eventKey ->
                        backStack.add(Screen.EventDetail(eventKey))
                    },
                    scrollToTopTrigger = scrollToTopTrigger,
                    onYearState = onEventsYearState,
                )
            }
            entry<Screen.Teams> {
                TeamsScreen(
                    onNavigateToTeam = { teamKey ->
                        backStack.add(Screen.TeamDetail(teamKey))
                    },
                    scrollToTopTrigger = scrollToTopTrigger,
                )
            }
            entry<Screen.Districts> {
                DistrictsScreen(
                    onNavigateToDistrict = { districtKey ->
                        backStack.add(Screen.DistrictDetail(districtKey))
                    },
                    scrollToTopTrigger = scrollToTopTrigger,
                    onYearState = onDistrictsYearState,
                )
            }
            entry<Screen.More> {
                MoreScreen(
                    onNavigateToMyTBA = { backStack.add(Screen.MyTBA) },
                    onNavigateToSettings = { backStack.add(Screen.Settings) },
                    onNavigateToAbout = { backStack.add(Screen.About) },
                    onNavigateToThanks = { backStack.add(Screen.Thanks) },
                )
            }
            entry<Screen.MyTBA> {
                MyTBAScreen(
                    onSignIn = onSignIn,
                    onNavigateToTeam = { teamKey ->
                        backStack.add(Screen.TeamDetail(teamKey))
                    },
                    onNavigateToEvent = { eventKey ->
                        backStack.add(Screen.EventDetail(eventKey))
                    },
                    scrollToTopTrigger = scrollToTopTrigger,
                )
            }
            entry<Screen.Settings> {
                SettingsScreen()
            }
            entry<Screen.About> {
                AboutScreen()
            }
            entry<Screen.Thanks> {
                ThanksScreen()
            }
            entry<Screen.Search> {
                SearchScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToTeam = { teamKey ->
                        backStack.add(Screen.TeamDetail(teamKey))
                    },
                    onNavigateToEvent = { eventKey ->
                        backStack.add(Screen.EventDetail(eventKey))
                    },
                )
            }
            entry<Screen.EventDetail> { eventDetail ->
                val viewModel = hiltViewModel<EventDetailViewModel, EventDetailViewModel.Factory>(
                    creationCallback = { factory -> factory.create(eventDetail) }
                )
                EventDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToTeam = { teamKey ->
                        backStack.add(Screen.TeamDetail(teamKey))
                    },
                    onNavigateToMatch = { matchKey ->
                        backStack.add(Screen.MatchDetail(matchKey))
                    },
                    onNavigateToMyTBA = { backStack.add(Screen.MyTBA) },
                    onNavigateToTeamEvent = { teamKey, eventKey ->
                        backStack.add(Screen.TeamEventDetail(teamKey, eventKey))
                    },
                )
            }
            entry<Screen.MatchDetail> { matchDetail ->
                val viewModel = hiltViewModel<MatchDetailViewModel, MatchDetailViewModel.Factory>(
                    creationCallback = { factory -> factory.create(matchDetail) }
                )
                MatchDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToTeam = { teamKey ->
                        backStack.add(Screen.TeamDetail(teamKey))
                    },
                    onNavigateToEvent = { eventKey ->
                        backStack.add(Screen.EventDetail(eventKey))
                    },
                )
            }
            entry<Screen.DistrictDetail> { districtDetail ->
                val viewModel = hiltViewModel<DistrictDetailViewModel, DistrictDetailViewModel.Factory>(
                    creationCallback = { factory -> factory.create(districtDetail) }
                )
                DistrictDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToEvent = { eventKey ->
                        backStack.add(Screen.EventDetail(eventKey))
                    },
                    onNavigateToTeam = { teamKey ->
                        backStack.add(Screen.TeamDetail(teamKey))
                    },
                )
            }
            entry<Screen.TeamDetail> { teamDetail ->
                val viewModel = hiltViewModel<TeamDetailViewModel, TeamDetailViewModel.Factory>(
                    creationCallback = { factory -> factory.create(teamDetail) }
                )
                TeamDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToEvent = { eventKey ->
                        backStack.add(Screen.EventDetail(eventKey))
                    },
                    onNavigateToMyTBA = { backStack.add(Screen.MyTBA) },
                    onNavigateToTeamEvent = { teamKey, eventKey ->
                        backStack.add(Screen.TeamEventDetail(teamKey, eventKey))
                    },
                )
            }
            entry<Screen.TeamEventDetail> { teamEventDetail ->
                val viewModel = hiltViewModel<TeamEventDetailViewModel, TeamEventDetailViewModel.Factory>(
                    creationCallback = { factory -> factory.create(teamEventDetail) }
                )
                TeamEventDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToMatch = { matchKey ->
                        backStack.add(Screen.MatchDetail(matchKey))
                    },
                    onNavigateToTeam = { teamKey ->
                        backStack.add(Screen.TeamDetail(teamKey))
                    },
                    onNavigateToEvent = { eventKey ->
                        backStack.add(Screen.EventDetail(eventKey))
                    },
                )
            }
        },
        modifier = modifier,
    )
}

