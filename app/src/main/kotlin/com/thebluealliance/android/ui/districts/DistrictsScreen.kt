package com.thebluealliance.android.ui.districts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.ui.regionaladvancement.RegionalAdvancementUiState
import com.thebluealliance.android.ui.regionaladvancement.RegionalAdvancementViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictsScreen(
    onNavigateToDistrict: (String) -> Unit = {},
    onNavigateToTeam: (String) -> Unit = {},
    scrollToTopTrigger: Int = 0,
    onYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
    districtsViewModel: DistrictsViewModel = hiltViewModel(),
    regionalViewModel: RegionalAdvancementViewModel = hiltViewModel(),
) {
    val districtsUiState by districtsViewModel.uiState.collectAsStateWithLifecycle()
    val districtsSelectedYear by districtsViewModel.selectedYear.collectAsStateWithLifecycle()
    val districtsMaxYear by districtsViewModel.maxYear.collectAsStateWithLifecycle()
    val districtsIsRefreshing by districtsViewModel.isRefreshing.collectAsStateWithLifecycle()

    val regionalUiState by regionalViewModel.uiState.collectAsStateWithLifecycle()
    val regionalSelectedYear by regionalViewModel.selectedYear.collectAsStateWithLifecycle()
    val regionalMaxYear by regionalViewModel.maxYear.collectAsStateWithLifecycle()
    val regionalIsRefreshing by regionalViewModel.isRefreshing.collectAsStateWithLifecycle()

    // Regional Advancement is only available for years 2025+
    val showRegionalAdvancement = districtsSelectedYear >= 2025
    val tabs = if (showRegionalAdvancement) {
        listOf("Districts", "Regional Advancement")
    } else {
        listOf("Districts")
    }

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    val districtsListState = rememberLazyListState()
    val regionalListState = rememberLazyListState()

    // Synchronize year selection across both screens
    LaunchedEffect(districtsSelectedYear, districtsMaxYear) {
        onYearState(districtsSelectedYear, districtsMaxYear) { year ->
            districtsViewModel.selectYear(year)
            regionalViewModel.selectYear(year)
        }
    }

    LaunchedEffect(scrollToTopTrigger) {
        if (scrollToTopTrigger > 0) {
            when (pagerState.currentPage) {
                0 -> districtsListState.animateScrollToItem(0)
                1 -> if (showRegionalAdvancement) regionalListState.animateScrollToItem(0)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) },
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = when (pagerState.currentPage) {
                0 -> districtsIsRefreshing && districtsUiState !is DistrictsUiState.Loading
                1 -> if (showRegionalAdvancement) regionalIsRefreshing else false
                else -> false
            },
            onRefresh = {
                if (pagerState.currentPage == 0) {
                    districtsViewModel.refreshDistricts()
                } else if (showRegionalAdvancement) {
                    regionalViewModel.refreshRankings()
                }
            },
            modifier = Modifier.fillMaxSize(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false,
            ) { page ->
                when (page) {
                    0 -> DistrictsTab(
                        uiState = districtsUiState,
                        listState = districtsListState,
                        onNavigateToDistrict = onNavigateToDistrict,
                    )
                    1 -> if (showRegionalAdvancement) {
                        RegionalAdvancementTab(
                            uiState = regionalUiState,
                            selectedYear = regionalSelectedYear,
                            listState = regionalListState,
                            onNavigateToTeam = onNavigateToTeam,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DistrictsTab(
    uiState: DistrictsUiState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onNavigateToDistrict: (String) -> Unit,
) {
    when (val state = uiState) {
        is DistrictsUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is DistrictsUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        is DistrictsUiState.Success -> {
            if (state.districts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No districts found")
                }
            } else {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(state.districts, key = { it.key }) { district ->
                        DistrictItem(
                            district = district,
                            onClick = { onNavigateToDistrict(district.key) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RegionalAdvancementTab(
    uiState: RegionalAdvancementUiState,
    selectedYear: Int,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onNavigateToTeam: (String) -> Unit,
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
                        RegionalRankingItem(
                            ranking = ranking,
                            onClick = { onNavigateToTeam(ranking.teamKey) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DistrictItem(
    district: District,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = district.displayName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = district.abbreviation.uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RegionalRankingItem(
    ranking: RegionalRanking,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = "#${ranking.rank} - ${ranking.teamKey.removePrefix("frc")}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = "${ranking.pointTotal} points",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

