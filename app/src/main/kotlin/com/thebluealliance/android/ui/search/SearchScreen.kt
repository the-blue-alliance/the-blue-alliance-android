package com.thebluealliance.android.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.components.EscapeHandler
import com.thebluealliance.android.ui.components.EventRow
import com.thebluealliance.android.ui.components.ReadingMaxWidth
import com.thebluealliance.android.ui.components.ReadingWidthBox
import com.thebluealliance.android.ui.components.SectionHeader
import com.thebluealliance.android.ui.components.SectionHeaderInfo
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TeamRow
import com.thebluealliance.android.ui.components.TooltipIconButton
import com.thebluealliance.android.ui.components.onlyBottom
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.readingColumnFit
import com.thebluealliance.android.ui.components.rememberStickyHeaderFocusOrder
import com.thebluealliance.android.ui.components.stickyHeaderFocusOrder
import com.thebluealliance.android.ui.components.tabOrderedStickyHeader
import com.thebluealliance.android.ui.components.verticalScrollbar
import com.thebluealliance.android.ui.components.withoutBottom
import com.thebluealliance.android.ui.theme.TbaIcons
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateUp: () -> Unit,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    // Search is a temporary surface (Ctrl+F opens it over any screen), so with focus anywhere but
    // the field, Esc closes it. The field handles Esc itself first: clear, then leave the field.
    EscapeHandler(onEscape = onNavigateUp)
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(uiState.teams, uiState.events) {
        lazyListState.scrollToItem(0)
    }

    val headerInfos =
        remember(uiState.teams.size, uiState.events.size) {
            buildList {
                var index = 0
                if (uiState.teams.isNotEmpty()) {
                    add(SectionHeaderInfo("teams_header", "Teams", index))
                    index += 1 + uiState.teams.size
                }
                if (uiState.events.isNotEmpty()) {
                    add(SectionHeaderInfo("events_header", "Events", index))
                }
            }
        }

    val headerKeys =
        remember(headerInfos) {
            headerInfos.map { it.key }.toSet()
        }

    val stuckHeaderKey by remember(headerKeys) {
        derivedStateOf {
            val stuck =
                lazyListState.layoutInfo.visibleItemsInfo
                    .firstOrNull { item ->
                        val key = item.key as? String
                        key != null && key in headerKeys && item.offset <= 0
                    }?.key as? String
            stuck ?: headerInfos.firstOrNull()?.key
        }
    }

    // The field shares the reading column's edges with the results below it. Until both have been
    // placed it fills its slot, as it does wherever the column fills the window.
    var fieldBounds by remember { mutableStateOf<Rect?>(null) }
    var contentBounds by remember { mutableStateOf<Rect?>(null) }
    val density = LocalDensity.current
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val fieldFit by remember(density, rtl) {
        derivedStateOf {
            val field = fieldBounds ?: return@derivedStateOf null
            val content = contentBounds ?: return@derivedStateOf null
            val fit =
                readingColumnFit(
                    fieldStart = if (rtl) field.right else field.left,
                    areaLeft = content.left,
                    areaRight = content.right,
                    column = with(density) { ReadingMaxWidth.toPx() },
                    rtl = rtl,
                )
            with(density) { SearchFieldFit(fit.start.toDp(), fit.maxWidth.toDp()) }
        }
    }

    Scaffold(
        // Search opens with the keyboard up: the bottom padding covers it, not just the navigation
        // bar, so the last result scrolls clear of it and the messages center above it.
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.ime),
        topBar = {
            TBATopAppBar(
                title = {
                    SearchFieldSlot(
                        fit = fieldFit,
                        modifier =
                            Modifier.onGloballyPositioned {
                                fieldBounds = it.unclippedBoundsInRoot()
                            },
                    ) {
                        TextField(
                            state = viewModel.queryState,
                            placeholder = { Text("Search teams and events") },
                            lineLimits = TextFieldLineLimits.SingleLine,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .onPreviewKeyEvent { event ->
                                        if (event.key != Key.Escape) return@onPreviewKeyEvent false
                                        if (event.type == KeyEventType.KeyUp) {
                                            when (searchFieldEscape(viewModel.queryState.text)) {
                                                SearchFieldEscape.ClearText ->
                                                    viewModel.queryState.clearText()
                                                SearchFieldEscape.ClearFocus ->
                                                    focusManager
                                                        .clearFocus()
                                            }
                                        }
                                        true
                                    },
                            colors =
                                TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedPlaceholderColor =
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    unfocusedPlaceholderColor =
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    cursorColor = MaterialTheme.colorScheme.primary,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                                    focusedTrailingIconColor =
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    unfocusedTrailingIconColor =
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            trailingIcon = {
                                if (viewModel.queryState.text.isNotEmpty()) {
                                    TooltipIconButton(
                                        label = "Clear",
                                        icon = TbaIcons.Close,
                                        onClick = { viewModel.queryState.clearText() },
                                    )
                                }
                            },
                        )
                    }
                },
                navigationIcon = {
                    TooltipIconButton("Back", TbaIcons.ArrowBack, onNavigateUp)
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .pageBackground()
                    .padding(innerPadding.withoutBottom())
                    .fillMaxSize()
                    .onGloballyPositioned { contentBounds = it.unclippedBoundsInRoot() },
        ) {
            // The messages center above the keyboard or the navigation bar, whichever is taller,
            // following the keyboard as it opens and closes. The fill still reaches the window's
            // bottom edge behind them.
            if (uiState.query.isBlank()) {
                EmptyBox(
                    message = "Try a team number, team name or event name",
                    modifier = Modifier.padding(innerPadding.onlyBottom()),
                )
                return@Column
            }
            // Only declare "no results" once the debounced search has caught up with
            // what the user typed, so the message can't flash mid-keystroke.
            val searchSettled = uiState.query == uiState.searchedQuery
            if (searchSettled && uiState.teams.isEmpty() && uiState.events.isEmpty()) {
                EmptyBox(
                    message = "No results for \"${uiState.query}\"",
                    modifier = Modifier.padding(innerPadding.onlyBottom()),
                )
                return@Column
            }
            ReadingWidthBox(innerPadding.onlyBottom(), Modifier.fillMaxSize()) { readingPadding ->
                val headerFocus = rememberStickyHeaderFocusOrder()
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .stickyHeaderFocusOrder(headerFocus)
                            .verticalScrollbar(lazyListState, readingPadding),
                    state = lazyListState,
                    contentPadding = readingPadding,
                ) {
                    if (uiState.teams.isNotEmpty()) {
                        tabOrderedStickyHeader(headerFocus, key = "teams_header") {
                            SectionHeader(
                                label = "Teams",
                                isStuck = stuckHeaderKey == "teams_header",
                                allHeaders = headerInfos,
                                onHeaderSelected = { info ->
                                    coroutineScope.launch {
                                        lazyListState.animateScrollToItem(info.itemIndex)
                                    }
                                },
                            )
                        }
                        items(uiState.teams, key = { "team_${it.key}" }) { team ->
                            TeamRow(team = team, onClick = { onNavigateToTeam(team.key) })
                        }
                    }
                    if (uiState.events.isNotEmpty()) {
                        tabOrderedStickyHeader(headerFocus, key = "events_header") {
                            SectionHeader(
                                label = "Events",
                                isStuck = stuckHeaderKey == "events_header",
                                allHeaders = headerInfos,
                                onHeaderSelected = { info ->
                                    coroutineScope.launch {
                                        lazyListState.animateScrollToItem(info.itemIndex)
                                    }
                                },
                            )
                        }
                        items(uiState.events, key = { "event_${it.key}" }) { event ->
                            EventRow(
                                event = event,
                                onClick = { onNavigateToEvent(event.key) },
                                showYear = true,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** What Esc does in the search field: the first clears the query, the next leaves the field. */
internal enum class SearchFieldEscape { ClearText, ClearFocus }

internal fun searchFieldEscape(query: CharSequence): SearchFieldEscape =
    if (query.isEmpty()) SearchFieldEscape.ClearFocus else SearchFieldEscape.ClearText

/**
 * Places [field] in the top-bar slot at [fit]'s start offset and no wider than its width, or across
 * the whole slot while [fit] is null.
 *
 * The fit arrives after the first layout. It moves the field by placing it again, not with padding:
 * in a TopAppBar title, padding that changes later leaves Compose reporting the field's old window
 * position for everything inside it, and Clear's tooltip would open where Clear used to be.
 */
@Composable
internal fun SearchFieldSlot(
    fit: SearchFieldFit?,
    modifier: Modifier = Modifier,
    field: @Composable () -> Unit,
) {
    Layout(content = field, modifier = modifier) { measurables, constraints ->
        val start = fit?.start?.roundToPx()?.coerceIn(0, constraints.maxWidth) ?: 0
        val room = constraints.maxWidth - start
        val width = fit?.maxWidth?.roundToPx()?.coerceIn(0, room) ?: room
        val placeable =
            measurables.first().measure(constraints.copy(minWidth = width, maxWidth = width))
        val height = placeable.height.coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(constraints.maxWidth, height) { placeable.placeRelative(start, 0) }
    }
}

/** Start offset and widest size of the search field, in its top-bar slot. */
internal data class SearchFieldFit(
    val start: Dp,
    val maxWidth: Dp,
)

/** Bounds in the root, unclipped, so a screen sliding in during a transition still measures whole. */
private fun LayoutCoordinates.unclippedBoundsInRoot(): Rect =
    Rect(positionInRoot(), Size(size.width.toFloat(), size.height.toFloat()))
