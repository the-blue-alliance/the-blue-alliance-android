package com.thebluealliance.android.ui.districts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.thebluealliance.android.ui.components.TopBarYearPicker
import com.thebluealliance.android.ui.events.EventSection
import com.thebluealliance.android.ui.events.computeThisWeekEvents
import com.thebluealliance.android.ui.theme.TBAIndigo400
import kotlinx.coroutines.launch
import java.time.LocalDate

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
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val availableYears by viewModel.availableYears.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { TABS.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                TBATopAppBar(
                    title = {
                        TopBarYearPicker(
                            selectedYear = selectedYear,
                            years = availableYears,
                            onYearSelected = viewModel::selectYear,
                            title = {
                                Text(
                                    text = uiState.district?.displayName ?: "District",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSearch) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                )

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
            }
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing && uiState.eventSections != null && uiState.rankings != null,
            onRefresh = viewModel::refreshAll,
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> EventsTab(
                        sections = uiState.eventSections,
                        onNavigateToEvent = onNavigateToEvent,
                        innerPadding = PaddingValues(0.dp),
                    )
                    1 -> RankingsTab(
                        rankings = uiState.rankings,
                        onNavigateToTeam = onNavigateToTeam,
                        innerPadding = PaddingValues(0.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EventsTab(
    sections: List<EventSection>?,
    onNavigateToEvent: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (sections == null) {
        LoadingBox()
        return
    }
    if (sections.isEmpty()) {
        EmptyBox("No events")
        return
    }

    val allEvents = sections.flatMap { it.events }
    val today = remember { LocalDate.now() }
    val thisWeekResult = remember(allEvents, today) {
        computeThisWeekEvents(allEvents, today, today.year)
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val headerInfos = remember(sections, thisWeekResult) {
        buildList {
            var index = 0
            if (thisWeekResult != null) {
                add(SectionHeaderInfo("this_week_header", thisWeekResult.label, index))
                index += 1 + thisWeekResult.events.size + 1 // header + items + divider
            }
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

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        if (thisWeekResult != null) {
            stickyHeader(key = "this_week_header") {
                SectionHeader(
                    label = thisWeekResult.label,
                    isStuck = stuckHeaderKey == "this_week_header",
                    allHeaders = headerInfos,
                    onHeaderSelected = { info ->
                        coroutineScope.launch {
                            listState.animateScrollToItem(info.itemIndex)
                        }
                    },
                )
            }
            items(thisWeekResult.events, key = { "thisweek_${it.key}" }) { event ->
                EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
            }
            item(key = "this_week_divider") {
                HorizontalDivider()
            }
        }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RankingsTab(
    rankings: List<DistrictRanking>?,
    onNavigateToTeam: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (rankings == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding)
        )
        return
    }
    if (rankings.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No rankings"
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TBAIndigo400)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rank",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.10f)
                )
                Text(
                    text = "Team",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f)
                )
                Text(
                    text = "Event\n1",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f)
                )
                Text(
                    text = "Event\n2",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f)
                )
                Text(
                    text = "DCMP",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f)
                )
                Text(
                    text = "Rookie\nBonus",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f)
                )
                Text(
                    text = "Total\nPoints",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f)
                )
            }
        }

        items(rankings, key = { "${it.districtKey}_${it.teamKey}" }) { ranking ->
            val event1Points = ranking.eventPoints.filter { !it.districtCmp }.getOrNull(0)?.total?.toInt()?.toString() ?: "-"
            val event2Points = ranking.eventPoints.filter { !it.districtCmp }.getOrNull(1)?.total?.toInt()?.toString() ?: "-"
            val dcmpPoints = ranking.eventPoints.find { it.districtCmp }?.total?.toInt()?.toString() ?: "-"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTeam(ranking.teamKey) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "#${ranking.rank}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.10f),
                )
                Text(
                    text = ranking.teamKey.removePrefix("frc"),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = event1Points,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = event2Points,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = dcmpPoints,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = if (ranking.rookieBonus > 0) "+${ranking.rookieBonus.toInt()}" else "-",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = "${ranking.pointTotal.toInt()} pts",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
        }
    }
}
