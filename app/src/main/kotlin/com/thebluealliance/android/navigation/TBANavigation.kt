package com.thebluealliance.android.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowWidthSizeClass
import com.thebluealliance.android.MainActivity
import com.thebluealliance.android.ui.MORE_SUB_SCREENS
import com.thebluealliance.android.ui.RAIL_BOTTOM_DESTINATIONS
import com.thebluealliance.android.ui.RAIL_PRIMARY_DESTINATIONS
import com.thebluealliance.android.ui.RAIL_SECONDARY_DESTINATIONS
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS
import com.thebluealliance.android.ui.districts.DistrictDetailScreen
import com.thebluealliance.android.ui.districts.DistrictDetailViewModel
import com.thebluealliance.android.ui.districts.DistrictsScreen
import com.thebluealliance.android.ui.events.EventsScreen
import com.thebluealliance.android.ui.events.detail.EventDetailScreen
import com.thebluealliance.android.ui.events.detail.EventDetailViewModel
import com.thebluealliance.android.ui.isDestinationSelected
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TBANavigation(
    navState: NavigationState,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current as MainActivity
    val navigator = remember { Navigator(navState, activity) }

    val showNavigation =
        navState.currentRoute in TOP_LEVEL_DESTINATIONS.map { it.key }.toSet() ||
            navState.currentRoute in MORE_SUB_SCREENS

    val coroutineScope = rememberCoroutineScope()
    val tabReselectFlows =
        remember {
            TOP_LEVEL_DESTINATIONS
                .map { it.key }
                .associateWith { MutableSharedFlow<Unit>() }
        }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val useRail =
        windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    if (useRail) {
        TabletLayout(
            navState = navState,
            navigator = navigator,
            showNavigation = showNavigation,
            tabReselectFlows = tabReselectFlows,
            coroutineScope = coroutineScope,
            modifier = modifier,
            activity = activity,
        )
    } else {
        PhoneLayout(
            navState = navState,
            navigator = navigator,
            showNavigation = showNavigation,
            tabReselectFlows = tabReselectFlows,
            coroutineScope = coroutineScope,
            modifier = modifier,
            activity = activity,
        )
    }
}

