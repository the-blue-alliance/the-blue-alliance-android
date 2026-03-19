package com.thebluealliance.android.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.FastScrollbar
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TopBarYearPicker
import com.thebluealliance.android.ui.theme.TBABlue
import com.thebluealliance.android.ui.theme.TBAIndigo400
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Sentinel for [scrollOverride] indicating a scroll-to-favorites is in progress. */
private const val SCROLL_TO_FAVORITES = "\u0000"

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
                                selectedYear = selectedYear,
                                onNavigateToEvent = onNavigateToEvent,
                                listState = listState,
                                reselectFlow = reselectFlow,
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
    reselectFlow: Flow<Unit>,
) {
    val allEvents = remember(sections) { sections.flatMap { it.events } }
    val today = remember { LocalDate.now() }

    val favoriteEvents = remember(allEvents, favoriteEventKeys) {
        if (favoriteEventKeys.isEmpty()) emptyList()
        else allEvents.filter { it.key in favoriteEventKeys }
    }
    val hasFavorites = favoriteEvents.isNotEmpty()

    val weekChipLabels = remember(sections) {
        sections.map { it.label }
    }

    val currentWeekLabel = remember(allEvents, today, selectedYear) {
        currentWeekChipLabel(allEvents, today, selectedYear)
    }

    val headerInfos = remember(sections, favoriteEventKeys) {
        buildHeaderInfos(sections, favoriteEventKeys)
    }

    val weekChipLabelSet = remember(weekChipLabels) { weekChipLabels.toSet() }

    // Programmatic scroll override — locks chip highlight to prevent flickering.
    // SCROLL_TO_FAVORITES sentinel means "star is selected, no week chip".
    // Any other non-null value is the week label override.
    // null means "use scroll-position tracking".
    var scrollOverride by remember { mutableStateOf<String?>(null) }

    val effectiveLabel by remember(headerInfos, weekChipLabelSet, weekChipLabels) {
        derivedStateOf {
            scrollOverride?.let {
                return@derivedStateOf if (it == SCROLL_TO_FAVORITES) null else it
            }
            if (!listState.canScrollForward && weekChipLabels.isNotEmpty()) {
                return@derivedStateOf weekChipLabels.last()
            }
            val firstVisible = listState.firstVisibleItemIndex
            val currentHeader = headerInfos.lastOrNull { it.itemIndex <= firstVisible }
            val label = currentHeader?.label
            if (label != null && label in weekChipLabelSet) label else null
        }
    }
    val effectiveFavoritesSelected by remember(favoriteEvents) {
        derivedStateOf {
            if (scrollOverride == SCROLL_TO_FAVORITES) return@derivedStateOf true
            if (scrollOverride != null) return@derivedStateOf false
            favoriteEvents.isNotEmpty() &&
                listState.firstVisibleItemIndex <= favoriteEvents.size
        }
    }
    val coroutineScope = rememberCoroutineScope()

    // Scrolls list to a section header and locks chip highlight until animation completes.
    suspend fun animateToHeader(label: String) {
        scrollOverride = label
        val targetKey = "header_$label"
        val target = headerInfos.find { it.key == targetKey }
        if (target != null) {
            try {
                listState.animateScrollToItem(target.itemIndex)
            } finally {
                scrollOverride = null
            }
        } else {
            scrollOverride = null
        }
    }

    // Auto-select current week on first load
    var hasAutoSelected by remember(selectedYear) { mutableStateOf(false) }
    LaunchedEffect(currentWeekLabel, weekChipLabels) {
        if (!hasAutoSelected && currentWeekLabel != null && currentWeekLabel in weekChipLabels) {
            hasAutoSelected = true
            animateToHeader(currentWeekLabel)
        }
    }

    // Tab reselect: scroll to current week (or top if no current week)
    LaunchedEffect(reselectFlow) {
        reselectFlow.collect {
            if (currentWeekLabel != null && currentWeekLabel in weekChipLabels) {
                animateToHeader(currentWeekLabel)
            } else {
                scrollOverride = SCROLL_TO_FAVORITES
                try {
                    listState.animateScrollToItem(0)
                } finally {
                    scrollOverride = null
                }
            }
        }
    }

    fun scrollToWeek(label: String) {
        coroutineScope.launch { animateToHeader(label) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        WeekFilterChips(
            weekLabels = weekChipLabels,
            selectedLabel = effectiveLabel,
            onWeekSelected = ::scrollToWeek,
            hasFavorites = hasFavorites,
            isFavoritesSelected = effectiveFavoritesSelected,
            onFavoritesSelected = {
                coroutineScope.launch {
                    scrollOverride = SCROLL_TO_FAVORITES
                    try {
                        listState.animateScrollToItem(0)
                    } finally {
                        scrollOverride = null
                    }
                }
            },
        )

        FastScrollbar(listState = listState, modifier = Modifier.weight(1f)) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                // Top-level Favorites section
                if (favoriteEvents.isNotEmpty()) {
                    item(key = "favorites_header") {
                        SimpleSectionHeader(label = "Favorites")
                    }
                    items(favoriteEvents, key = { "fav_top_${it.key}" }) { event ->
                        EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
                    }
                }
                // Week sections
                sections.forEach { section ->
                    val headerKey = "header_${section.label}"
                    item(key = headerKey) {
                        SimpleSectionHeader(label = section.label)
                    }
                    // Per-week favorites sub-section
                    val sectionFavorites = section.events.filter { it.key in favoriteEventKeys }
                    if (sectionFavorites.isNotEmpty()) {
                        item(key = "${headerKey}_sub_Favorites") {
                            SubSectionHeader(label = "Favorites")
                        }
                        items(sectionFavorites, key = { "fav_${section.label}_${it.key}" }) { event ->
                            EventRow(event = event, onClick = { onNavigateToEvent(event.key) })
                        }
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
    onWeekSelected: (String) -> Unit,
    hasFavorites: Boolean = false,
    isFavoritesSelected: Boolean = false,
    onFavoritesSelected: () -> Unit = {},
) {
    if (weekLabels.isEmpty()) return

    val chipRowState = rememberLazyListState()

    // Scroll chip row: to selected chip, or to start when favorites is active
    LaunchedEffect(selectedLabel, isFavoritesSelected, weekLabels) {
        if (isFavoritesSelected) {
            chipRowState.animateScrollToItem(0)
        } else if (selectedLabel != null) {
            val index = weekLabels.indexOf(selectedLabel)
            if (index >= 0) {
                chipRowState.animateScrollToItem(maxOf(0, index - 1))
            }
        }
    }

    val weekChipColors = FilterChipDefaults.filterChipColors(
        containerColor = Color.Transparent,
        labelColor = Color.White.copy(alpha = 0.7f),
        selectedContainerColor = Color.White,
        selectedLabelColor = TBABlue,
    )
    val weekChipBorder = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = false,
        borderColor = Color.White.copy(alpha = 0.5f),
        selectedBorderColor = Color.Transparent,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TBABlue)
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val overlapPx = 4.dp.roundToPx()
                layout(placeable.width, (placeable.height - overlapPx).coerceAtLeast(0)) {
                    placeable.placeRelative(0, -overlapPx)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (hasFavorites) {
            FilterChip(
                selected = isFavoritesSelected,
                onClick = onFavoritesSelected,
                label = {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Favorites",
                        modifier = Modifier.size(18.dp),
                    )
                },
                modifier = Modifier.padding(start = 12.dp, bottom = 4.dp),
                colors = weekChipColors,
                border = weekChipBorder,
            )
            // Vertical divider between pinned star and scrollable chips
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 4.dp)
                    .height(24.dp)
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
        val fadeWidth = 24.dp
        LazyRow(
            state = chipRowState,
            modifier = Modifier
                .weight(1f)
                .then(
                    if (hasFavorites) {
                        Modifier.drawWithContent {
                            drawContent()
                            if (chipRowState.canScrollBackward) {
                                val solidZone = 4.dp.toPx()
                                val totalWidth = fadeWidth.toPx()
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colorStops = arrayOf(
                                            0f to TBABlue,
                                            solidZone / totalWidth to TBABlue,
                                            1f to Color.Transparent,
                                        ),
                                        endX = totalWidth,
                                    ),
                                    size = size.copy(width = totalWidth),
                                )
                            }
                        }
                    } else Modifier
                ),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 12.dp,
                bottom = 4.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(weekLabels.size) { index ->
                val label = weekLabels[index]
                FilterChip(
                    selected = selectedLabel == label && !isFavoritesSelected,
                    onClick = { onWeekSelected(label) },
                    label = { Text(label) },
                    colors = weekChipColors,
                    border = weekChipBorder,
                )
            }
        }
    }
}

@Composable
private fun SimpleSectionHeader(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TBAIndigo400)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
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

/** Returns the chip label for the current competition week, or null. */
private fun currentWeekChipLabel(
    allEvents: List<Event>,
    today: LocalDate,
    selectedYear: Int,
): String? {
    if (selectedYear != today.year) return null
    val week = findCurrentCompetitionWeek(allEvents, today) ?: return null
    return "Week ${week + 1}"
}
