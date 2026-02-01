package com.thebluealliance.android.ui.mytba

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import kotlinx.coroutines.launch

private val TABS = listOf("Favorites", "Subscriptions")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTBAScreen(
    onSignIn: () -> Unit = {},
    onNavigateToTeam: (String) -> Unit = {},
    onNavigateToEvent: (String) -> Unit = {},
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

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
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
            OutlinedButton(onClick = viewModel::signOut) {
                Text("Sign out")
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
                    0 -> FavoritesTab(uiState.favorites, onNavigateToTeam, onNavigateToEvent)
                    1 -> SubscriptionsTab(uiState.subscriptions, onNavigateToTeam, onNavigateToEvent)
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
) {
    if (favorites.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No favorites yet", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(favorites, key = { "${it.modelType}_${it.modelKey}" }) { favorite ->
            FavoriteItem(
                favorite = favorite,
                onClick = {
                    when (favorite.modelType) {
                        ModelType.TEAM -> onNavigateToTeam(favorite.modelKey)
                        ModelType.EVENT -> onNavigateToEvent(favorite.modelKey)
                    }
                },
            )
        }
    }
}

@Composable
private fun FavoriteItem(favorite: Favorite, onClick: () -> Unit) {
    val typeLabel = when (favorite.modelType) {
        ModelType.EVENT -> "Event"
        ModelType.TEAM -> "Team"
        ModelType.MATCH -> "Match"
        else -> "Other"
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
}

@Composable
private fun SubscriptionsTab(
    subscriptions: List<Subscription>,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
) {
    if (subscriptions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No subscriptions yet", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
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
