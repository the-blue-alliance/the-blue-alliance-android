package com.thebluealliance.android.ui.mytba

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.NotificationType
import com.thebluealliance.android.domain.model.Subscription
import com.thebluealliance.android.ui.common.ErrorBox
import com.thebluealliance.android.ui.common.TouchPullToRefreshBox
import com.thebluealliance.android.ui.components.ProseMaxWidth
import com.thebluealliance.android.ui.components.ReadingTabRow
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.TBATab
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarTitle
import com.thebluealliance.android.ui.components.TopLevelContentWindowInsets
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.messageBackground
import com.thebluealliance.android.ui.components.onSecondaryClick
import com.thebluealliance.android.ui.components.onlyBottom
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.pagerPageFocus
import com.thebluealliance.android.ui.components.readingWidth
import com.thebluealliance.android.ui.components.withoutBottom
import com.thebluealliance.android.ui.theme.TbaIcons
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

enum class MyTBATab(
    val readableName: String,
) {
    FAVORITES("Favorites"),
    NOTIFICATIONS("Notifications"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTBAScreen(
    onSignIn: () -> Unit = {},
    onNavigateToTeam: (String) -> Unit = {},
    onNavigateToEvent: (String) -> Unit = {},
    onNavigateToMatch: (String) -> Unit = {},
    onNavigateUp: (() -> Unit)? = null,
    onNavigateToSearch: () -> Unit,
    reselectFlow: Flow<Unit>,
    viewModel: MyTBAViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(pageCount = { MyTBATab.entries.size })
    val coroutineScope = rememberCoroutineScope()
    val favoritesListState = rememberLazyListState()
    val notificationsListState = rememberLazyListState()
    LaunchedEffect(reselectFlow) {
        reselectFlow.collect {
            when (MyTBATab.entries[pagerState.currentPage]) {
                MyTBATab.FAVORITES -> favoritesListState.animateScrollToItem(0)
                MyTBATab.NOTIFICATIONS -> notificationsListState.animateScrollToItem(0)
            }
        }
    }

    var showSignOutDialog by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign out?") },
            text = { Text("Your favorites and notifications won't apply to the app anymore.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        viewModel.signOut()
                    },
                    modifier = Modifier.handCursor(),
                ) { Text("Sign out") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                    },
                    modifier = Modifier.handCursor(),
                ) { Text("Cancel") }
            },
        )
    }

    Scaffold(
        contentWindowInsets = TopLevelContentWindowInsets,
        topBar = {
            TBATopAppBar(
                onRefresh =
                    if (uiState.isSignedIn) {
                        { viewModel.refreshTab(MyTBATab.entries[pagerState.currentPage]) }
                    } else {
                        null
                    },
                isRefreshing = isRefreshing,
                title = { TopBarTitle("myTBA") },
                navigationIcon =
                    onNavigateUp?.let { navigateUp ->
                        {
                            IconButton(onClick = navigateUp, modifier = Modifier.handCursor()) {
                                Icon(
                                    imageVector = TbaIcons.ArrowBack,
                                    contentDescription = "Back",
                                )
                            }
                        }
                    },
                secondaryActions =
                    listOf(
                        TopBarAction("Search", TbaIcons.Search, onNavigateToSearch),
                    ),
            )
        },
    ) { innerPadding ->
        if (!uiState.isSignedIn) {
            Box(
                modifier =
                    Modifier
                        .messageBackground()
                        .padding(innerPadding.withoutBottom())
                        .fillMaxSize(),
            ) {
                SignInPrompt(onSignIn = onSignIn)
            }
            return@Scaffold
        }

        Column(
            modifier =
                Modifier
                    .pageBackground()
                    .padding(innerPadding.withoutBottom())
                    .fillMaxSize(),
        ) {
            Row(
                modifier =
                    Modifier
                        .readingWidth()
                        .fillMaxWidth()
                        // End inset matches the list rows so the overflow buttons line up.
                        .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val personIcon = rememberVectorPainter(TbaIcons.Person)
                AsyncImage(
                    model = uiState.userPhotoUrl,
                    contentDescription = "Profile photo",
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
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
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.handCursor(),
                    ) {
                        Icon(
                            imageVector = TbaIcons.MoreVert,
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
                            modifier = Modifier.handCursor(),
                        )
                    }
                }
            }

            ReadingTabRow(selectedTabIndex = pagerState.currentPage) {
                MyTBATab.entries.forEach { tab ->
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

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                TouchPullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refreshTab(MyTBATab.entries[page]) },
                    modifier = Modifier.fillMaxSize().pagerPageFocus(pagerState, page),
                ) {
                    when (MyTBATab.entries[page]) {
                        MyTBATab.FAVORITES ->
                            FavoritesTab(
                                favorites = uiState.favorites,
                                error = uiState.favoritesError,
                                onRetry = { viewModel.refreshTab(MyTBATab.FAVORITES) },
                                displayNames = uiState.displayNames,
                                onNavigateToTeam = onNavigateToTeam,
                                onNavigateToEvent = onNavigateToEvent,
                                onNavigateToMatch = onNavigateToMatch,
                                listState = favoritesListState,
                                contentPadding = innerPadding.onlyBottom(),
                                canPinShortcuts = uiState.canPinShortcuts,
                                onAddShortcut = viewModel::requestPinShortcut,
                                onRemoveFavorite = viewModel::removeFavorite,
                            )

                        MyTBATab.NOTIFICATIONS ->
                            NotificationsTab(
                                subscriptions = uiState.subscriptions,
                                error = uiState.subscriptionsError,
                                onRetry = { viewModel.refreshTab(MyTBATab.NOTIFICATIONS) },
                                displayNames = uiState.displayNames,
                                onNavigateToTeam = onNavigateToTeam,
                                onNavigateToEvent = onNavigateToEvent,
                                onNavigateToMatch = onNavigateToMatch,
                                listState = notificationsListState,
                                contentPadding = innerPadding.onlyBottom(),
                                onRemoveSubscription = { sub ->
                                    viewModel.removeSubscription(sub.modelKey, sub.modelType)
                                },
                            )
                    }
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
            modifier = Modifier.widthIn(max = ProseMaxWidth),
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
            Button(onClick = onSignIn, modifier = Modifier.handCursor()) {
                Text("Sign in with Google")
            }
        }
    }
}

