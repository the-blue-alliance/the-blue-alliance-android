package com.thebluealliance.android.ui.regional

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarYearPicker
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionalAdvancementScreen(
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateUp: (() -> Unit)? = null,
    onNavigateToSearch: () -> Unit,
    reselectFlow: Flow<Unit>,
    viewModel: RegionalAdvancementViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val maxYear by viewModel.maxYear.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    LaunchedEffect(reselectFlow) {
        reselectFlow.collect { listState.animateScrollToItem(0) }
    }
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        topBar = {
            TBATopAppBar(
                title = {
                    TopBarYearPicker(
                        selectedYear = selectedYear,
                        years = if (selectedYear > 0) (maxYear downTo 2025).toList() else emptyList(),
                        onYearSelected = viewModel::selectYear,
                        title = { Text("Regional Advancement") },
                    )
                },
                navigationIcon = {
                    if (onNavigateUp != null) {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    }
                },
                actions = { IconButton(onClick = onNavigateToSearch) { Icon(Icons.Default.Search, contentDescription = "Search") } },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing && uiState !is RegionalAdvancementUiState.Loading,
            onRefresh = viewModel::refreshRankings,
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
        ) {
            when (val state = uiState) {
                RegionalAdvancementUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is RegionalAdvancementUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                is RegionalAdvancementUiState.Success -> {
                    if (state.rankings.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No regional advancement rankings for $selectedYear", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                            item(key = "header") { RankingsTableHeader() }
                            items(state.rankings, key = { "${it.year}_${it.teamKey}" }) { ranking ->
                                RegionalRankingRow(ranking = ranking, onNavigateToTeam = onNavigateToTeam, onNavigateToEvent = onNavigateToEvent)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingsTableHeader() {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer).padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Rank", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.12f))
            Text(text = "Team", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.18f))
            Text(text = "Event 1", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.15f))
            Text(text = "Event 2", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.15f))
            Text(text = "Bonus", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.15f))
            Text(text = "Total Points", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.18f))
            Text(text = "Qualified", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.25f))
        }
    }
}

@Composable
private fun RegionalRankingRow(ranking: RegionalRanking, onNavigateToTeam: (String) -> Unit, onNavigateToEvent: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onNavigateToTeam(ranking.teamKey) }.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${ranking.rank}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.12f))
            Text(text = ranking.teamKey.removePrefix("frc"), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.18f))
            val sortedEvents = ranking.eventPoints.sortedBy { it.eventKey }
            if (sortedEvents.isNotEmpty()) {
                Text(text = "${sortedEvents[0].total.toInt()}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.15f).clickable { onNavigateToEvent(sortedEvents[0].eventKey) })
            } else {
                Text(text = "--", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.15f))
            }
            if (sortedEvents.size > 1) {
                Text(text = "${sortedEvents[1].total.toInt()}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.15f).clickable { onNavigateToEvent(sortedEvents[1].eventKey) })
            } else {
                Text(text = "--", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.15f))
            }
            val totalBonus = (ranking.rookieBonus + ranking.singleEventBonus).toInt()
            if (totalBonus > 0) {
                Text(text = "+$totalBonus", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.15f))
            } else {
                Text(text = "--", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.15f))
            }
            Text(text = "${ranking.pointTotal.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.18f))
            AdvancementBadge(method = ranking.advancementMethod, modifier = Modifier.weight(0.25f))
        }
    }
}

@Composable
private fun AdvancementBadge(method: String?, modifier: Modifier = Modifier) {
    if (method.isNullOrBlank()) {
        Text(text = "--", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = modifier)
    } else {
        Box(modifier = modifier.background(color = when { method.contains("Regional Winner") -> MaterialTheme.colorScheme.primaryContainer; method.contains("Finalist") -> MaterialTheme.colorScheme.secondaryContainer; else -> MaterialTheme.colorScheme.tertiaryContainer }, shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(text = method, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, color = when { method.contains("Regional Winner") -> MaterialTheme.colorScheme.onPrimaryContainer; method.contains("Finalist") -> MaterialTheme.colorScheme.onSecondaryContainer; else -> MaterialTheme.colorScheme.onTertiaryContainer })
        }
    }
}

