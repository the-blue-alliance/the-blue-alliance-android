package com.thebluealliance.android.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.ui.components.FastScrollbar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onNavigateToEvent: (String) -> Unit,
    scrollToTopTrigger: Int = 0,
    onYearState: (selectedYear: Int, maxYear: Int, onYearSelected: (Int) -> Unit) -> Unit = { _, _, _ -> },
    viewModel: EventsViewModel = hiltViewModel(),
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
                            onEventClick = onNavigateToEvent,
                            listState = listState,
                        )
                    }
                }
            }
        }
    }
}

private data class SectionHeaderInfo(val key: String, val label: String, val itemIndex: Int)

@Composable
private fun EventsList(
    sections: List<EventSection>,
    favoriteEventKeys: Set<String>,
    onEventClick: (String) -> Unit,
    listState: LazyListState,
) {
    val allEvents = sections.flatMap { it.events }
    val favoriteEvents = if (favoriteEventKeys.isNotEmpty()) {
        allEvents.filter { it.key in favoriteEventKeys }
    } else {
        emptyList()
    }
    val today = remember { LocalDate.now() }
    val happeningNowEvents = allEvents.filter { event ->
        val start = event.startDate?.let { LocalDate.parse(it) }
        val end = event.endDate?.let { LocalDate.parse(it) }
        start != null && end != null && !today.isBefore(start) && !today.isAfter(end)
    }

    val headerInfos = remember(sections, favoriteEvents, happeningNowEvents) {
        buildList {
            var index = 0
            if (favoriteEvents.isNotEmpty()) {
                add(SectionHeaderInfo("favorites_header", "Favorites", index))
                index += 1 + favoriteEvents.size + 1 // header + items + divider
            }
            if (happeningNowEvents.isNotEmpty()) {
                add(SectionHeaderInfo("happening_now_header", "Happening now", index))
                index += 1 + happeningNowEvents.size + 1 // header + items + divider
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
                    EventItem(event = event, onClick = { onEventClick(event.key) })
                }
                item(key = "favorites_divider") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
            if (happeningNowEvents.isNotEmpty()) {
                stickyHeader(key = "happening_now_header") {
                    SectionHeader(
                        label = "Happening now",
                        isStuck = stuckHeaderKey == "happening_now_header",
                        allHeaders = headerInfos,
                        onHeaderSelected = { info ->
                            coroutineScope.launch {
                                listState.animateScrollToItem(info.itemIndex)
                            }
                        },
                    )
                }
                items(happeningNowEvents, key = { "now_${it.key}" }) { event ->
                    EventItem(event = event, onClick = { onEventClick(event.key) })
                }
                item(key = "happening_now_divider") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
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
                    EventItem(event = event, onClick = { onEventClick(event.key) })
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    label: String,
    isStuck: Boolean,
    allHeaders: List<SectionHeaderInfo>,
    onHeaderSelected: (SectionHeaderInfo) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(enabled = isStuck) { menuExpanded = true },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            if (isStuck) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Jump to section",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            allHeaders.forEach { info ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = info.label,
                            fontWeight = if (info.label == label) FontWeight.Bold else FontWeight.Normal,
                            color = if (info.label == label) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onHeaderSelected(info)
                    },
                )
            }
        }
    }
}

@Composable
private fun EventItem(
    event: Event,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = event.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
        val location = listOfNotNull(event.city, event.state, event.country)
            .joinToString(", ")
        if (location.isNotEmpty()) {
            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        val dateRange = formatEventDateRange(event.startDate, event.endDate)
        if (dateRange != null) {
            Text(
                text = dateRange,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

internal val fullFormat = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.US)
internal val noYearFormat = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)

internal fun formatEventDateRange(startDate: String?, endDate: String?): String? {
    if (startDate == null) return null
    val start = LocalDate.parse(startDate)
    val end = endDate?.let { LocalDate.parse(it) }
    if (end == null || start == end) return start.format(fullFormat)
    return if (start.year == end.year) {
        "${start.format(noYearFormat)} - ${end.format(fullFormat)}"
    } else {
        "${start.format(fullFormat)} - ${end.format(fullFormat)}"
    }
}
