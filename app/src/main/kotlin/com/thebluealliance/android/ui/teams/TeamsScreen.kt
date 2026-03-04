package com.thebluealliance.android.ui.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.ui.components.FastScrollbar
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.SectionHeaderInfo
import com.thebluealliance.android.ui.components.TeamRow
import com.thebluealliance.android.ui.components.TBATopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    onNavigateToTeam: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit,
    reselectFlow: Flow<Unit>,
    viewModel: TeamsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(reselectFlow) {
        reselectFlow.collect {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        topBar = {
            TBATopAppBar(
                title = { Text("Teams") },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                showLamp = true
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

                        val hasFavorites = favoriteTeams.isNotEmpty()

                        val headerInfos = remember(hasFavorites, favoriteTeams.size, state.teams.size) {
                            if (!hasFavorites) {
                                emptyList()
                            } else {
                                buildList {
                                    var index = 0
                                    add(SectionHeaderInfo("favorites_header", "Favorites", index))
                                    index += 1 + favoriteTeams.size
                                    add(SectionHeaderInfo("all_teams_header", "All teams", index))
                                }
                            }
                        }

                        val headerKeys = remember(headerInfos) {
                            headerInfos.map { it.key }.toSet()
                        }

                        val stuckHeaderKey by remember(headerKeys) {
                            derivedStateOf {
                                val stuck = listState.layoutInfo.visibleItemsInfo
                                    .firstOrNull { item ->
                                        val key = item.key as? String
                                        key != null && key in headerKeys && item.offset <= 0
                                    }?.key as? String
                                stuck ?: headerInfos.firstOrNull()?.key
                            }
                        }

                        FastScrollbar(listState = listState) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                if (hasFavorites) {
                                    stickyHeader(key = "favorites_header") {
                                        SectionHeader(
                                            label = "Favorites",
                                            isStuck = stuckHeaderKey == "favorites_header",
                                            allHeaders = headerInfos,
                                            onHeaderSelected = { info ->
                                                coroutineScope.launch {
                                                    listState.animateScrollToItem(info.itemIndex)
                                                }
                                            },
                                        )
                                    }
                                    items(favoriteTeams, key = { "fav_${it.key}" }) { team ->
                                        TeamRow(team = team, onClick = { onNavigateToTeam(team.key) })
                                    }
                                    stickyHeader(key = "all_teams_header") {
                                        SectionHeader(
                                            label = "All teams",
                                            isStuck = stuckHeaderKey == "all_teams_header",
                                            allHeaders = headerInfos,
                                            onHeaderSelected = { info ->
                                                coroutineScope.launch {
                                                    listState.animateScrollToItem(info.itemIndex)
                                                }
                                            },
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
