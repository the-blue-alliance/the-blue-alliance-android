package com.thebluealliance.android.ui.mytba

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import kotlinx.coroutines.launch

private val TABS = listOf("Favorites", "Notifications")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTBAScreen(
    onSignIn: () -> Unit = {},
    onNavigateToTeam: (String) -> Unit = {},
    onNavigateToEvent: (String) -> Unit = {},
    scrollToTopTrigger: Int = 0,
    viewModel: MyTBAViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    if (!uiState.isSignedIn) {
        SignInPrompt(onSignIn = onSignIn)
        return
    }

    val pagerState = rememberPagerState(pageCount = { TABS.size })
    val coroutineScope = rememberCoroutineScope()
    val favoritesListState = rememberLazyListState()
    val notificationsListState = rememberLazyListState()

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            when (pagerState.currentPage) {
                0 -> favoritesListState.animateScrollToItem(0)
                1 -> notificationsListState.animateScrollToItem(0)
            }
        }
    }

    var showSignOutDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign out?") },
            text = { Text("Your favorites and notifications won't apply to the app anymore.") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    viewModel.signOut()
                }) { Text("Sign out") }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) { Text("Cancel") }
            },
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val personIcon = rememberVectorPainter(Icons.Default.Person)
            AsyncImage(
                model = uiState.userPhotoUrl,
                contentDescription = "Profile photo",
                modifier = Modifier.size(40.dp).clip(CircleShape),
                placeholder = personIcon,
                error = personIcon,
                fallback = personIcon,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = uiState.userName ?: "Signed in",
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (uiState.userEmail != null) {
                    Text(
                        text = uiState.userEmail!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Sign out") },
                        onClick = {
                            menuExpanded = false
                            showSignOutDialog = true
                        },
                    )
                }
            }
        }

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
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> FavoritesTab(
                        favorites = uiState.favorites,
                        onNavigateToTeam = onNavigateToTeam,
                        onNavigateToEvent = onNavigateToEvent,
                        listState = favoritesListState,
                        canPinShortcuts = uiState.canPinShortcuts,
                        onAddShortcut = viewModel::requestPinShortcut,
                    )
                    1 -> NotificationsTab(uiState.subscriptions, onNavigateToTeam, onNavigateToEvent, notificationsListState)
                }
            }
        }
    }
}

@Composable
private fun SignInPrompt(onSignIn: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "myTBA",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Sign in to save your favorite teams and events",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = onSignIn) {
                Text("Sign in with Google")
            }
        }
    }
}

@Composable
private fun FavoritesTab(
    favorites: List<Favorite>,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    listState: LazyListState,
    canPinShortcuts: Boolean,
    onAddShortcut: (Favorite) -> Unit,
) {
    if (favorites.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No favorites yet", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(favorites, key = { "${it.modelType}_${it.modelKey}" }) { favorite ->
            FavoriteItem(
                favorite = favorite,
                onClick = {
                    when (favorite.modelType) {
                        ModelType.TEAM -> onNavigateToTeam(favorite.modelKey)
                        ModelType.EVENT -> onNavigateToEvent(favorite.modelKey)
                    }
                },
                showMenu = canPinShortcuts,
                onAddShortcut = { onAddShortcut(favorite) },
            )
        }
    }
}

@Composable
private fun FavoriteItem(
    favorite: Favorite,
    onClick: () -> Unit,
    showMenu: Boolean,
    onAddShortcut: () -> Unit,
) {
    val typeLabel = when (favorite.modelType) {
        ModelType.EVENT -> "Event"
        ModelType.TEAM -> "Team"
        ModelType.MATCH -> "Match"
        else -> "Other"
    }
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = 16.dp,
                top = if (showMenu) 8.dp else 12.dp,
                bottom = if (showMenu) 8.dp else 12.dp,
                end = if (showMenu) 4.dp else 16.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = favorite.modelKey,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = typeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (showMenu) {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Add shortcut to home screen") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_add_to_home_screen),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onAddShortcut()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationsTab(
    subscriptions: List<Subscription>,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    listState: LazyListState,
) {
    if (subscriptions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No notifications yet", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(subscriptions, key = { "${it.modelType}_${it.modelKey}" }) { subscription ->
            val typeLabel = when (subscription.modelType) {
                ModelType.EVENT -> "Event"
                ModelType.TEAM -> "Team"
                ModelType.MATCH -> "Match"
                else -> "Other"
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (subscription.modelType) {
                            ModelType.TEAM -> onNavigateToTeam(subscription.modelKey)
                            ModelType.EVENT -> onNavigateToEvent(subscription.modelKey)
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = subscription.modelKey,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "$typeLabel · ${subscription.notifications.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
