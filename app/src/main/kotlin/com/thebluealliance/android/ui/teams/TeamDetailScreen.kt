package com.thebluealliance.android.ui.teams

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.core.theme.TbaAllianceColors
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.shortcuts.ReportShortcutVisitEffect
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.ErrorBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.common.TouchPullToRefreshBox
import com.thebluealliance.android.ui.common.previousSeason
import com.thebluealliance.android.ui.common.shareTbaUrl
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.FocusHandoffEffect
import com.thebluealliance.android.ui.components.MediaTab
import com.thebluealliance.android.ui.components.NotificationPreferencesSheet
import com.thebluealliance.android.ui.components.ProseMaxWidth
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.TBATab
import com.thebluealliance.android.ui.components.TBATabRow
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarYearPicker
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.mediaUrl
import com.thebluealliance.android.ui.components.notificationPreferencesAction
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.pagerPageFocus
import com.thebluealliance.android.ui.components.rememberFocusHandoff
import com.thebluealliance.android.ui.components.verticalScrollbar
import com.thebluealliance.android.ui.theme.TbaIcons
import kotlinx.coroutines.launch

enum class TeamDetailTab(
    val readableName: String,
) {
    INFO("Info"),
    EVENTS("Events"),
    MEDIA("Media"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateToMyTBA: () -> Unit = {},
    onNavigateToTeamEvent: (teamKey: String, eventKey: String) -> Unit = { _, _ -> },
    onNavigateToSearch: () -> Unit,
    initialTab: Int = 0,
    viewModel: TeamDetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val subscription by viewModel.subscription.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val yearsParticipated by viewModel.yearsParticipated.collectAsStateWithLifecycle()
    // Empty Events and Media tabs offer a step back to the team's previous season.
    val previousYear = previousSeason(yearsParticipated, selectedYear)
    // Pressing that button by keyboard removes it; focus moves to the first event or photo of the
    // season it shows, or stays on the button when that season is empty too.
    val inputModeManager = LocalInputModeManager.current
    val eventsFocus = rememberFocusHandoff()
    val mediaFocus = rememberFocusHandoff()
    FocusHandoffEffect(
        eventsFocus,
        current = selectedYear,
        hasContent = !uiState.events.isNullOrEmpty(),
        controlShown = previousYear != null && uiState.events?.isEmpty() == true,
    )
    val hasMediaTiles =
        uiState.media?.any { it.type != "avatar" && mediaUrl(it.type, it.foreignKey) != null }
    FocusHandoffEffect(
        mediaFocus,
        current = selectedYear,
        hasContent = hasMediaTiles == true,
        controlShown = previousYear != null && uiState.media?.none { it.type != "avatar" } == true,
    )
    val pagerState =
        rememberPagerState(initialPage = initialTab, pageCount = { TeamDetailTab.entries.size })
    val coroutineScope = rememberCoroutineScope()

    var showSignInDialog by rememberSaveable { mutableStateOf(false) }
    var showNotificationSheet by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.showSignInPrompt.collect { showSignInDialog = true }
    }
    ReportShortcutVisitEffect(uiState.team?.key)

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
            displayName =
                uiState.team?.let {
                    "Team ${it.number}" + (
                        it.nickname?.let { n ->
                            " - $n"
                        } ?: ""
                    )
                }
                    ?: "Team",
            modelType = ModelType.TEAM,
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
                            TeamDetailTab.entries[pagerState.currentPage],
                        )
                    },
                    isRefreshing = isRefreshing && uiState.team != null,
                    title = {
                        val team = uiState.team
                        TopBarYearPicker(
                            selectedYear = selectedYear,
                            years = yearsParticipated,
                            onYearSelected = viewModel::selectYear,
                            title = {
                                Text(
                                    text =
                                        if (team !=
                                            null
                                        ) {
                                            "${team.number} - ${team.nickname ?: ""}"
                                        } else {
                                            "Team"
                                        },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
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
                    // A missing key has nothing to favorite, subscribe to or pin.
                    secondaryActions =
                        if (uiState.notFound) {
                            emptyList()
                        } else {
                            listOf(
                                notificationPreferencesAction(
                                    notificationsOn =
                                        subscription?.notifications?.isNotEmpty() == true,
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
                                        if (isFavorite) {
                                            "Remove from favorites"
                                        } else {
                                            "Add to favorites"
                                        },
                                    icon = if (isFavorite) TbaIcons.Star else TbaIcons.StarOutlined,
                                    onClick = viewModel::toggleFavorite,
                                ),
                            )
                        },
                    overflowActions =
                        listOfNotNull(
                            uiState.team?.let { team ->
                                TopBarAction(
                                    label = "Share",
                                    icon = TbaIcons.Share,
                                    onClick = {
                                        context.shareTbaUrl(
                                            title =
                                                "Team ${team.number} - " +
                                                    (team.nickname ?: ""),
                                            url =
                                                "https://www.thebluealliance.com/team/" +
                                                    "${team.number}",
                                        )
                                    },
                                )
                            },
                            if (viewModel.canPinShortcuts && !uiState.notFound) {
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

                // A missing team has no tabs to switch between.
                if (!uiState.notFound) {
                    TBATabRow(selectedTabIndex = pagerState.currentPage) {
                        TeamDetailTab.entries.forEach { tab ->
                            TBATab(
                                label = tab.readableName,
                                selected = pagerState.currentPage == tab.ordinal,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            tab.ordinal,
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        if (uiState.notFound) {
            // A 404 is TBA's answer, not a failed fetch, so Retry would only get it again.
            EmptyBox(
                message = "Team not found",
                modifier = Modifier.padding(innerPadding),
            )
            return@Scaffold
        }
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
                isRefreshing = isRefreshing && uiState.team != null,
                onRefresh = { viewModel.refreshTab(TeamDetailTab.entries[page]) },
                modifier = Modifier.fillMaxSize().pagerPageFocus(pagerState, page),
            ) {
                val tab = TeamDetailTab.entries[page]
                if (uiState.loadFailed(tab)) {
                    // With no team the pull spinner is hidden, so show the retry in progress here.
                    if (isRefreshing && uiState.team == null) {
                        LoadingBox(modifier = Modifier.padding(bottomPadding))
                    } else {
                        ErrorBox(
                            modifier = Modifier.padding(bottomPadding),
                            message = tab.loadErrorMessage(),
                            onRetry = { viewModel.retry(tab) },
                        )
                    }
                    return@TouchPullToRefreshBox
                }
                when (tab) {
                    TeamDetailTab.INFO ->
                        InfoTab(
                            team = uiState.team,
                            media = uiState.media,
                            innerPadding = bottomPadding,
                        )
                    TeamDetailTab.EVENTS ->
                        EventsTab(
                            events = uiState.events,
                            selectedYear = selectedYear,
                            firstEventFocus = eventsFocus.requester,
                            emptyAction =
                                previousYear?.let { year ->
                                    {
                                        ShowSeasonButton(
                                            "Show $year events",
                                            eventsFocus.requester,
                                        ) {
                                            eventsFocus.arm(year, inputModeManager.inputMode)
                                            viewModel.selectYear(year)
                                        }
                                    }
                                },
                            onNavigateToEvent = { eventKey ->
                                val teamKey = uiState.team?.key
                                if (teamKey != null) {
                                    onNavigateToTeamEvent(teamKey, eventKey)
                                } else {
                                    onNavigateToEvent(eventKey)
                                }
                            },
                            innerPadding = bottomPadding,
                        )
                    TeamDetailTab.MEDIA ->
                        MediaTab(
                            media = uiState.media,
                            innerPadding = bottomPadding,
                            firstItemFocus = mediaFocus.requester,
                            emptyAction =
                                previousYear?.let { year ->
                                    {
                                        ShowSeasonButton(
                                            "Show $year media",
                                            mediaFocus.requester,
                                        ) {
                                            mediaFocus.arm(year, inputModeManager.inputMode)
                                            viewModel.selectYear(year)
                                        }
                                    }
                                },
                        )
                }
            }
        }
    }
}

private fun TeamDetailTab.loadErrorMessage(): String =
    when (this) {
        TeamDetailTab.INFO -> "Couldn't load team"
        TeamDetailTab.EVENTS -> "Couldn't load events"
        TeamDetailTab.MEDIA -> "Couldn't load media"
    }

@Composable
private fun InfoTab(
    team: Team?,
    media: List<Media>?,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (team == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding),
        )
        return
    }
    val avatar = media?.firstOrNull { it.isAvatar }
    ReadingWidthBox(innerPadding, Modifier.fillMaxSize()) { readingPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            contentPadding = readingPadding,
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (avatar != null) {
                        val bitmap =
                            remember(avatar.base64Image) {
                                try {
                                    val bytes = Base64.decode(avatar.base64Image, Base64.DEFAULT)
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                } catch (_: Exception) {
                                    null
                                }
                            }
                        if (bitmap != null) {
                            var showRed by remember { mutableStateOf(false) }
                            val bgColor =
                                if (showRed) {
                                    TbaAllianceColors.AvatarRed
                                } else {
                                    TbaAllianceColors.AvatarBlue
                                }
                            Box(
                                modifier =
                                    Modifier
                                        .size(64.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(bgColor)
                                        .handCursor()
                                        .toggleable(
                                            value = showRed,
                                            role = Role.Switch,
                                            onValueChange = { showRed = it },
                                        ).semantics {
                                            stateDescription =
                                                if (showRed) {
                                                    "Red alliance background"
                                                } else {
                                                    "Blue alliance background"
                                                }
                                        },
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Team avatar",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(64.dp),
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                    Column {
                        Text(
                            text = "Team ${team.number}",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        if (team.nickname != null) {
                            Text(
                                text = team.nickname,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }
            }
            if (team.name != null) {
                item {
                    // The sponsor list can run several lines.
                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp).widthIn(max = ProseMaxWidth),
                    )
                }
            }
            val location = listOfNotNull(team.city, team.state, team.country).joinToString(", ")
            if (location.isNotEmpty()) {
                item {
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
            if (team.rookieYear != null) {
                item {
                    Text(
                        text = "Rookie Year: ${team.rookieYear}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun EventsTab(
    events: List<Event>?,
    selectedYear: Int,
    onNavigateToEvent: (String) -> Unit,
    firstEventFocus: FocusRequester,
    innerPadding: PaddingValues = PaddingValues.Zero,
    emptyAction: (@Composable () -> Unit)? = null,
) {
    if (events == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding),
        )
    } else if (events.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No events for $selectedYear",
            action = emptyAction,
        )
    } else {
        ReadingWidthBox(innerPadding, Modifier.fillMaxSize()) { readingPadding ->
            val scrollbarState = rememberLazyListState()
            LazyColumn(
                state = scrollbarState,
                modifier = Modifier.fillMaxSize().verticalScrollbar(scrollbarState, readingPadding),
                contentPadding = readingPadding,
            ) {
                itemsIndexed(events, key = { _, event -> event.key }) { index, event ->
                    EventRow(
                        event = event,
                        onClick = { onNavigateToEvent(event.key) },
                        modifier =
                            if (index == 0) Modifier.focusRequester(firstEventFocus) else Modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowSeasonButton(
    label: String,
    focusRequester: FocusRequester,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.focusRequester(focusRequester).handCursor().heightIn(min = 48.dp),
    ) {
        Text(label)
    }
}
