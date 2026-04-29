package com.thebluealliance.android.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.thebluealliance.android.MainActivity
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS
import com.thebluealliance.android.ui.components.TBABottomBar
import com.thebluealliance.android.ui.districts.DistrictDetailScreen
import com.thebluealliance.android.ui.districts.DistrictDetailViewModel
import com.thebluealliance.android.ui.districts.DistrictsScreen
import com.thebluealliance.android.ui.events.EventsScreen
import com.thebluealliance.android.ui.events.EventsViewModel
import com.thebluealliance.android.ui.events.detail.EventDetailScreen
import com.thebluealliance.android.ui.events.detail.EventDetailViewModel
import com.thebluealliance.android.ui.events.detail.PitMapScreen
import com.thebluealliance.android.ui.events.detail.PitMapViewModel
import com.thebluealliance.android.ui.matches.MatchDetailScreen
import com.thebluealliance.android.ui.matches.MatchDetailViewModel
import com.thebluealliance.android.ui.more.AboutScreen
import com.thebluealliance.android.ui.more.MoreScreen
import com.thebluealliance.android.ui.more.ThanksScreen
import com.thebluealliance.android.ui.mytba.MyTBAScreen
import com.thebluealliance.android.ui.regional.RegionalAdvancementScreen
import com.thebluealliance.android.ui.search.SearchScreen
import com.thebluealliance.android.ui.settings.SettingsScreen
import com.thebluealliance.android.ui.teamevent.TeamEventDetailScreen
import com.thebluealliance.android.ui.teamevent.TeamEventDetailViewModel
import com.thebluealliance.android.ui.teams.TeamDetailScreen
import com.thebluealliance.android.ui.teams.TeamDetailViewModel
import com.thebluealliance.android.ui.teams.TeamsScreen
import com.thebluealliance.android.ui.theme.TBAMotionTokens
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/** Looks up the reselect flow for the tab [route] belongs to, tolerating parameterized keys. */
private fun Map<NavKey, MutableSharedFlow<Unit>>.reselectFlowFor(route: NavKey) =
    entries.firstOrNull { it.key.isSameTab(route) }?.value

