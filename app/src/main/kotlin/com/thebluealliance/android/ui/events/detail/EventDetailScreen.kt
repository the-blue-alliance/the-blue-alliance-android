package com.thebluealliance.android.ui.events.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.thebluealliance.android.ui.components.TBATopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.shortcuts.ReportShortcutVisitEffect
import com.thebluealliance.android.ui.common.shareTbaUrl
import com.thebluealliance.android.ui.components.NotificationPreferencesSheet
import com.thebluealliance.android.ui.events.detail.tabs.EventAlliancesTab
import com.thebluealliance.android.ui.events.detail.tabs.EventAwardsTab
import com.thebluealliance.android.ui.events.detail.tabs.EventDistrictPointsTab
import com.thebluealliance.android.ui.events.detail.tabs.EventInfoTab
import com.thebluealliance.android.ui.events.detail.tabs.EventMatchesTab
import com.thebluealliance.android.ui.events.detail.tabs.EventRankingsTab
import com.thebluealliance.android.ui.events.detail.tabs.EventTeamsTab
import kotlinx.coroutines.launch

private val TABS = listOf("Info", "Teams", "Matches", "Rankings", "Alliances", "Awards", "District points")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToTeam: (String) -> Unit = {},
    onNavigateToMatch: (String) -> Unit = {},
    onNavigateToMyTBA: () -> Unit = {},
    onNavigateToTeamEvent: (teamKey: String, eventKey: String) -> Unit = { _, _ -> },
    viewModel: EventDetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val subscription by viewModel.subscription.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { TABS.size })
    val coroutineScope = rememberCoroutineScope()

    var showSignInDialog by remember { mutableStateOf(false) }
    var showNotificationSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.showSignInPrompt.collect { showSignInDialog = true }
    }
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    ReportShortcutVisitEffect(uiState.event?.key)

    if (showSignInDialog) {
        AlertDialog(
            onDismissRequest = { showSignInDialog = false },
            title = { Text("Sign in required") },
            text = { Text("Sign in to save your favorite teams and events.") },
            confirmButton = {
                TextButton(onClick = {
                    showSignInDialog = false
                    onNavigateToMyTBA()
                }) { Text("Sign in") }
            },
            dismissButton = {
                TextButton(onClick = { showSignInDialog = false }) { Text("Cancel") }
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
            TBATopAppBar(
                title = {
                    Text(
                        text = uiState.event?.let { "${it.year} ${it.name}" } ?: "Event",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (!viewModel.isSignedIn()) {
                            showSignInDialog = true
                        } else {
                            showNotificationSheet = true
                        }
                    }) {
                        val hasSubscription = subscription?.notifications?.isNotEmpty() == true
                        Icon(
                            imageVector = if (hasSubscription) Icons.Filled.Notifications else Icons.Outlined.NotificationsNone,
                            contentDescription = "Notification preferences",
                        )
                    }
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        )
                    }
                    var menuExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            uiState.event?.let { event ->
                                DropdownMenuItem(
                                    text = { Text("Share") },
                                    leadingIcon = { Icon(Icons.Filled.Share, contentDescription = null) },
                                    onClick = {
                                        menuExpanded = false
                                        context.shareTbaUrl(
                                            title = "${event.year} ${event.name}",
                                            url = "https://www.thebluealliance.com/event/${event.key}",
                                        )
                                    },
                                )
                            }
                            if (viewModel.canPinShortcuts) {
                                DropdownMenuItem(
                                    text = { Text("Add to home screen") },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_add_to_home_screen),
                                            contentDescription = null,
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        viewModel.requestPinShortcut()
                                    },
                                )
                            }
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PrimaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 0.dp,
            ) {
                TABS.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(title) },
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refreshAll,
                modifier = Modifier.fillMaxSize(),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    when (page) {
                        0 -> EventInfoTab(uiState.event)
                        1 -> EventTeamsTab(uiState.teams) { teamKey ->
                            val eventKey = uiState.event?.key
                            if (eventKey != null) onNavigateToTeamEvent(teamKey, eventKey)
                            else onNavigateToTeam(teamKey)
                        }
                        2 -> EventMatchesTab(
                            matches = uiState.matches,
                            playoffType = uiState.event?.playoffType ?: PlayoffType.OTHER,
                            onNavigateToMatch = onNavigateToMatch
                        )
                        3 -> EventRankingsTab(uiState.rankings) { teamKey ->
                            val eventKey = uiState.event?.key
                            if (eventKey != null) onNavigateToTeamEvent(teamKey, eventKey)
                        }
                        4 -> EventAlliancesTab(uiState.alliances)
                        5 -> EventAwardsTab(uiState.awards)
                        6 -> EventDistrictPointsTab(
                            uiState.districtPoints,
                            uiState.event,
                            uiState.teams
                        )
                    }
                }
            }
        }
    }
}
