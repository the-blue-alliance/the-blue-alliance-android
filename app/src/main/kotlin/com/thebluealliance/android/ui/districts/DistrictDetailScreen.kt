package com.thebluealliance.android.ui.districts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.SectionHeaderInfo
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.TBATabRow
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.events.EventSection
import kotlinx.coroutines.launch

private val TABS = listOf("Events", "Rankings")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictDetailScreen(
    onNavigateUp: () -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: DistrictDetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { TABS.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TBATopAppBar(
                title = {
                    Text(
                        text = uiState.district?.displayName ?: "District",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TBATabRow(selectedTabIndex = pagerState.currentPage) {
                TABS.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = title,
                                color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        },
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing && uiState.eventSections != null && uiState.rankings != null,
                onRefresh = viewModel::refreshAll,
                modifier = Modifier.fillMaxSize(),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    when (page) {
                        0 -> EventsTab(uiState.eventSections, onNavigateToEvent)
                        1 -> RankingsTab(uiState.rankings, onNavigateToTeam)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EventsTab(sections: List<EventSection>?, onNavigateToEvent: (String) -> Unit) {
    if (sections == null) {
        LoadingBox()
        return
    }
    if (sections.isEmpty()) {
        EmptyBox("No events")
        return
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val headerInfos = remember(sections) {
        buildList {
            var index = 0
            sections.forEach { section ->
                val headerKey = "header_${section.label}"
                add(SectionHeaderInfo(headerKey, section.label, index))
                index += 1 + section.events.size // header + items
            }
        }
    }

    val headerKeys = remember(headerInfos) { headerInfos.map { it.key }.toSet() }

    val stuckHeaderKey by remember {
        derivedStateOf {
            val stuck = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { item ->
                    val key = item.key as? String
                    key != null && key in headerKeys && item.offset <= 0
                }?.key as? String
            stuck ?: headerInfos.firstOrNull()?.key
        }
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        sections.forEach { section ->
            val headerKey = "header_${section.label}"
            stickyHeader(key = headerKey) {
                SectionHeader(
                    label = section.label,
                    isStuck = stuckHeaderKey == headerKey,
                    allHeaders = headerInfos,
                    onHeaderSelected = { info ->
                        coroutineScope.launch {
                            listState.animateScrollToItem(info.itemIndex)
                        }
                    },
                )
            }
            items(section.events, key = { it.key }) { event ->
                EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
            }
        }
    }
}

@Composable
private fun RankingsTab(rankings: List<DistrictRanking>?, onNavigateToTeam: (String) -> Unit) {
    if (rankings == null) {
        LoadingBox()
        return
    }
    if (rankings.isEmpty()) {
        EmptyBox("No rankings")
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(rankings, key = { "${it.districtKey}_${it.teamKey}" }) { ranking ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTeam(ranking.teamKey) }
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
                    text = "${ranking.pointTotal.toInt()} pts",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.25f),
                )
                if (ranking.rookieBonus > 0) {
                    Text(
                        text = "+${ranking.rookieBonus.toInt()} rookie",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.25f),
                    )
                }
            }
        }
    }
}