@Composable
private fun FavoritesTab(
    favorites: List<Favorite>,
    error: String?,
    onRetry: () -> Unit,
    displayNames: Map<String, String>,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateToMatch: (String) -> Unit,
    listState: LazyListState,
    contentPadding: PaddingValues,
    canPinShortcuts: Boolean,
    onAddShortcut: (Favorite) -> Unit,
    onRemoveFavorite: (Favorite) -> Unit,
) {
    if (error != null) {
        ErrorBox(message = error, onRetry = onRetry)
        return
    }
    if (favorites.isEmpty()) {
        EmptyTabMessage(
            title = "No favorites yet",
            hint = "Tap the star on any team or event to add it here.",
        )
        return
    }
    ReadingWidthBox(contentPadding, Modifier.fillMaxSize()) { readingPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = readingPadding,
        ) {
            items(favorites, key = { "${it.modelType}_${it.modelKey}" }) { favorite ->
                FavoriteItem(
                    favorite = favorite,
                    displayName = displayNames[favorite.modelKey] ?: favorite.modelKey,
                    onClick = {
                        when (favorite.modelType) {
                            ModelType.TEAM -> onNavigateToTeam(favorite.modelKey)
                            ModelType.EVENT -> onNavigateToEvent(favorite.modelKey)
                            ModelType.MATCH -> onNavigateToMatch(favorite.modelKey)
                            ModelType.EVENT_TEAM ->
                                onNavigateToEvent(favorite.modelKey.substringBefore('_'))
                        }
                    },
                    canPinShortcuts = canPinShortcuts,
                    onAddShortcut = { onAddShortcut(favorite) },
                    onRemove = { onRemoveFavorite(favorite) },
                )
            }
        }
    }
}

@Composable
private fun EmptyTabMessage(
    title: String,
    hint: String,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FavoriteItem(
    favorite: Favorite,
    displayName: String,
    onClick: () -> Unit,
    canPinShortcuts: Boolean,
    onAddShortcut: () -> Unit,
    onRemove: () -> Unit,
) {
    MyTbaEntityRow(
        title = displayName,
        subtitle = modelTypeLabel(favorite.modelType),
        onClickLabel = modelTypeClickLabel(favorite.modelType),
        onClick = onClick,
        onRemove = onRemove,
        onAddShortcut = if (canPinShortcuts) onAddShortcut else null,
    )
}

@Composable
private fun NotificationsTab(
    subscriptions: List<Subscription>,
    error: String?,
    onRetry: () -> Unit,
    displayNames: Map<String, String>,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateToMatch: (String) -> Unit,
    listState: LazyListState,
    contentPadding: PaddingValues,
    onRemoveSubscription: (Subscription) -> Unit,
) {
    if (error != null) {
        ErrorBox(message = error, onRetry = onRetry)
        return
    }
    if (subscriptions.isEmpty()) {
        EmptyTabMessage(
            title = "No notifications yet",
            hint = "Tap the bell on any team or event to add it here.",
        )
        return
    }
    ReadingWidthBox(contentPadding, Modifier.fillMaxSize()) { readingPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = readingPadding,
        ) {
            items(subscriptions, key = { "${it.modelType}_${it.modelKey}" }) { subscription ->
                SubscriptionItem(
                    subscription = subscription,
                    displayName = displayNames[subscription.modelKey] ?: subscription.modelKey,
                    onClick = {
                        when (subscription.modelType) {
                            ModelType.TEAM -> onNavigateToTeam(subscription.modelKey)
                            ModelType.EVENT -> onNavigateToEvent(subscription.modelKey)
                            ModelType.MATCH -> onNavigateToMatch(subscription.modelKey)
                            ModelType.EVENT_TEAM ->
                                onNavigateToEvent(subscription.modelKey.substringBefore('_'))
                        }
                    },
                    onRemove = { onRemoveSubscription(subscription) },
                )
            }
        }
    }
}

