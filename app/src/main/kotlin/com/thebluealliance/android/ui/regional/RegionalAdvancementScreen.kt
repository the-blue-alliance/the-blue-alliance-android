package com.thebluealliance.android.ui.regional

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.ui.components.TBATopAppBar
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionalAdvancementScreen(
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    reselectFlow: Flow<Unit>,
    viewModel: RegionalAdvancementViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val maxYear by viewModel.maxYear.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var yearDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(reselectFlow) {
        reselectFlow.collect {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        topBar = {
            TBATopAppBar(
                title = {
                    Row(
                        modifier = Modifier.clickable { yearDropdownExpanded = true },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("$selectedYear Regional Advancement")
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select year")
                        DropdownMenu(
                            expanded = yearDropdownExpanded,
                            onDismissRequest = { yearDropdownExpanded = false },
                        ) {
                            (maxYear downTo 2025).forEach { year ->
                                DropdownMenuItem(
                                    text = { Text(year.toString()) },
                                    onClick = {
                                        viewModel.selectYear(year)
                                        yearDropdownExpanded = false
                                    },
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                showLamp = true,
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing && uiState !is RegionalAdvancementUiState.Loading,
            onRefresh = viewModel::refreshRankings,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when (val state = uiState) {
                RegionalAdvancementUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is RegionalAdvancementUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                is RegionalAdvancementUiState.Success -> {
                    if (state.rankings.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No regional advancement rankings for $selectedYear",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                        ) {
                            items(state.rankings, key = { "${it.year}_${it.teamKey}" }) { ranking ->
                                RegionalRankingRow(
                                    ranking = ranking,
                                    onNavigateToTeam = onNavigateToTeam,
                                    onNavigateToEvent = onNavigateToEvent,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegionalRankingRow(
    ranking: RegionalRanking,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToTeam(ranking.teamKey) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "#${ranking.rank}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.18f),
            )
            Text(
                text = ranking.teamKey.removePrefix("frc"),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(0.32f),
            )
            Text(
                text = "${ranking.pointTotal.toInt()} pts",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.25f),
            )
            if (ranking.rookieBonus > 0 || ranking.singleEventBonus > 0) {
                val bonusText = buildString {
                    if (ranking.rookieBonus > 0) append("+${ranking.rookieBonus.toInt()} rookie")
                    if (ranking.singleEventBonus > 0) {
                        if (isNotEmpty()) append(" ")
                        append("+${ranking.singleEventBonus.toInt()} single")
                    }
                }
                Text(
                    text = bonusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.25f),
                )
            }
        }

        if (ranking.eventPoints.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ranking.eventPoints.take(2).forEach { point ->
                    AssistChip(
                        onClick = { onNavigateToEvent(point.eventKey) },
                        label = { Text("${point.eventKey} (${point.total.toInt()})") },
                    )
                }
            }
        }
    }
}