@Composable
private fun PhoneLayout(
    navState: NavigationState,
    navigator: Navigator,
    showNavigation: Boolean,
    tabReselectFlows: Map<out Any, MutableSharedFlow<Unit>>,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    modifier: Modifier,
    activity: MainActivity,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        NavDisplayContent(
            navState = navState,
            navigator = navigator,
            tabReselectFlows = tabReselectFlows,
            modifier = Modifier.weight(1f).fillMaxSize(),
            activity = activity,
        )

        AnimatedVisibility(
            visible = showNavigation,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            NavigationBar {
                TOP_LEVEL_DESTINATIONS.forEach { dest ->
                    val selected =
                        isDestinationSelected(dest, navState.currentRoute)
                    NavigationBarItem(
                        selected = selected,
                        onClick =
                            dropUnlessResumed {
                                if (navState.currentRoute == dest.key) {
                                    coroutineScope.launch {
                                        tabReselectFlows[navState.currentRoute]
                                            ?.emit(Unit)
                                    }
                                } else {
                                    navigator.navigate(dest.key)
                                }
                            },
                        icon = {
                            Icon(
                                imageVector =
                                    if (selected) {
                                        dest.selectedIcon
                                    } else {
                                        dest.unselectedIcon
                                    },
                                contentDescription = dest.label,
                            )
                        },
                        label = { Text(dest.label) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabletLayout(
    navState: NavigationState,
    navigator: Navigator,
    showNavigation: Boolean,
    tabReselectFlows: Map<out Any, MutableSharedFlow<Unit>>,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    modifier: Modifier,
    activity: MainActivity,
) {
    val railState =
        rememberWideNavigationRailState(
            initialValue = WideNavigationRailValue.Collapsed,
        )

    Row(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        AnimatedVisibility(visible = showNavigation) {
            WideNavigationRail(
                state = railState,
                windowInsets = WindowInsets(0),
                arrangement = Arrangement.Top,
                header = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch { railState.toggle() }
                        },
                    ) {
                        Icon(
                            imageVector =
                                if (railState.targetValue ==
                                    WideNavigationRailValue.Expanded
                                ) {
                                    Icons.Default.MenuOpen
                                } else {
                                    Icons.Default.Menu
                                },
                            contentDescription = "Toggle navigation",
                        )
                    }
                },
            ) {
                val isExpanded =
                    railState.targetValue ==
                        WideNavigationRailValue.Expanded

                RAIL_PRIMARY_DESTINATIONS.forEach { dest ->
                    RailItem(
                        dest,
                        navState,
                        isExpanded,
                        coroutineScope,
                        tabReselectFlows,
                        navigator,
                    )
                }

                Spacer(Modifier.weight(1f))

                if (isExpanded) {
                    RAIL_SECONDARY_DESTINATIONS.forEach { dest ->
                        RailItem(
                            dest,
                            navState,
                            isExpanded,
                            coroutineScope,
                            tabReselectFlows,
                            navigator,
                        )
                    }
                }

                RAIL_BOTTOM_DESTINATIONS.forEach { dest ->
                    RailItem(
                        dest,
                        navState,
                        isExpanded,
                        coroutineScope,
                        tabReselectFlows,
                        navigator,
                    )
                }
            }
        }

        NavDisplayContent(
            navState = navState,
            navigator = navigator,
            tabReselectFlows = tabReselectFlows,
            modifier = Modifier.weight(1f).fillMaxSize(),
            activity = activity,
        )
    }
}

@Composable
private fun NavDisplayContent(
    navState: NavigationState,
    navigator: Navigator,
    tabReselectFlows: Map<out Any, MutableSharedFlow<Unit>>,
    modifier: Modifier,
    activity: MainActivity,
) {
    NavDisplay(
        modifier =
            modifier.background(MaterialTheme.colorScheme.background),
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
        },
        onBack = { navigator.goBack() },
        entries =
            navState.toEntries(
                entryProvider =
                    entryProvider {
                        entry<Screen.Events>(
                            metadata = Transitions.topLevelTransitionSpec,
                        ) {
                            EventsScreen(
                                onNavigateToEvent = { eventKey ->
                                    navigator.navigate(
                                        Screen.EventDetail(eventKey),
                                    )
                                },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                                reselectFlow =
                                    tabReselectFlows[Screen.Events]
                                        ?: emptyFlow(),
                            )
                        }
                        entry<Screen.Teams>(
                            metadata = Transitions.topLevelTransitionSpec,
                        ) {
                            TeamsScreen(
                                onNavigateToTeam = { teamKey ->
                                    navigator.navigate(
                                        Screen.TeamDetail(teamKey),
                                    )
                                },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                                reselectFlow =
                                    tabReselectFlows[Screen.Teams]
                                        ?: emptyFlow(),
                            )
                        }
                        entry<Screen.Districts>(
                            metadata = Transitions.topLevelTransitionSpec,
                        ) {
                            DistrictsScreen(
                                onNavigateToDistrict = { districtKey ->
                                    navigator.navigate(
                                        Screen.DistrictDetail(districtKey),
                                    )
                                },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                                reselectFlow =
                                    tabReselectFlows[Screen.Districts]
                                        ?: emptyFlow(),
                            )
                        }
                        entry<Screen.RegionalAdvancement>(
                            metadata = Transitions.topLevelTransitionSpec,
                        ) {
                            RegionalAdvancementScreen(
                                onNavigateToTeam = { teamKey ->
                                    navigator.navigate(
                                        Screen.TeamDetail(teamKey),
                                    )
                                },
                                onNavigateToEvent = { eventKey ->
                                    navigator.navigate(
                                        Screen.EventDetail(eventKey),
                                    )
                                },
                                onNavigateUp = { navigator.navigateUp() },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                                reselectFlow =
                                    tabReselectFlows[
                                        Screen.RegionalAdvancement,
                                    ]
                                        ?: emptyFlow(),
                            )
                        }
                        entry<Screen.More>(
                            metadata = Transitions.topLevelTransitionSpec,
                        ) {
                            MoreScreen(
                                onNavigateToMyTBA = {
                                    navigator.navigate(Screen.MyTBA)
                                },
                                onNavigateToRegionalAdvancement = {
                                    navigator.navigate(
                                        Screen.RegionalAdvancement,
                                    )
                                },
                                onNavigateToSettings = {
                                    navigator.navigate(Screen.Settings)
                                },
                                onNavigateToAbout = {
                                    navigator.navigate(Screen.About)
                                },
                                onNavigateToThanks = {
                                    navigator.navigate(Screen.Thanks)
                                },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                            )
                        }
                        entry<Screen.MyTBA>(
                            metadata = Transitions.topLevelTransitionSpec,
                        ) {
                            MyTBAScreen(
                                onSignIn = { activity.startGoogleSignIn() },
                                onNavigateToTeam = { teamKey ->
                                    navigator.navigate(
                                        Screen.TeamDetail(teamKey),
                                    )
                                },
                                onNavigateToEvent = { eventKey ->
                                    navigator.navigate(
                                        Screen.EventDetail(eventKey),
                                    )
                                },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                                onNavigateUp = { navigator.navigateUp() },
                                reselectFlow =
                                    tabReselectFlows[Screen.MyTBA]
                                        ?: emptyFlow(),
                            )
                        }
                        entry<Screen.Settings>(
                            metadata = Transitions.topLevelTransitionSpec,
                        ) {
                            SettingsScreen(
                                onNavigateUp = { navigator.navigateUp() },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                            )
                        }
                        entry<Screen.About>(
                            metadata = Transitions.topLevelTransitionSpec,
                        ) {
                            AboutScreen(
                                onNavigateUp = { navigator.navigateUp() },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                            )
                        }
                        entry<Screen.Thanks>(
                            metadata = Transitions.topLevelTransitionSpec,
                        ) {
                            ThanksScreen(
                                onNavigateUp = { navigator.navigateUp() },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                            )
                        }
                        entry<Screen.Search> {
                            SearchScreen(
                                onNavigateUp = { navigator.navigateUp() },
                                onNavigateToTeam = { teamKey ->
                                    navigator.navigate(
                                        Screen.TeamDetail(teamKey),
                                    )
                                },
                                onNavigateToEvent = { eventKey ->
                                    navigator.navigate(
                                        Screen.EventDetail(eventKey),
                                    )
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
                                    navigator.navigate(
                                        Screen.TeamDetail(teamKey),
                                    )
                                },
                                onNavigateToMatch = { matchKey ->
                                    navigator.navigate(
                                        Screen.MatchDetail(matchKey),
                                    )
                                },
                                onNavigateToMyTBA = {
                                    navigator.navigate(Screen.MyTBA)
                                },
                                onNavigateToTeamEvent = {
                                    teamKey,
                                    eventKey,
                                    ->
                                    navigator.navigate(
                                        Screen.TeamEventDetail(
                                            teamKey,
                                            eventKey,
                                        ),
                                    )
                                },
                                onNavigateToDistrict = { districtKey ->
                                    navigator.navigate(
                                        Screen.DistrictDetail(districtKey),
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
                                onNavigateToTeamEvent = {
                                    teamKey,
                                    eventKey,
                                    ->
                                    navigator.navigate(
                                        Screen.TeamEventDetail(
                                            teamKey,
                                            eventKey,
                                        ),
                                    )
                                },
                                onNavigateToEvent = { eventKey ->
                                    navigator.navigate(
                                        Screen.EventDetail(eventKey),
                                    )
                                },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                            )
                        }
                        entry<Screen.DistrictDetail> { districtDetail ->
                            val viewModel: DistrictDetailViewModel =
                                hiltViewModel<
                                    DistrictDetailViewModel,
                                    DistrictDetailViewModel.Factory,
                                >(
                                    creationCallback = { factory ->
                                        factory.create(districtDetail)
                                    },
                                )
                            DistrictDetailScreen(
                                viewModel = viewModel,
                                onNavigateUp = { navigator.navigateUp() },
                                onNavigateToEvent = { eventKey ->
                                    navigator.navigate(
                                        Screen.EventDetail(eventKey),
                                    )
                                },
                                onNavigateToTeam = { teamKey ->
                                    navigator.navigate(
                                        Screen.TeamDetail(teamKey),
                                    )
                                },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                            )
                        }
                        entry<Screen.TeamDetail> { teamDetail ->
                            val viewModel =
                                hiltViewModel<
                                    TeamDetailViewModel,
                                    TeamDetailViewModel.Factory,
                                >(
                                    creationCallback = { factory ->
                                        factory.create(teamDetail)
                                    },
                                )
                            TeamDetailScreen(
                                viewModel = viewModel,
                                onNavigateUp = { navigator.navigateUp() },
                                onNavigateToEvent = { eventKey ->
                                    navigator.navigate(
                                        Screen.EventDetail(eventKey),
                                    )
                                },
                                onNavigateToMyTBA = {
                                    navigator.navigate(Screen.MyTBA)
                                },
                                onNavigateToTeamEvent = {
                                    teamKey,
                                    eventKey,
                                    ->
                                    navigator.navigate(
                                        Screen.TeamEventDetail(
                                            teamKey,
                                            eventKey,
                                        ),
                                    )
                                },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                                initialTab = teamDetail.initialTab,
                            )
                        }
                        entry<Screen.TeamEventDetail> { teamEventDetail ->
                            val viewModel: TeamEventDetailViewModel =
                                hiltViewModel<
                                    TeamEventDetailViewModel,
                                    TeamEventDetailViewModel.Factory,
                                >(
                                    creationCallback = { factory ->
                                        factory.create(teamEventDetail)
                                    },
                                )
                            TeamEventDetailScreen(
                                viewModel = viewModel,
                                onNavigateUp = { navigator.navigateUp() },
                                onNavigateToMatch = { matchKey ->
                                    navigator.navigate(
                                        Screen.MatchDetail(matchKey),
                                    )
                                },
                                onNavigateToTeam = { teamKey ->
                                    navigator.navigate(
                                        Screen.TeamDetail(teamKey),
                                    )
                                },
                                onNavigateToEvent = { eventKey, initialTab ->
                                    navigator.navigate(
                                        Screen.EventDetail(
                                            eventKey,
                                            initialTab,
                                        ),
                                    )
                                },
                                onNavigateToSearch = {
                                    navigator.navigate(Screen.Search)
                                },
                            )
                        }
                    },
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RailItem(
    dest: com.thebluealliance.android.ui.TopLevelDestination,
    navState: NavigationState,
    railExpanded: Boolean,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    tabReselectFlows: Map<out Any, MutableSharedFlow<Unit>>,
    navigator: Navigator,
) {
    val selected = isDestinationSelected(dest, navState.currentRoute)
    WideNavigationRailItem(
        railExpanded = railExpanded,
        selected = selected,
        onClick = {
            if (navState.currentRoute == dest.key) {
                coroutineScope.launch {
                    tabReselectFlows[navState.currentRoute]?.emit(Unit)
                }
            } else {
                navigator.navigate(dest.key)
            }
        },
        icon = {
            Icon(
                imageVector =
                    if (selected) dest.selectedIcon else dest.unselectedIcon,
                contentDescription = dest.label,
            )
        },
        label = { Text(dest.label) },
    )
}