@Composable
private fun SubscriptionItem(
    subscription: Subscription,
    displayName: String,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val notificationNames =
        subscription.notifications.joinToString(", ") { serverKey ->
            NotificationType
                .fromServerKey(serverKey)
                ?.displayName
                ?.takeIf { it.isNotBlank() }
                ?: serverKey
        }
    MyTbaEntityRow(
        title = displayName,
        subtitle = "${modelTypeLabel(subscription.modelType)} · $notificationNames",
        onClickLabel = modelTypeClickLabel(subscription.modelType),
        onClick = onClick,
        onRemove = onRemove,
    )
}

internal fun modelTypeLabel(modelType: Int): String =
    when (modelType) {
        ModelType.EVENT -> "Event"
        ModelType.TEAM -> "Team"
        ModelType.MATCH -> "Match"
        ModelType.EVENT_TEAM -> "Team @ Event"
        ModelType.DISTRICT -> "District"
        else -> "Other"
    }

/** Names where tapping a myTBA row goes; Team @ Event rows open the event. */
internal fun modelTypeClickLabel(modelType: Int): String? =
    when (modelType) {
        ModelType.EVENT, ModelType.EVENT_TEAM -> "View event"
        ModelType.TEAM -> "View team"
        ModelType.MATCH -> "View match"
        else -> null
    }

/**
 * A myTBA entity link with its overflow menu; the shortcut item shows only when [onAddShortcut] is
 * set. A right-click anywhere on the row opens the same menu at the pointer.
 */
@Composable
private fun MyTbaEntityRow(
    title: String,
    subtitle: String,
    onClickLabel: String?,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onAddShortcut: (() -> Unit)? = null,
) {
    // One state for both menus, so opening either closes the other.
    var openMenu by remember { mutableStateOf(MyTbaRowMenu.None) }
    var contextMenuAt by remember { mutableStateOf(IntOffset.Zero) }

    Box {
        // The entity link stops short of the menu button so the two targets don't nest.
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .onSecondaryClick { position ->
                        contextMenuAt = position.round()
                        openMenu = MyTbaRowMenu.Pointer
                    }.padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .handCursor()
                        .clickable(onClickLabel = onClickLabel, onClick = onClick)
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box(modifier = Modifier.padding(vertical = 8.dp)) {
                IconButton(
                    onClick = { openMenu = MyTbaRowMenu.Button },
                    modifier = Modifier.handCursor(),
                ) {
                    Icon(
                        imageVector = TbaIcons.MoreVert,
                        contentDescription = "More options for $title",
                    )
                }
                DropdownMenu(
                    expanded = openMenu == MyTbaRowMenu.Button,
                    onDismissRequest = { openMenu = MyTbaRowMenu.None },
                ) {
                    MyTbaEntityMenuItems(onRemove, onAddShortcut) { openMenu = MyTbaRowMenu.None }
                }
            }
        }
        // A zero-size anchor at the pointer, so the menu opens where the row was right-clicked.
        Box(modifier = Modifier.offset { contextMenuAt }) {
            DropdownMenu(
                expanded = openMenu == MyTbaRowMenu.Pointer,
                onDismissRequest = { openMenu = MyTbaRowMenu.None },
            ) {
                MyTbaEntityMenuItems(onRemove, onAddShortcut) { openMenu = MyTbaRowMenu.None }
            }
        }
    }
}

/** Which of a myTBA row's two menus is open: the More options button's, or the right-click one. */
private enum class MyTbaRowMenu { None, Button, Pointer }

@Composable
private fun MyTbaEntityMenuItems(
    onRemove: () -> Unit,
    onAddShortcut: (() -> Unit)?,
    close: () -> Unit,
) {
    if (onAddShortcut != null) {
        DropdownMenuItem(
            text = { Text("Add shortcut to home screen") },
            leadingIcon = {
                Icon(
                    imageVector = TbaIcons.AddToHomeScreen,
                    contentDescription = null,
                )
            },
            onClick = {
                close()
                onAddShortcut()
            },
            modifier = Modifier.handCursor(),
        )
    }
    DropdownMenuItem(
        text = { Text("Remove") },
        leadingIcon = {
            Icon(
                imageVector = TbaIcons.Delete,
                contentDescription = null,
            )
        },
        onClick = {
            close()
            onRemove()
        },
        modifier = Modifier.handCursor(),
    )
}
