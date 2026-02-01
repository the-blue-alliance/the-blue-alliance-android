package com.thebluealliance.android.ui.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Event
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
                    if (state.eventsByWeek.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No events found for $selectedYear")
                        }
                    } else {
                        EventsList(
                            eventsByWeek = state.eventsByWeek,
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

@Composable
private fun EventsList(
    eventsByWeek: Map<Int?, List<Event>>,
    favoriteEventKeys: Set<String>,
    onEventClick: (String) -> Unit,
    listState: LazyListState,
) {
    val allEvents = eventsByWeek.values.flatten()
    val favoriteEvents = if (favoriteEventKeys.isNotEmpty()) {
        allEvents.filter { it.key in favoriteEventKeys }
    } else {
        emptyList()
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        if (favoriteEvents.isNotEmpty()) {
            item(key = "favorites_header") {
                Text(
                    text = "Favorites",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            items(favoriteEvents, key = { "fav_${it.key}" }) { event ->
                EventItem(event = event, onClick = { onEventClick(event.key) })
            }
            item(key = "favorites_divider") {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
        eventsByWeek.forEach { (week, events) ->
            item(key = "header_$week") {
                Text(
                    text = if (week != null) "Week $week" else "Other Events",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            items(events, key = { it.key }) { event ->
                EventItem(event = event, onClick = { onEventClick(event.key) })
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

private val fullFormat = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.US)
private val noYearFormat = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)

private fun formatEventDateRange(startDate: String?, endDate: String?): String? {
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
