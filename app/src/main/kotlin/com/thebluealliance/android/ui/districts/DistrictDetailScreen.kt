package com.thebluealliance.android.ui.districts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.DistrictEventPoints
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.common.TouchPullToRefreshBox
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.SectionHeaderInfo
import com.thebluealliance.android.ui.components.TBATab
import com.thebluealliance.android.ui.components.TBATabRow
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarYearPicker
import com.thebluealliance.android.ui.components.blocksRowsBeneath
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.pagerPageFocus
import com.thebluealliance.android.ui.components.tableWidth
import com.thebluealliance.android.ui.events.EventSection
import com.thebluealliance.android.ui.events.computeThisWeekEvents
import com.thebluealliance.android.ui.theme.BrandHeaderContainer
import com.thebluealliance.android.ui.theme.OnBrand
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.teamNumber
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class DistrictDetailTab(
    val readableName: String,
) {
    EVENTS("Events"),
    RANKINGS("Rankings"),
}

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
    val pagerState = rememberPagerState(pageCount = { DistrictDetailTab.entries.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Column {
                TBATopAppBar(
                    onRefresh = {
                        viewModel.refreshTab(
                            DistrictDetailTab.entries[pagerState.currentPage],
                        )
                    },
                    isRefreshing =
                        isRefreshing && uiState.eventSections != null && uiState.rankings != null,
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
                                imageVector = TbaIcons.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    secondaryActions =
                        listOf(
                            TopBarAction("Search", TbaIcons.Search, onNavigateToSearch),
                        ),
                )

                TBATabRow(selectedTabIndex = pagerState.currentPage) {
                    DistrictDetailTab.entries.forEach { tab ->
                        TBATab(
                            label = tab.readableName,
                            selected = pagerState.currentPage == tab.ordinal,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        tab.ordinal,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        val bottomPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .pageBackground(),
        ) { page ->
            TouchPullToRefreshBox(
                isRefreshing =
                    isRefreshing && uiState.eventSections != null && uiState.rankings != null,
                onRefresh = { viewModel.refreshTab(DistrictDetailTab.entries[page]) },
                modifier = Modifier.fillMaxSize().pagerPageFocus(pagerState, page),
            ) {
                when (DistrictDetailTab.entries[page]) {
                    DistrictDetailTab.EVENTS ->
                        EventsTab(
                            sections = uiState.eventSections,
                            onNavigateToEvent = onNavigateToEvent,
                            innerPadding = bottomPadding,
                        )
                    DistrictDetailTab.RANKINGS ->
                        RankingsTab(
                            rankings = uiState.rankings,
                            onNavigateToTeam = onNavigateToTeam,
                            innerPadding = bottomPadding,
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
    val thisWeekResult =
        remember(allEvents, today) {
            computeThisWeekEvents(allEvents, today, today.year)
        }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val headerInfos =
        remember(sections, thisWeekResult) {
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
            val stuck =
                listState.layoutInfo.visibleItemsInfo
                    .firstOrNull { item ->
                        val key = item.key as? String
                        key != null && key in headerKeys && item.offset <= 0
                    }?.key as? String
            stuck ?: headerInfos.firstOrNull()?.key
        }
    }

    ReadingWidthBox(innerPadding, Modifier.fillMaxSize()) { readingPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = readingPadding,
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
            modifier = Modifier.padding(innerPadding),
        )
        return
    }
    if (rankings.isEmpty()) {
        EmptyBox(
            modifier = Modifier.padding(innerPadding),
            message = "No rankings",
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        stickyHeader {
            Row(
                modifier =
                    Modifier
                        .tableWidth()
                        .blocksRowsBeneath()
                        .background(BrandHeaderContainer)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Rank",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBrand,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.10f),
                )
                Text(
                    text = "Team",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBrand,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = "Event\n1",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBrand,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = "Event\n2",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBrand,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = "DCMP",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBrand,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = "Rookie\nBonus",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBrand,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
                Text(
                    text = "Total\nPoints",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBrand,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f),
                )
            }
        }

        items(rankings, key = { "${it.districtKey}_${it.teamKey}" }) { ranking ->
            val eventPoints =
                ranking.eventPoints
                    .filterNot { it.districtCmp }
                    .map { it.total.toInt() }
            val event1Points = eventPoints.getOrNull(0)
            val event2Points = eventPoints.getOrNull(1)
            val dcmpPoints = dcmpPoints(ranking.eventPoints)
            val rookieBonus = ranking.rookieBonus.toInt()
            val description =
                districtRowDescription(
                    rank = ranking.rank,
                    teamNumber = ranking.teamKey.teamNumber,
                    event1 = event1Points,
                    event2 = event2Points,
                    dcmp = dcmpPoints,
                    rookieBonus = rookieBonus,
                    total = ranking.pointTotal.toInt(),
                )

            Row(
                modifier =
                    Modifier
                        .tableWidth()
                        .clickable(onClickLabel = "View team ${ranking.teamKey.teamNumber}") {
                            onNavigateToTeam(ranking.teamKey)
                        }.semantics { contentDescription = description }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "#${ranking.rank}",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.10f).clearAndSetSemantics {},
                )
                Text(
                    text = ranking.teamKey.teamNumber,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f).clearAndSetSemantics {},
                )
                Text(
                    text = event1Points?.toString() ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f).clearAndSetSemantics {},
                )
                Text(
                    text = event2Points?.toString() ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f).clearAndSetSemantics {},
                )
                Text(
                    text = dcmpPoints?.toString() ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f).clearAndSetSemantics {},
                )
                Text(
                    text = if (rookieBonus > 0) "+$rookieBonus" else "-",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f).clearAndSetSemantics {},
                )
                Text(
                    text = "${ranking.pointTotal.toInt()} pts",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.15f).clearAndSetSemantics {},
                )
            }
            HorizontalDivider(
                modifier = Modifier.tableWidth().padding(horizontal = 16.dp),
                thickness = 0.5.dp,
            )
        }
    }
}

/** DCMPs with divisions report division + finals as separate districtCmp entries; sum them. */
internal fun dcmpPoints(eventPoints: List<DistrictEventPoints>): Int? {
    val dcmpEntries = eventPoints.filter { it.districtCmp }
    if (dcmpEntries.isEmpty()) return null
    return dcmpEntries.sumOf { it.total }.toInt()
}

/** One spoken summary of a district rankings row; a null value is an empty cell. */
internal fun districtRowDescription(
    rank: Int,
    teamNumber: String,
    event1: Int?,
    event2: Int?,
    dcmp: Int?,
    rookieBonus: Int,
    total: Int,
): String =
    buildList {
        add("Rank $rank")
        add("Team $teamNumber")
        add(event1?.let { "Event 1: $it points" } ?: "Event 1 none")
        add(event2?.let { "Event 2: $it points" } ?: "Event 2 none")
        add(dcmp?.let { "DCMP: $it points" } ?: "DCMP none")
        add(if (rookieBonus > 0) "rookie bonus $rookieBonus" else "rookie bonus none")
        add("$total total points")
    }.joinToString(", ")
