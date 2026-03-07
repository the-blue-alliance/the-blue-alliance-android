package com.thebluealliance.android.ui.teams

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.shortcuts.ReportShortcutVisitEffect
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.common.shareTbaUrl
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.MediaTab
import com.thebluealliance.android.ui.components.NotificationPreferencesSheet
import com.thebluealliance.android.ui.components.TBATabRow
import com.thebluealliance.android.ui.components.TBATopAppBar
import kotlinx.coroutines.launch

private val TABS = listOf("Info", "Events", "Media")

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
    val pagerState = rememberPagerState(initialPage = initialTab, pageCount = { TABS.size })
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
    ReportShortcutVisitEffect(uiState.team?.key)

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
            displayName = uiState.team?.let { "Team ${it.number}" + (it.nickname?.let { n -> " - $n" } ?: "") } ?: "Team",
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
                    title = {
                        val team = uiState.team
                        Text(
                            text = if (team != null) "${team.number} - ${team.nickname ?: ""}" else "Team",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
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
                            if (hasSubscription) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_notification),
                                    contentDescription = "Notification preferences",
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.NotificationsNone,
                                    contentDescription = "Notification preferences",
                                )
                            }
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
                                uiState.team?.let { team ->
                                    DropdownMenuItem(
                                        text = { Text("Share") },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Filled.Share,
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            menuExpanded = false
                                            context.shareTbaUrl(
                                                title = "Team ${team.number} - ${team.nickname ?: ""}",
                                                url = "https://www.thebluealliance.com/team/${team.number}",
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

                TBATabRow(selectedTabIndex = pagerState.currentPage) {
                    TABS.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = {
                                Text(
                                    text = title,
                                    color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.7f)
                                )
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing && uiState.team != null,
            onRefresh = viewModel::refreshAll,
            modifier = Modifier.fillMaxSize(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> InfoTab(
                        team = uiState.team,
                        media = uiState.media,
                        innerPadding = innerPadding,
                    )
                    1 -> EventsTab(
                        events = uiState.events,
                        selectedYear = selectedYear,
                        yearsParticipated = yearsParticipated,
                        onYearSelected = viewModel::selectYear,
                        onNavigateToEvent = { eventKey ->
                            val teamKey = uiState.team?.key
                            if (teamKey != null) onNavigateToTeamEvent(teamKey, eventKey)
                            else onNavigateToEvent(eventKey)
                        },
                        innerPadding = innerPadding,
                    )
                    2 -> MediaTab(
                        media = uiState.media,
                        innerPadding = innerPadding,
                    )
                }
            }
        }
    }
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
    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentPadding = innerPadding,
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (avatar != null) {
                    val bitmap = remember(avatar.base64Image) {
                        try {
                            val bytes = Base64.decode(avatar.base64Image, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        } catch (_: Exception) {
                            null
                        }
                    }
                    if (bitmap != null) {
                        var showRed by remember { mutableStateOf(false) }
                        val bgColor = if (showRed) FrcRed else FrcBlue
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .clickable { showRed = !showRed },
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
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
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

@Composable
private fun EventsTab(
    events: List<Event>?,
    selectedYear: Int,
    yearsParticipated: List<Int>,
    onYearSelected: (Int) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding())
    ) {
        // To allow content to show up above the LazyColumn we need to put the top innerPadding on
        // the parent column. Take all remaining padding and apply them to the rest of the content
        val layoutDirection = LocalLayoutDirection.current
        val remainingContentPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection),
            end = innerPadding.calculateEndPadding(layoutDirection),
            bottom = innerPadding.calculateBottomPadding(),
        )

        if (yearsParticipated.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                TextButton(onClick = { expanded = true }) {
                    Text(
                        text = selectedYear.toString(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select year")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    yearsParticipated.forEach { year ->
                        DropdownMenuItem(
                            text = { Text(year.toString()) },
                            onClick = {
                                onYearSelected(year)
                                expanded = false
                            },
                        )
                    }
                }
            }
        }

        if (events == null) {
            LoadingBox(
                modifier = Modifier.padding(remainingContentPadding)
            )
        } else if (events.isEmpty()) {
            EmptyBox(
                modifier = Modifier.padding(remainingContentPadding),
                message = "No events for $selectedYear"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = remainingContentPadding,
            ) {
                items(events, key = { it.key }) { event ->
                    EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
                }
            }
        }
    }
}

private val FrcBlue = Color(0xFF0066B3)
private val FrcRed = Color(0xFFED1C24)

