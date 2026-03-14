package com.thebluealliance.android.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.FastScrollbar
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.SectionHeaderInfo
import com.thebluealliance.android.ui.components.TBATopAppBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    var yearDropdownExpanded by remember { mutableStateOf(false) }
    var selectedWeekLabel by remember { mutableStateOf<String?>(null) }

    // Reset week filter when year changes
    LaunchedEffect(selectedYear) {
        selectedWeekLabel = null
    }

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
                    if (selectedYear > 0) {
                        Row(
                            modifier = Modifier.clickable { yearDropdownExpanded = true },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("$selectedYear Events")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select year")
                            DropdownMenu(
                                expanded = yearDropdownExpanded,
                                onDismissRequest = { yearDropdownExpanded = false },
                            ) {
                                (maxYear downTo 1992).forEach { year ->
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
                    } else {
                        Text("Events")
                    }
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
                                selectedWeekLabel = selectedWeekLabel,
                                onWeekSelected = { label ->
                                    selectedWeekLabel = label
                                },
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
    selectedWeekLabel: String?,
    onWeekSelected: (String?) -> Unit,
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

    // Build week chip labels from sections (these are the main data sections, not favorites/this-week)
    val weekChipLabels = remember(sections) {
        sections.map { it.label }
    }

    // Auto-detect current week for initial selection
    val currentWeekLabel = remember(allEvents, today, selectedYear) {
        if (selectedYear != today.year) return@remember null
        val week = findCurrentCompetitionWeek(allEvents, today)
        if (week != null) "Week ${week + 1}" else null
    }

    // Auto-select current week on first data load for the current year.
    // Uses a key that resets when selectedYear changes, allowing re-auto-selection.
    var hasAutoSelected by remember(selectedYear) { mutableStateOf(false) }
    LaunchedEffect(currentWeekLabel, weekChipLabels) {
        if (!hasAutoSelected && currentWeekLabel != null && currentWeekLabel in weekChipLabels) {
            onWeekSelected(currentWeekLabel)
            hasAutoSelected = true
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
            // At scroll position 0, the first header is always stuck
            stuck ?: headerInfos.firstOrNull()?.key
        }
    }

    val coroutineScope = rememberCoroutineScope()

    // Scroll to selected week section when chip is tapped (or auto-selected)
    LaunchedEffect(selectedWeekLabel) {
        if (selectedWeekLabel == null) {
            listState.animateScrollToItem(0)
        } else {
            val targetKey = "header_$selectedWeekLabel"
            val target = headerInfos.find { it.key == targetKey }
            if (target != null) {
                listState.animateScrollToItem(target.itemIndex)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        WeekFilterChips(
            weekLabels = weekChipLabels,
            selectedLabel = selectedWeekLabel,
            onWeekSelected = onWeekSelected,
        )

        FastScrollbar(listState = listState, modifier = Modifier.weight(1f)) {
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
}

@Composable
private fun WeekFilterChips(
    weekLabels: List<String>,
    selectedLabel: String?,
    onWeekSelected: (String?) -> Unit,
) {
    if (weekLabels.isEmpty()) return

    val chipRowState = rememberLazyListState()

    // Scroll to the selected chip when it changes
    LaunchedEffect(selectedLabel, weekLabels) {
        if (selectedLabel != null) {
            val index = weekLabels.indexOf(selectedLabel)
            // +1 because the "All" chip is at index 0
            if (index >= 0) {
                chipRowState.animateScrollToItem(index + 1)
            }
        }
    }

    LazyRow(
        state = chipRowState,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selectedLabel == null,
                onClick = { onWeekSelected(null) },
                label = { Text("All") },
            )
        }
        items(weekLabels.size) { index ->
            val label = weekLabels[index]
            FilterChip(
                selected = selectedLabel == label,
                onClick = { onWeekSelected(label) },
                label = { Text(label) },
            )
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
