package com.thebluealliance.android.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.FastScrollbar
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.SectionHeaderInfo
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarYearPicker
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onNavigateToEvent: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    reselectFlow: Flow<Unit>,
    viewModel: EventsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val maxYear by viewModel.maxYear.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

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
                    TopBarYearPicker(
                        selectedYear = selectedYear,
                        years = if (selectedYear > 0) (maxYear downTo 1992).toList() else emptyList(),
                        onYearSelected = viewModel::selectYear,
                        title = { Text("Events") },
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                showLamp = true
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing && uiState !is EventsUiState.Loading,
                onRefresh = viewModel::refreshEvents,
                modifier = Modifier.fillMaxSize(),
            ) {
                when (val state = uiState) {
                    is EventsUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is EventsUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(state.message, style = MaterialTheme.typography.bodyLarge)
                                Button(onClick = viewModel::refreshEvents) {
                                    Text("Retry")
                                }
                            }
                        }
                    }

                    is EventsUiState.Success -> {
                        if (state.sections.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("No events found for $selectedYear")
                                    Button(onClick = viewModel::refreshEvents) {
                                        Text("Retry")
                                    }
                                }
                            }
                        } else {
                            EventsList(
                                sections = state.sections,
                                favoriteEventKeys = state.favoriteEventKeys,
                                districtNames = state.districtNames,
                                selectedYear = selectedYear,
                                onNavigateToEvent = onNavigateToEvent,
                                listState = listState,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsList(
    sections: List<EventSection>,
    favoriteEventKeys: Set<String>,
    districtNames: Map<String, String>,
    selectedYear: Int,
    onNavigateToEvent: (String) -> Unit,
    listState: LazyListState,
) {
    val allEvents = sections.flatMap { it.events }
    val favoriteEvents = if (favoriteEventKeys.isNotEmpty()) {
        allEvents.filter { it.key in favoriteEventKeys }
    } else {
        emptyList()
    }
    val today = remember { LocalDate.now() }
    val thisWeekResult = remember(allEvents, today, selectedYear, districtNames) {
        computeThisWeekEvents(allEvents, today, selectedYear, districtNames)
    }

    val headerInfos = remember(sections, favoriteEvents, thisWeekResult) {
        buildHeaderInfos(sections, favoriteEvents, thisWeekResult)
    }

    val headerKeys = remember(headerInfos) { headerInfos.map { it.key }.toSet() }

    val stuckHeaderKey by remember {
        derivedStateOf {
            val stuck = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { item ->
                    val key = item.key as? String
                    key != null && key in headerKeys && item.offset <= 0
                }?.key as? String
            // At scroll position 0, the first header is always stuck
            stuck ?: headerInfos.firstOrNull()?.key
        }
    }

    val coroutineScope = rememberCoroutineScope()

    FastScrollbar(listState = listState) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            if (favoriteEvents.isNotEmpty()) {
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
                items(favoriteEvents, key = { "fav_${it.key}" }) { event ->
                    EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
                }
            }
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
                thisWeekResult.subSections.forEach { subSection ->
                    if (subSection.label.isNotEmpty()) {
                        item(key = "thisweek_sub_${subSection.label}") {
                            SubSectionHeader(label = subSection.label)
                        }
                    }
                    items(subSection.events, key = { "thisweek_${it.key}" }) { event ->
                        EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
                    }
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
                section.subSections.forEach { subSection ->
                    if (subSection.label.isNotEmpty()) {
                        item(key = "${headerKey}_sub_${subSection.label}") {
                            SubSectionHeader(label = subSection.label)
                        }
                    }
                    items(subSection.events, key = { it.key }) { event ->
                        EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SubSectionHeader(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
