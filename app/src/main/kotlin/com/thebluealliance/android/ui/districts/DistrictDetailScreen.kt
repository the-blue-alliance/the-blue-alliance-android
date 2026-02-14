package com.thebluealliance.android.ui.districts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.ui.components.EventRow
import kotlinx.coroutines.launch

private val TABS = listOf("Events", "Advancement")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateToTeam: (String) -> Unit,
    viewModel: DistrictDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { TABS.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            windowInsets = WindowInsets(0),
            title = {
                Text(
                    text = uiState.district?.displayName ?: "District",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )

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
            isRefreshing = isRefreshing && uiState.events != null && uiState.rankings != null,
            onRefresh = viewModel::refreshAll,
            modifier = Modifier.fillMaxSize(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> EventsTab(uiState.events, onNavigateToEvent)
                    1 -> AdvancementTab(
                        uiState.rankings,
                        uiState.regionalRankings,
                        uiState.district?.year ?: 0,
                        onNavigateToTeam
                    )
                }
            }
        }
    }
}

@Composable
private fun EventsTab(events: List<Event>?, onNavigateToEvent: (String) -> Unit) {
    if (events == null) {
        LoadingBox()
        return
    }
    if (events.isEmpty()) {
        EmptyBox("No events")
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(events, key = { it.key }) { event ->
            EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
        }
    }
}

@Composable
private fun AdvancementTab(
    rankings: List<DistrictRanking>?,
    regionalRankings: List<RegionalRanking>?,
    year: Int,
    onNavigateToTeam: (String) -> Unit,
) {
    if (rankings == null) {
        LoadingBox()
        return
    }
    if (rankings.isEmpty() && (year < 2025 || regionalRankings?.isEmpty() != false)) {
        EmptyBox("No rankings")
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Show Regional Advancement section for years 2025+
        if (year >= 2025 && !regionalRankings.isNullOrEmpty()) {
            item {
                Text(
                    text = "Regional Advancement",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
            items(regionalRankings, key = { "${it.year}_${it.teamKey}" }) { ranking ->
                RegionalRankingRow(ranking = ranking, onClick = { onNavigateToTeam(ranking.teamKey) })
            }
            item {
                Text(
                    text = "District Rankings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
                )
            }
        }

        // Show District Rankings
        items(rankings, key = { "${it.districtKey}_${it.teamKey}" }) { ranking ->
            DistrictRankingRow(ranking = ranking, onClick = { onNavigateToTeam(ranking.teamKey) })
        }
    }
}

@Composable
private fun RegionalRankingRow(ranking: RegionalRanking, onClick: () -> Unit) {
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
            modifier = Modifier.weight(0.5f),
        )
        Text(
            text = "${ranking.pointTotal} pts",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.35f),
        )
    }
}

@Composable
private fun DistrictRankingRow(ranking: DistrictRanking, onClick: () -> Unit) {
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
            modifier = Modifier.weight(0.5f),
        )
        Text(
            text = "${ranking.pointTotal.toInt()} pts",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.35f),
        )
    }
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyBox(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
    }
}
