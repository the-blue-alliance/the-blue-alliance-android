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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.FastScrollbar
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.SectionHeaderInfo
import com.thebluealliance.android.ui.theme.TBABlue
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
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

    var yearDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(reselectFlow) {
        reselectFlow.collect {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.tba_lamp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TBABlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
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
    val thisWeekResult = remember(allEvents, today, selectedYear) {
        computeThisWeekEvents(allEvents, today, selectedYear)
    }

    val headerInfos = remember(sections, favoriteEvents, thisWeekResult) {
        buildList {
            var index = 0
            if (favoriteEvents.isNotEmpty()) {
                add(SectionHeaderInfo("favorites_header", "Favorites", index))
                index += 1 + favoriteEvents.size + 1 // header + items + divider
            }
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
                item(key = "favorites_divider") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
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
                items(thisWeekResult.events, key = { "thisweek_${it.key}" }) { event ->
                    EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
                }
                item(key = "this_week_divider") {
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
                    EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
                }
            }
        }
    }
}