@Composable
fun TBANavigation(
    navState: NavigationState,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current as MainActivity
    val navigator = remember { Navigator(navState, activity) }

    val showBottomBar =
        TOP_LEVEL_DESTINATIONS.any { it.key.isSameTab(navState.currentRoute) } ||
            navState.currentRoute in
            setOf(
                Screen.MyTBA,
                Screen.Settings,
                Screen.About,
                Screen.Thanks,
                Screen.RegionalAdvancement,
            )

    val coroutineScope = rememberCoroutineScope()
    val tabReselectFlows =
        remember {
            TOP_LEVEL_DESTINATIONS
                .map { it.key }
                .associateWith { MutableSharedFlow<Unit>() }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        NavDisplay(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                    initialOffsetX = { it },
                ) togetherWith
                    slideOutHorizontally(
                        animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                        targetOffsetX = { -it },
                    )
            },
            popTransitionSpec = {
                slideInHorizontally(
                    animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                    initialOffsetX = { -it },
                ) togetherWith
                    slideOutHorizontally(
                        animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                        targetOffsetX = { it },
                    )
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(
                    animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                    initialOffsetX = { -it },
                ) togetherWith
                    slideOutHorizontally(
                        animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                        targetOffsetX = { it },
                    )
            },
            onBack = { navigator.goBack() },
            entries =
                navState.toEntries(
                    entryProvider =
                        entryProvider {
                            entry<Screen.Events>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) { events ->
                                val viewModel =
                                    hiltViewModel<EventsViewModel, EventsViewModel.Factory>(
                                        creationCallback = { factory -> factory.create(events) },
                                    )
                                EventsScreen(
                                    viewModel = viewModel,
                                    onNavigateToEvent = { eventKey ->
                                        navigator.navigate(Screen.EventDetail(eventKey))
                                    },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                    reselectFlow =
                                        tabReselectFlows.reselectFlowFor(events) ?: emptyFlow(),
                                )
                            }
                            entry<Screen.Teams>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) {
                                TeamsScreen(
                                    onNavigateToTeam = { teamKey ->
                                        navigator.navigate(Screen.TeamDetail(teamKey))
                                    },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                    reselectFlow = tabReselectFlows[Screen.Teams] ?: emptyFlow(),
                                )
                            }
                            entry<Screen.Districts>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) {
                                DistrictsScreen(
                                    onNavigateToDistrict = { districtKey ->
                                        navigator.navigate(Screen.DistrictDetail(districtKey))
                                    },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                    reselectFlow =
                                        tabReselectFlows[Screen.Districts] ?: emptyFlow(),
                                )
                            }
                            entry<Screen.RegionalAdvancement>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) {
                                RegionalAdvancementScreen(
                                    onNavigateToTeam = { teamKey ->
                                        navigator.navigate(Screen.TeamDetail(teamKey))
                                    },
                                    onNavigateToEvent = { eventKey ->
                                        navigator.navigate(Screen.EventDetail(eventKey))
                                    },
                                    onNavigateUp = { navigator.navigateUp() },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                    reselectFlow =
                                        tabReselectFlows[Screen.RegionalAdvancement] ?: emptyFlow(),
                                )
                            }
                            entry<Screen.More>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) {
                                MoreScreen(
                                    onNavigateToMyTBA = { navigator.navigate(Screen.MyTBA) },
                                    onNavigateToRegionalAdvancement = {
                                        navigator.navigate(
                                            Screen.RegionalAdvancement,
                                        )
                                    },
                                    onNavigateToSettings = { navigator.navigate(Screen.Settings) },
                                    onNavigateToAbout = { navigator.navigate(Screen.About) },
                                    onNavigateToThanks = { navigator.navigate(Screen.Thanks) },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                )
                            }
                            entry<Screen.MyTBA>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) {
                                MyTBAScreen(
                                    onSignIn = { activity.startGoogleSignIn() },
                                    onNavigateToTeam = { teamKey ->
                                        navigator.navigate(Screen.TeamDetail(teamKey))
                                    },
                                    onNavigateToEvent = { eventKey ->
                                        navigator.navigate(Screen.EventDetail(eventKey))
                                    },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                    onNavigateUp = { navigator.navigateUp() },
                                    reselectFlow = tabReselectFlows[Screen.MyTBA] ?: emptyFlow(),
                                )
                            }
                            entry<Screen.Settings>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) {
                                SettingsScreen(
                                    onNavigateUp = { navigator.navigateUp() },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                )
                            }
                            entry<Screen.About>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) {
                                AboutScreen(
                                    onNavigateUp = { navigator.navigateUp() },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                )
                            }
                            entry<Screen.Thanks>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) {
                                ThanksScreen(
                                    onNavigateUp = { navigator.navigateUp() },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
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
                                val viewModel: EventDetailViewModel =
                                    hiltViewModel(
                                        creationCallback = { f: EventDetailViewModel.Factory ->
                                            f.create(eventDetail)
                                        },
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
                                        navigator.navigate(
                                            Screen.TeamEventDetail(teamKey, eventKey),
                                        )
                                    },
                                    onNavigateToDistrict = { districtKey ->
                                        navigator.navigate(Screen.DistrictDetail(districtKey))
                                    },
                                    onNavigateToPitMap = { eventKey, highlightedTeamKeys ->
                                        navigator.navigate(
                                            Screen.PitMap(eventKey, highlightedTeamKeys),
                                        )
                                    },
                                    initialTab = eventDetail.initialTab,
                                )
                            }
                            entry<Screen.MatchDetail> { matchDetail ->
                                val viewModel: MatchDetailViewModel =
                                    hiltViewModel(
                                        creationCallback = { f: MatchDetailViewModel.Factory ->
                                            f.create(matchDetail)
                                        },
                                    )
                                MatchDetailScreen(
                                    viewModel = viewModel,
                                    onNavigateUp = { navigator.navigateUp() },
                                    onNavigateToTeamEvent = { teamKey, eventKey ->
                                        navigator.navigate(
                                            Screen.TeamEventDetail(teamKey, eventKey),
                                        )
                                    },
                                    onNavigateToEvent = { eventKey ->
                                        navigator.navigate(Screen.EventDetail(eventKey))
                                    },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                )
                            }
                            entry<Screen.DistrictDetail> { districtDetail ->
                                val viewModel: DistrictDetailViewModel =
                                    hiltViewModel(
                                        creationCallback = { f: DistrictDetailViewModel.Factory ->
                                            f.create(districtDetail)
                                        },
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
                                val viewModel =
                                    hiltViewModel<TeamDetailViewModel, TeamDetailViewModel.Factory>(
                                        creationCallback = { factory ->
                                            factory.create(teamDetail)
                                        },
                                    )
                                TeamDetailScreen(
                                    viewModel = viewModel,
                                    onNavigateUp = { navigator.navigateUp() },
                                    onNavigateToEvent = { eventKey ->
                                        navigator.navigate(Screen.EventDetail(eventKey))
                                    },
                                    onNavigateToMyTBA = { navigator.navigate(Screen.MyTBA) },
                                    onNavigateToTeamEvent = { teamKey, eventKey ->
                                        navigator.navigate(
                                            Screen.TeamEventDetail(teamKey, eventKey),
                                        )
                                    },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                    initialTab = teamDetail.initialTab,
                                )
                            }
                            entry<Screen.TeamEventDetail> { teamEventDetail ->
                                val viewModel: TeamEventDetailViewModel =
                                    hiltViewModel(
                                        creationCallback = { f: TeamEventDetailViewModel.Factory ->
                                            f.create(teamEventDetail)
                                        },
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
                                    onNavigateToEvent = { eventKey, initialTab ->
                                        navigator.navigate(Screen.EventDetail(eventKey, initialTab))
                                    },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                    onNavigateToPitMap = { eventKey, highlightedTeamKeys ->
                                        navigator.navigate(
                                            Screen.PitMap(eventKey, highlightedTeamKeys),
                                        )
                                    },
                                )
                            }
                            entry<Screen.PitMap> { pitMap ->
                                val viewModel: PitMapViewModel =
                                    hiltViewModel(
                                        creationCallback = { f: PitMapViewModel.Factory ->
                                            f.create(pitMap)
                                        },
                                    )
                                PitMapScreen(
                                    viewModel = viewModel,
                                    onNavigateUp = { navigator.navigateUp() },
                                )
                            }
                            entry<Screen.EventPitMap> { eventPitMap ->
                                val pitMapKey =
                                    Screen.PitMap(
                                        eventKey = eventPitMap.eventKey,
                                        highlightedTeamKeys =
                                            eventPitMap.teamsCsv
                                                .split(",")
                                                .map { it.trim() }
                                                .filter { it.isNotEmpty() },
                                    )
                                val viewModel: PitMapViewModel =
                                    hiltViewModel(
                                        creationCallback = { f: PitMapViewModel.Factory ->
                                            f.create(pitMapKey)
                                        },
                                    )
                                PitMapScreen(
                                    viewModel = viewModel,
                                    onNavigateUp = { navigator.navigateUp() },
                                )
                            }
                        },
                ),
        )

        AnimatedVisibility(
            visible = showBottomBar,
            enter =
                slideInVertically(
                    animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                    initialOffsetY = { it },
                ),
            exit =
                slideOutVertically(
                    animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                    targetOffsetY = { it },
                ),
        ) {
            TBABottomBar(
                currentRoute = navState.currentRoute,
                onNavigate = { navigator.navigate(it) },
                onReselect = {
                    coroutineScope.launch {
                        tabReselectFlows.reselectFlowFor(navState.currentRoute)?.emit(Unit)
                    }
                },
            )
        }
    }
}
