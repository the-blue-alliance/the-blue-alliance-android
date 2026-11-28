package com.thebluealliance.android.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.thebluealliance.android.MainActivity
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS
import com.thebluealliance.android.ui.common.LocalUserMessageInsets
import com.thebluealliance.android.ui.common.UserMessageHost
import com.thebluealliance.android.ui.common.UserMessageState
import com.thebluealliance.android.ui.common.rememberUserMessageState
import com.thebluealliance.android.ui.components.FocusReturn
import com.thebluealliance.android.ui.components.LocalNavigationRailShown
import com.thebluealliance.android.ui.components.LocalRefreshTargets
import com.thebluealliance.android.ui.components.TBABottomBar
import com.thebluealliance.android.ui.components.TBANavigationRail
import com.thebluealliance.android.ui.components.WindowFocusEntry
import com.thebluealliance.android.ui.components.focusEntersAt
import com.thebluealliance.android.ui.components.focusRegion
import com.thebluealliance.android.ui.components.isSelectedFor
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.useNavigationRail
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
import com.thebluealliance.android.ui.more.MoreViewModel
import com.thebluealliance.android.ui.more.ThanksScreen
import com.thebluealliance.android.ui.mytba.MyTBAScreen
import com.thebluealliance.android.ui.regional.RegionalAdvancementScreen
import com.thebluealliance.android.ui.regional.RegionalAdvancementViewModel
import com.thebluealliance.android.ui.search.SearchScreen
import com.thebluealliance.android.ui.settings.SettingsScreen
import com.thebluealliance.android.ui.teamevent.TeamEventDetailScreen
import com.thebluealliance.android.ui.teamevent.TeamEventDetailViewModel
import com.thebluealliance.android.ui.teams.TeamDetailScreen
import com.thebluealliance.android.ui.teams.TeamDetailViewModel
import com.thebluealliance.android.ui.teams.TeamsScreen
import com.thebluealliance.android.ui.theme.StatusStripGray
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
    val focusReturn = remember { FocusReturn() }
    val navigator = remember { Navigator(navState, activity, focusReturn) }

    // Deeplinks and notification taps that arrive while the app is already running reach
    // MainActivity.onNewIntent rather than onCreate, so they navigate the live back stack here
    // instead of setting a start route.
    val matchBesideEvent by rememberUpdatedState(
        showsEventMatchesTwoPane(currentWindowAdaptiveInfo().windowSizeClass),
    )
    LaunchedEffect(navigator) {
        activity.intentRoutes.collect { navigator.navigateFromIntent(it, matchBesideEvent) }
    }
    val refreshTargets = LocalRefreshTargets.current
    val coroutineScope = rememberCoroutineScope()
    val tabReselectFlows =
        remember {
            TOP_LEVEL_DESTINATIONS
                .map { it.key }
                .associateWith { MutableSharedFlow<Unit>() }
        }

    val onReselect: () -> Unit = {
        coroutineScope.launch {
            tabReselectFlows.reselectFlowFor(navState.currentRoute)?.emit(Unit)
        }
    }

    LaunchedEffect(navigator) {
        activity.keyboardShortcuts.collect {
            when (it) {
                // Already on Search, this is a no-op, so focus stays in the search field.
                ShortcutAction.Search -> navigator.navigate(Screen.Search)
                ShortcutAction.Refresh -> refreshTargets?.refresh()
                ShortcutAction.Back -> activity.onBackPressedDispatcher.keyboardBack()
                // As tapping the tab: on it, its screen scrolls to the top; otherwise it opens.
                is ShortcutAction.SwitchTab -> {
                    val tab = TOP_LEVEL_DESTINATIONS[it.index].key
                    if (tab.isSameTab(navState.currentRoute)) {
                        onReselect()
                    } else {
                        navigator.navigate(tab)
                    }
                }
            }
        }
    }

    // Top-level screens, and the screens opened from More, which keep its tab highlighted.
    val showBottomBar = TOP_LEVEL_DESTINATIONS.any { it.isSelectedFor(navState.currentRoute) }

    // Once in use, the rail stays put on every screen, so pushing or popping a detail screen
    // doesn't slide it out and reflow the content beside it.
    val useRail = useNavigationRail(currentWindowAdaptiveInfo().windowSizeClass)

    val bottomBarShown = showBottomBar && !useRail
    // The rail or the bottom bar, whichever shows: keyboard focus starts there.
    val focusEntry = remember { WindowFocusEntry() }
    val userMessages = rememberUserMessageState()

    NavigationFrame(
        showRail = useRail,
        navigationShown = useRail || bottomBarShown,
        focusEntry = focusEntry,
        userMessages = userMessages,
        rail = {
            TBANavigationRail(
                currentRoute = navState.currentRoute,
                stackRoot = navState.topLevelRoute,
                onTopLevelScreen = showBottomBar,
                onNavigate = { navigator.navigate(it) },
                onReselect = onReselect,
            )
        },
        modifier =
            modifier
                .fillMaxSize()
                // Shortcuts the focused element leaves unhandled, before Compose spends arrow keys
                // (Alt+Left) on moving focus. With nothing focused, MainActivity gets them itself.
                .onKeyEvent { activity.onShortcutKey(it.nativeKeyEvent) }
                .background(MaterialTheme.colorScheme.background),
    ) {
        NavDisplay(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    // The bottom bar takes the bottom inset; with none, screens pad their content by it.
                    .then(
                        if (bottomBarShown) {
                            Modifier.consumeWindowInsets(
                                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
                            )
                        } else {
                            Modifier
                        },
                    )
                    // Keeps sliding screens from drawing over the rail beside them.
                    .clipToBounds()
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
            sceneStrategies = listOf(rememberEventMatchesSceneStrategy()),
            entries =
                navState.toEntries(
                    focusReturn = focusReturn,
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
                            ) { regional ->
                                val viewModel =
                                    hiltViewModel<
                                        RegionalAdvancementViewModel,
                                        RegionalAdvancementViewModel.Factory,
                                    >(creationCallback = { factory -> factory.create(regional) })
                                RegionalAdvancementScreen(
                                    viewModel = viewModel,
                                    onNavigateToTeam = { teamKey ->
                                        navigator.navigate(Screen.TeamDetail(teamKey))
                                    },
                                    onNavigateToEvent = { eventKey ->
                                        navigator.navigate(Screen.EventDetail(eventKey))
                                    },
                                    onNavigateUp = { navigator.navigateUp() },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                    // Not a tab: tapping More pops back to it instead.
                                    reselectFlow = emptyFlow(),
                                )
                            }
                            entry<Screen.More>(
                                metadata = Transitions.topLevelTransitionSpec,
                            ) {
                                val moreViewModel: MoreViewModel = hiltViewModel()
                                val sponsors by moreViewModel.sponsors.collectAsStateWithLifecycle()
                                MoreScreen(
                                    onNavigateToMyTBA = { navigator.navigate(Screen.MyTBA) },
                                    onNavigateToRegionalAdvancement = {
                                        navigator.navigate(Screen.RegionalAdvancement())
                                    },
                                    onNavigateToSettings = { navigator.navigate(Screen.Settings) },
                                    onNavigateToAbout = { navigator.navigate(Screen.About) },
                                    onNavigateToThanks = { navigator.navigate(Screen.Thanks) },
                                    onNavigateToSearch = { navigator.navigate(Screen.Search) },
                                    sponsors = sponsors,
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
                                    onNavigateToMatch = { matchKey ->
                                        navigator.navigate(Screen.MatchDetail(matchKey))
                                    },
                                    onNavigateToTeamAtEvent = { teamKey, eventKey ->
                                        navigator.navigate(
                                            Screen.TeamEventDetail(teamKey, eventKey),
                                        )
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
                            entry<Screen.EventDetail>(
                                metadata = { eventMatchesListPane(it.eventKey) },
                            ) { eventDetail ->
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
                                    // Replaces a match already in the detail pane.
                                    onNavigateToMatch = { matchKey ->
                                        navigator.state.currentStack.selectMatch(
                                            Screen.MatchDetail(matchKey),
                                        )
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
                            entry<Screen.MatchDetail>(
                                metadata = { matchDetailPane(it.matchKey) },
                            ) { matchDetail ->
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

        UserMessageHost(
            state = userMessages,
            padNavigationBars = !bottomBarShown,
            screen = navState.currentRoute,
        )

        AnimatedVisibility(
            visible = bottomBarShown,
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
                modifier = Modifier.focusRegion(focusEntry.navigation),
                currentRoute = navState.currentRoute,
                onNavigate = { navigator.navigate(it) },
                onReselect = onReselect,
            )
        }
    }
}

/**
 * Lays [rail] out to the start of [content], which stacks the screen above any bottom bar. The frame
 * leaves the bottom inset to the screens, so their backgrounds reach the window's bottom edge.
 *
 * Keyboard focus moves through the frame by region: the navigation ([focusEntry], the rail or a
 * bottom bar placed in [content]) first, then the screen's top bar and body, then any second
 * pane. Tab wraps from the end back to the navigation.
 */
@Composable
private fun NavigationFrame(
    showRail: Boolean,
    navigationShown: Boolean,
    focusEntry: WindowFocusEntry,
    userMessages: UserMessageState,
    rail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    CompositionLocalProvider(LocalNavigationRailShown provides showRail) {
        Row(
            modifier =
                modifier
                    .then(if (showRail) Modifier.statusBarStrip() else Modifier)
                    .focusEntersAt(focusEntry, navigationShown),
        ) {
            AnimatedVisibility(
                visible = showRail,
                enter =
                    slideInHorizontally(
                        animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                        initialOffsetX = { -it },
                    ),
                exit =
                    slideOutHorizontally(
                        animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                        targetOffsetX = { -it },
                    ),
            ) {
                Box(Modifier.focusRegion(focusEntry.navigation)) { rail() }
            }
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .focusRegion()
                        .pageBackground()
                        .then(
                            // The rail owns the start inset.
                            if (showRail) {
                                Modifier.consumeWindowInsets(
                                    WindowInsets.safeDrawing.only(WindowInsetsSides.Start),
                                )
                            } else {
                                Modifier
                            },
                        ),
            ) {
                // A snackbar with buttons covers the bottom of the screen: screens pad their lists
                // by it, and focused items scroll into view above it.
                val parentSpec = LocalBringIntoViewSpec.current
                CompositionLocalProvider(
                    LocalUserMessageInsets provides userMessages.insets,
                    LocalBringIntoViewSpec provides
                        remember(parentSpec) { userMessages.bringIntoViewSpec(parentSpec) },
                ) { content() }
            }
        }
    }
}

/**
 * Paints [StatusStripGray] behind the status bar across the window, over the rail and the bars, so
 * the light status icons sit on their own band rather than on the rail's edge against the blue bar.
 */
@Composable
private fun Modifier.statusBarStrip(): Modifier {
    val top = WindowInsets.statusBars.getTop(LocalDensity.current).toFloat()
    return drawWithContent {
        drawContent()
        drawRect(StatusStripGray, size = Size(size.width, top))
    }
}
