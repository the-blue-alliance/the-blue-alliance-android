package com.thebluealliance.android.ui.regionaladvancement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.RegionalRanking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionalAdvancementScreen(
    onNavigateToTeam: (String) -> Unit = {},
    scrollToTopTrigger: Int = 0,
    onYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
    viewModel: RegionalAdvancementViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val maxYear by viewModel.maxYear.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(selectedYear, maxYear) {
        onYearState(selectedYear, maxYear, viewModel::selectYear)
    }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            listState.animateScrollToItem(0)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refreshRankings,
            modifier = Modifier.fillMaxSize(),
        ) {
            when (val state = uiState) {
                is RegionalAdvancementUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is RegionalAdvancementUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, style = MaterialTheme.typography.bodyLarge)
                            Button(onClick = viewModel::refreshRankings) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is RegionalAdvancementUiState.Success -> {
                    if (state.rankings.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No rankings found for $selectedYear")
                        }
                    } else {
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                            items(state.rankings, key = { "${it.year}_${it.teamKey}" }) { ranking ->
                                RankingItem(
                                    ranking = ranking,
                                    onClick = { onNavigateToTeam(ranking.teamKey) },
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
private fun RankingItem(
    ranking: RegionalRanking,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "#${ranking.rank}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.15f),
        )
        Text(
            text = ranking.teamKey.removePrefix("frc"),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.35f),
        )
        Text(
            text = "${ranking.pointTotal} pts",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.25f),
        )
        if (ranking.rookieBonus > 0 || ranking.singleEventBonus > 0) {
            Column(modifier = Modifier.weight(0.25f)) {
                if (ranking.rookieBonus > 0) {
                    Text(
                        text = "+${ranking.rookieBonus} rookie",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (ranking.singleEventBonus > 0) {
                    Text(
                        text = "+${ranking.singleEventBonus} single",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
