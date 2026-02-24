package com.thebluealliance.android.ui.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.components.FastScrollbar
import com.thebluealliance.android.ui.components.TeamRow
import com.thebluealliance.android.ui.components.TBABottomBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    onNavigateToTeam: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit,
    onNavigateToTopLevel: (NavKey) -> Unit,
    currentRoute: NavKey,
    viewModel: TeamsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var scrollToTopTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teams") },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
            )
        },
        bottomBar = {
            TBABottomBar(
                currentRoute = currentRoute,
                onNavigate = onNavigateToTopLevel,
                onReselect = { scrollToTopTrigger++ },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing && uiState !is TeamsUiState.Loading,
            onRefresh = viewModel::refreshTeams,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when (val state = uiState) {
                is TeamsUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is TeamsUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, style = MaterialTheme.typography.bodyLarge)
                            Button(onClick = viewModel::refreshTeams) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is TeamsUiState.Success -> {
                    if (state.teams.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (!isRefreshing) {
                                Text("No teams found")
                            }
                        }
                    } else {
                        val favoriteTeams = if (state.favoriteTeamKeys.isNotEmpty()) {
                            state.teams.filter { it.key in state.favoriteTeamKeys }
                        } else {
                            emptyList()
                        }

                        FastScrollbar(listState = listState) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                if (favoriteTeams.isNotEmpty()) {
                                    item(key = "favorites_header") {
                                        Text(
                                            text = "Favorites",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp,
                                            ),
                                        )
                                    }
                                    items(favoriteTeams, key = { "fav_${it.key}" }) { team ->
                                        TeamRow(team = team, onClick = { onNavigateToTeam(team.key) })
                                    }
                                    item(key = "favorites_divider") {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    }
                                    item(key = "all_teams_header") {
                                        Text(
                                            text = "All teams",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp,
                                            ),
                                        )
                                    }
                                }
                                items(state.teams, key = { it.key }) { team ->
                                    TeamRow(team = team, onClick = { onNavigateToTeam(team.key) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
