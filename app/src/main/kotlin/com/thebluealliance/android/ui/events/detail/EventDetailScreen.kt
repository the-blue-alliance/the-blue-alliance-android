package com.thebluealliance.android.ui.events.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.shortcuts.ReportShortcutVisitEffect
import com.thebluealliance.android.ui.common.ErrorBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.common.TouchPullToRefreshBox
import com.thebluealliance.android.ui.common.shareTbaUrl
import com.thebluealliance.android.ui.components.LocalMatchDetailPaneHost
import com.thebluealliance.android.ui.components.NotificationPreferencesSheet
import com.thebluealliance.android.ui.components.TBATab
import com.thebluealliance.android.ui.components.TBATabRow
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarTitle
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.notificationPreferencesAction
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.pagerPageFocus
import com.thebluealliance.android.ui.events.detail.tabs.EventAdvancementTab
import com.thebluealliance.android.ui.events.detail.tabs.EventAlliancesTab
import com.thebluealliance.android.ui.events.detail.tabs.EventAwardsTab
import com.thebluealliance.android.ui.events.detail.tabs.EventInfoTab
import com.thebluealliance.android.ui.events.detail.tabs.EventInsightsTab
import com.thebluealliance.android.ui.events.detail.tabs.EventMatchesTab
import com.thebluealliance.android.ui.events.detail.tabs.EventRankingsTab
import com.thebluealliance.android.ui.events.detail.tabs.EventTeamsTab
import com.thebluealliance.android.ui.theme.TbaIcons
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

enum class EventDetailTab(
    val readableName: (Event?) -> String,
) {
    INFO({ "Info" }),
    TEAMS({ "Teams" }),
    RANKINGS({ "Rankings" }),
    MATCHES({ "Matches" }),
    ALLIANCES({ "Alliances" }),
    INSIGHTS({ "Insights" }),
    ADVANCEMENT_POINTS({ if (it?.district != null) "District Points" else "Regional Points" }),
    AWARDS({ "Awards" }),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToTeam: (String) -> Unit = {},
    onNavigateToMatch: (String) -> Unit = {},
    onNavigateToMyTBA: () -> Unit = {},
    onNavigateToTeamEvent: (teamKey: String, eventKey: String) -> Unit = { _, _ -> },
    onNavigateToDistrict: (districtKey: String) -> Unit = {},
    onNavigateToPitMap: (eventKey: String, highlightedTeamKeys: List<String>) -> Unit = { _, _ -> },
    initialTab: EventDetailTab = EventDetailTab.INFO,
    viewModel: EventDetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val subscription by viewModel.subscription.collectAsStateWithLifecycle()
    // A match selected in the two-pane layout belongs to the Matches tab: open on it, and
    // leaving the tab clears the selection rather than hiding it.
    val paneHost = LocalMatchDetailPaneHost.current
    val hasSelectedMatch = paneHost?.selectedMatchKey != null
    val firstTab = if (hasSelectedMatch) EventDetailTab.MATCHES else initialTab
    val pagerState =
        rememberPagerState(
            initialPage = firstTab.ordinal,
            pageCount = { EventDetailTab.entries.size },
        )
    LaunchedEffect(hasSelectedMatch) {
        if (hasSelectedMatch) pagerState.scrollToPage(EventDetailTab.MATCHES.ordinal)
    }
    val currentPaneHost by rememberUpdatedState(paneHost)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.drop(1).collect { page ->
            val host = currentPaneHost
            if (page != EventDetailTab.MATCHES.ordinal && host?.selectedMatchKey != null) {
                host.onClearSelection()
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()

    var showSignInDialog by rememberSaveable { mutableStateOf(false) }
    var showNotificationSheet by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.showSignInPrompt.collect { showSignInDialog = true }
    }
    ReportShortcutVisitEffect(uiState.event?.key)

    if (showSignInDialog) {
        AlertDialog(
            onDismissRequest = { showSignInDialog = false },
            title = { Text("Sign in required") },
            text = { Text("Sign in to save your favorite teams and events.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignInDialog = false
                        onNavigateToMyTBA()
                    },
                    modifier = Modifier.handCursor(),
                ) { Text("Sign in") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSignInDialog = false
                    },
                    modifier = Modifier.handCursor(),
                ) { Text("Cancel") }
            },
        )
    }

    if (showNotificationSheet) {
        NotificationPreferencesSheet(
            displayName = uiState.event?.let { "${it.year} ${it.name}" } ?: "Event",
            modelType = ModelType.EVENT,
            isFavorite = isFavorite,
            currentNotifications = subscription?.notifications ?: emptyList(),
            onSave = { favorite, notifications ->
                viewModel.updatePreferences(favorite, notifications)
                showNotificationSheet = false
            },
            onDismiss = { showNotificationSheet = false },
        )
    }

    Scaffold(
        topBar = {
            Column {
                TBATopAppBar(
                    onRefresh = {
                        viewModel.refreshTab(
                            EventDetailTab.entries[pagerState.currentPage],
                        )
                    },
                    isRefreshing = isRefreshing && uiState.event != null,
                    title = {
                        TopBarTitle(
                            text = uiState.event?.let { "${it.year} ${it.name}" } ?: "Event",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp, modifier = Modifier.handCursor()) {
                            Icon(
                                imageVector = TbaIcons.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    secondaryActions =
                        listOf(
                            notificationPreferencesAction(
                                notificationsOn = subscription?.notifications?.isNotEmpty() == true,
                                onClick = {
                                    if (!viewModel.isSignedIn()) {
                                        showSignInDialog = true
                                    } else {
                                        showNotificationSheet = true
                                    }
                                },
                            ),
                            TopBarAction(
                                label =
                                    if (isFavorite) "Remove from favorites" else "Add to favorites",
                                icon = if (isFavorite) TbaIcons.Star else TbaIcons.StarOutlined,
                                onClick = viewModel::toggleFavorite,
                            ),
                        ),
                    overflowActions =
                        listOfNotNull(
                            uiState.event?.let { event ->
                                TopBarAction(
                                    label = "Share",
                                    icon = TbaIcons.Share,
                                    onClick = {
                                        context.shareTbaUrl(
                                            title = "${event.year} ${event.name}",
                                            url =
                                                "https://www.thebluealliance.com/event/" +
                                                    event.key,
                                        )
                                    },
                                )
                            },
                            if (viewModel.canPinShortcuts) {
                                TopBarAction(
                                    label = "Add to home screen",
                                    icon = TbaIcons.AddToHomeScreen,
                                    onClick = viewModel::requestPinShortcut,
                                )
                            } else {
                                null
                            },
                        ),
                )

                TBATabRow(selectedTabIndex = pagerState.currentPage) {
                    val teamCount = uiState.teamCount
                    EventDetailTab.entries.forEach { tab ->
                        TBATab(
                            label = tab.readableName(uiState.event),
                            selected = pagerState.currentPage == tab.ordinal,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        tab.ordinal,
                                    )
                                }
                            },
                            trailing =
                                if (tab == EventDetailTab.TEAMS && teamCount != null) {
                                    {
                                        Surface(
                                            shape = MaterialTheme.shapes.small,
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor =
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                        ) {
                                            Text(
                                                text = "$teamCount",
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier =
                                                    Modifier.padding(
                                                        horizontal = 4.dp,
                                                        vertical = 2.dp,
                                                    ),
                                            )
                                        }
                                    }
                                } else {
                                    null
                                },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        val bottomPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .pageBackground(),
        ) { page ->
            TouchPullToRefreshBox(
                isRefreshing = isRefreshing && uiState.event != null,
                onRefresh = { viewModel.refreshTab(EventDetailTab.entries[page]) },
                modifier = Modifier.fillMaxSize().pagerPageFocus(pagerState, page),
            ) {
                val tab = EventDetailTab.entries[page]
                if (uiState.loadFailed(tab)) {
                    // With no event the pull spinner is hidden, so show the retry in progress here.
                    if (isRefreshing && uiState.event == null) {
                        LoadingBox(modifier = Modifier.padding(bottomPadding))
                    } else {
                        ErrorBox(
                            modifier = Modifier.padding(bottomPadding),
                            message = tab.loadErrorMessage(uiState.event),
                            onRetry = { viewModel.refreshTab(tab) },
                        )
                    }
                    return@TouchPullToRefreshBox
                }
                when (tab) {
                    EventDetailTab.INFO ->
                        EventInfoTab(
                            event = uiState.event,
                            districtDisplayName = uiState.districtDisplayName,
                            pitLocations = uiState.pitLocations,
                            favoriteTeamKeys = uiState.favoriteTeamKeysAtEvent,
                            onNavigateToDistrict = onNavigateToDistrict,
                            onNavigateToPitMap = onNavigateToPitMap,
                            innerPadding = bottomPadding,
                        )
                    EventDetailTab.TEAMS ->
                        EventTeamsTab(
                            teams = uiState.teams,
                            pitLocations = uiState.pitLocations,
                            onNavigateToTeam = { teamKey ->
                                val eventKey = uiState.event?.key
                                if (eventKey != null) {
                                    onNavigateToTeamEvent(teamKey, eventKey)
                                } else {
                                    onNavigateToTeam(teamKey)
                                }
                            },
                            innerPadding = bottomPadding,
                        )
                    EventDetailTab.RANKINGS ->
                        EventRankingsTab(
                            rankings = uiState.rankings,
                            rankingSortOrders = uiState.rankingSortOrders,
                            rankingExtraStatsInfo = uiState.rankingExtraStatsInfo,
                            onTeamClick = { teamKey ->
                                val eventKey = uiState.event?.key
                                if (eventKey != null) onNavigateToTeamEvent(teamKey, eventKey)
                            },
                            isRefreshing = isRefreshing,
                            innerPadding = bottomPadding,
                        )
                    EventDetailTab.MATCHES ->
                        EventMatchesTab(
                            matches = uiState.matches,
                            playoffType = uiState.event?.playoffType ?: PlayoffType.OTHER,
                            onNavigateToMatch = onNavigateToMatch,
                            innerPadding = bottomPadding,
                        )
                    EventDetailTab.ALLIANCES ->
                        EventAlliancesTab(
                            alliances = uiState.alliances,
                            onTeamClick = { teamKey ->
                                val eventKey = uiState.event?.key
                                if (eventKey != null) onNavigateToTeamEvent(teamKey, eventKey)
                            },
                            isRefreshing = isRefreshing,
                            innerPadding = bottomPadding,
                        )
                    EventDetailTab.INSIGHTS ->
                        EventInsightsTab(
                            oprs = uiState.oprs,
                            coprs = uiState.coprs,
                            insights = uiState.insights,
                            isRefreshing = isRefreshing,
                            innerPadding = bottomPadding,
                        )
                    EventDetailTab.ADVANCEMENT_POINTS ->
                        EventAdvancementTab(
                            uiState.advancementPoints,
                            uiState.event,
                            uiState.teams,
                            uiState.regionalCmpAdvancementByTeam,
                            onTeamClick = { teamKey ->
                                val eventKey = uiState.event?.key
                                if (eventKey != null) {
                                    onNavigateToTeamEvent(teamKey, eventKey)
                                } else {
                                    onNavigateToTeam(teamKey)
                                }
                            },
                            innerPadding = bottomPadding,
                        )
                    EventDetailTab.AWARDS ->
                        EventAwardsTab(
                            awards = uiState.awards,
                            onTeamClick = { teamKey ->
                                val eventKey = uiState.event?.key
                                if (eventKey != null) {
                                    onNavigateToTeamEvent(teamKey, eventKey)
                                } else {
                                    onNavigateToTeam(teamKey)
                                }
                            },
                            isRefreshing = isRefreshing,
                            innerPadding = bottomPadding,
                        )
                }
            }
        }
    }
}

private fun EventDetailTab.loadErrorMessage(event: Event?): String =
    if (this ==
        EventDetailTab.INFO
    ) {
        "Couldn't load event"
    } else {
        "Couldn't load ${readableName(event).lowercase()}"
    }
