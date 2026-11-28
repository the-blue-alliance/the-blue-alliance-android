package com.thebluealliance.android.ui.regional

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.domain.NO_VALUE
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.StateContent
import com.thebluealliance.android.ui.common.UiState
import com.thebluealliance.android.ui.components.PaddedClickableHorizontalPadding
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TooltipIconButton
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarYearPicker
import com.thebluealliance.android.ui.components.TopLevelContentWindowInsets
import com.thebluealliance.android.ui.components.bleedHorizontally
import com.thebluealliance.android.ui.components.fitColumnWidths
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.onlyBottom
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.returnsFocus
import com.thebluealliance.android.ui.components.tableSpan
import com.thebluealliance.android.ui.components.tableWidth
import com.thebluealliance.android.ui.components.verticalScrollbar
import com.thebluealliance.android.ui.components.withoutBottom
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.teamNumber
import kotlinx.coroutines.flow.Flow

/** The first season TBA published regional advancement rankings for. */
private const val FIRST_REGIONAL_ADVANCEMENT_YEAR = 2025

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionalAdvancementScreen(
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    onNavigateUp: (() -> Unit)? = null,
    onNavigateToSearch: () -> Unit,
    reselectFlow: Flow<Unit>,
    viewModel: RegionalAdvancementViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val maxYear by viewModel.maxYear.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    // Reset scroll position when year changes (but not on initial composition/back navigation)
    var previousYear by rememberSaveable { mutableIntStateOf(selectedYear) }
    LaunchedEffect(selectedYear) {
        if (selectedYear != previousYear) {
            previousYear = selectedYear
            listState.scrollToItem(0)
        }
    }
    LaunchedEffect(reselectFlow) {
        reselectFlow.collect { listState.animateScrollToItem(0) }
    }
    Scaffold(
        contentWindowInsets = TopLevelContentWindowInsets,
        topBar = {
            TBATopAppBar(
                onRefresh = viewModel::refreshRankings,
                isRefreshing = isRefreshing && uiState !is UiState.Loading,
                title = {
                    TopBarYearPicker(
                        selectedYear = selectedYear,
                        years =
                            if (selectedYear >
                                0
                            ) {
                                (maxYear downTo FIRST_REGIONAL_ADVANCEMENT_YEAR).toList()
                            } else {
                                emptyList()
                            },
                        onYearSelected = viewModel::selectYear,
                        title = { Text("Regional advancement") },
                    )
                },
                navigationIcon = {
                    if (onNavigateUp != null) {
                        TooltipIconButton("Back", TbaIcons.ArrowBack, onNavigateUp)
                    }
                },
                secondaryActions =
                    listOf(
                        TopBarAction("Search", TbaIcons.Search, onNavigateToSearch),
                    ),
            )
        },
    ) { innerPadding ->
        StateContent(
            state = uiState,
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refreshRankings,
            modifier =
                Modifier
                    .pageBackground()
                    .padding(
                        innerPadding.withoutBottom(),
                    ).fillMaxSize(),
            empty = {
                EmptyBox(
                    message = "No regional advancement rankings for $selectedYear",
                    action =
                        if (selectedYear > FIRST_REGIONAL_ADVANCEMENT_YEAR) {
                            {
                                FilledTonalButton(
                                    onClick = { viewModel.selectYear(selectedYear - 1) },
                                    modifier = Modifier.handCursor().heightIn(min = 48.dp),
                                ) {
                                    Text("Show ${selectedYear - 1} rankings")
                                }
                            }
                        } else {
                            null
                        },
                )
            },
        ) { rankings ->
            val contentWidths = regionalColumnContentWidths(rankings)
            val sideScroll = rememberScrollState()
            // On the viewport, not the list: a list wider than the window scrolls sideways, and
            // its end edge would be off screen.
            BoxWithConstraints(
                modifier =
                    Modifier.fillMaxSize().verticalScrollbar(listState, innerPadding.onlyBottom()),
            ) {
                val layout = regionalColumnLayout(maxWidth, contentWidths)
                LazyColumn(
                    state = listState,
                    modifier =
                        Modifier.fillMaxSize().then(
                            // Columns too wide for the window, e.g. at a large font scale, scroll
                            // sideways together instead of breaking their values.
                            layout.rowWidth?.let {
                                Modifier.horizontalScroll(sideScroll).width(it)
                            } ?: Modifier,
                        ),
                    contentPadding = innerPadding.onlyBottom(),
                ) {
                    val tableModifier =
                        layout.rowWidth?.let { Modifier.tableWidth(it) } ?: Modifier.tableWidth()
                    item(key = "header") {
                        Box(modifier = tableModifier) { RankingsTableHeader(layout.columnWidths) }
                    }
                    items(rankings, key = { "${it.year}_${it.teamKey}" }) { ranking ->
                        Box(modifier = tableModifier) {
                            RegionalRankingRow(
                                ranking = ranking,
                                columnWidths = layout.columnWidths,
                                onNavigateToTeam = onNavigateToTeam,
                                onNavigateToEvent = onNavigateToEvent,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Rank, Team, Event 1, Event 2, Bonus, Total Points, Qualified. */
private val COLUMN_WEIGHTS = listOf(0.12f, 0.18f, 0.15f, 0.15f, 0.15f, 0.18f, 0.25f)

/** Horizontal inset of the header and each row. */
private val ROW_PADDING = 16.dp

/** Least space after a column's widest value, so neighbouring values never touch. */
private val COLUMN_GUTTER = 8.dp

/** Horizontal padding inside an advancement badge, on each side. */
private val BADGE_PADDING = 8.dp

/**
 * Column widths, null when the plain weights fit; and the width of a row that needs to scroll
 * sideways, null when the rows fit the window.
 */
internal data class RegionalColumnLayout(
    val columnWidths: List<Dp>?,
    val rowWidth: Dp?,
)

/**
 * Lays the columns out in a [windowWidth]-wide window: weighted while every column's widest value,
 * [contentWidths], fits its weight. Otherwise each column gets at least its content plus a
 * [COLUMN_GUTTER] before the next, and the table scrolls sideways if that no longer fits.
 */
internal fun regionalColumnLayout(
    windowWidth: Dp,
    contentWidths: List<Dp>,
): RegionalColumnLayout {
    val tableWidth = tableSpan(windowWidth).width
    val available = (tableWidth - ROW_PADDING * 2).value
    fitColumnWidths(available, COLUMN_WEIGHTS, contentWidths.map { it.value })
        ?: return RegionalColumnLayout(null, null)
    val minWidths =
        contentWidths.mapIndexed { i, width ->
            if (i < COLUMN_WEIGHTS.lastIndex) width + COLUMN_GUTTER else width
        }
    val columnWidths =
        fitColumnWidths(available, COLUMN_WEIGHTS, minWidths.map { it.value })
            ?: return RegionalColumnLayout(null, null)
    val rowWidth = columnWidths.sum().dp + ROW_PADDING * 2
    return RegionalColumnLayout(
        columnWidths.map { it.dp },
        rowWidth.takeIf { it > tableWidth },
    )
}

/** Narrowest each column can be without breaking a header word or any row's value. */
@Composable
private fun regionalColumnContentWidths(rankings: List<RegionalRanking>): List<Dp> {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val typography = MaterialTheme.typography
    return remember(rankings, typography, density) {
        fun widest(
            texts: List<String>,
            style: TextStyle,
        ): Dp =
            texts.distinct().maxOfOrNull { text ->
                with(density) {
                    measurer
                        .measure(text, style, softWrap = false, maxLines = 1)
                        .size.width
                        .toDp()
                }
            } ?: 0.dp

        fun headerWords(label: String) = widest(label.split(' '), typography.labelSmall)
        val eventTotals =
            rankings.flatMap { r -> r.eventPoints.map { "${it.total.toInt()}" } } + NO_VALUE
        listOf(
            maxOf(
                headerWords("Rank"),
                widest(rankings.map { "${it.rank}" }, typography.labelLarge),
            ),
            maxOf(
                headerWords("Team"),
                widest(rankings.map { it.teamKey.teamNumber }, typography.titleMedium),
            ),
            maxOf(headerWords("Event 1"), widest(eventTotals, typography.bodyMedium)),
            maxOf(headerWords("Event 2"), widest(eventTotals, typography.bodyMedium)),
            maxOf(
                headerWords("Bonus"),
                widest(
                    rankings.map { "+${(it.rookieBonus + it.singleEventBonus).toInt()}" },
                    typography.bodyMedium,
                ),
            ),
            maxOf(
                headerWords("Total points"),
                widest(rankings.map { "${it.pointTotal.toInt()}" }, typography.labelLarge),
            ),
            maxOf(
                headerWords("Qualified"),
                widest(
                    rankings.flatMap { it.advancementMethod.orEmpty().split(' ') },
                    typography.labelSmall,
                ) + BADGE_PADDING * 2,
            ),
        )
    }
}

/** Sizes column [index] of [row]: its weight, or its fitted width from [columnWidths]. */
private fun Modifier.regionalColumn(
    row: RowScope,
    index: Int,
    columnWidths: List<Dp>?,
): Modifier =
    if (columnWidths == null) {
        with(row) { weight(COLUMN_WEIGHTS[index]) }
    } else {
        width(columnWidths[index])
    }

@Composable
private fun RankingsTableHeader(columnWidths: List<Dp>?) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                ).padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Rank",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.regionalColumn(this, 0, columnWidths),
            )
            Text(
                text = "Team",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.regionalColumn(this, 1, columnWidths),
            )
            Text(
                text = "Event 1",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.regionalColumn(this, 2, columnWidths),
            )
            Text(
                text = "Event 2",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.regionalColumn(this, 3, columnWidths),
            )
            Text(
                text = "Bonus",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.regionalColumn(this, 4, columnWidths),
            )
            Text(
                text = "Total points",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.regionalColumn(this, 5, columnWidths),
            )
            Text(
                text = "Qualified",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.regionalColumn(this, 6, columnWidths),
            )
        }
    }
}

@Composable
private fun RegionalRankingRow(
    ranking: RegionalRanking,
    columnWidths: List<Dp>?,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .returnsFocus()
                .handCursor()
                .clickable(onClickLabel = "View team ${ranking.teamKey.teamNumber}") {
                    onNavigateToTeam(ranking.teamKey)
                }.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${ranking.rank}",
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.regionalColumn(this, 0, columnWidths),
            )
            Text(
                text = ranking.teamKey.teamNumber,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.regionalColumn(this, 1, columnWidths),
            )
            val sortedEvents = ranking.eventPoints.sortedBy { it.eventKey }
            if (sortedEvents.isNotEmpty()) {
                EventScoreCell(
                    score = sortedEvents[0].total.toInt(),
                    onClick = { onNavigateToEvent(sortedEvents[0].eventKey) },
                    modifier = Modifier.regionalColumn(this, 2, columnWidths),
                )
            } else {
                Text(
                    text = NO_VALUE,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.regionalColumn(this, 2, columnWidths),
                )
            }
            if (sortedEvents.size > 1) {
                EventScoreCell(
                    score = sortedEvents[1].total.toInt(),
                    onClick = { onNavigateToEvent(sortedEvents[1].eventKey) },
                    modifier = Modifier.regionalColumn(this, 3, columnWidths),
                )
            } else {
                Text(
                    text = NO_VALUE,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.regionalColumn(this, 3, columnWidths),
                )
            }
            val totalBonus = (ranking.rookieBonus + ranking.singleEventBonus).toInt()
            if (totalBonus > 0) {
                Text(
                    text = "+$totalBonus",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.regionalColumn(this, 4, columnWidths),
                )
            } else {
                Text(
                    text = NO_VALUE,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    softWrap = false,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.regionalColumn(this, 4, columnWidths),
                )
            }
            Text(
                text = "${ranking.pointTotal.toInt()}",
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.regionalColumn(this, 5, columnWidths),
            )
            AdvancementBadge(
                method = ranking.advancementMethod,
                modifier = Modifier.regionalColumn(this, 6, columnWidths),
            )
        }
    }
}

/**
 * An event's score that opens the event. The highlight hugs the score and bleeds past its start
 * without moving it; the tap target runs from that bleed to the cell's end, so it is wide enough
 * that touch never stretches it further into the previous column.
 */
@Composable
private fun EventScoreCell(
    score: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .bleedHorizontally(start = PaddedClickableHorizontalPadding, end = 0.dp)
                .returnsFocus()
                .handCursor()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClickLabel = "View event",
                    role = Role.Button,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = "$score",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            softWrap = false,
            modifier =
                Modifier
                    .clip(MaterialTheme.shapes.small)
                    .indication(interactionSource, ripple())
                    .padding(horizontal = PaddedClickableHorizontalPadding, vertical = 6.dp),
        )
    }
}

@Composable
private fun AdvancementBadge(
    method: String?,
    modifier: Modifier = Modifier,
) {
    if (method.isNullOrBlank()) {
        Text(
            text = NO_VALUE,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier,
        )
    } else {
        Box(
            modifier =
                modifier
                    .background(
                        color =
                            when {
                                method.contains(
                                    "Regional Winner",
                                ) -> MaterialTheme.colorScheme.primaryContainer
                                method.contains(
                                    "Finalist",
                                ) -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.tertiaryContainer
                            },
                        shape = MaterialTheme.shapes.extraSmall,
                    ).padding(horizontal = BADGE_PADDING, vertical = 4.dp),
        ) {
            Text(
                text = method,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color =
                    when {
                        method.contains(
                            "Regional Winner",
                        ) -> MaterialTheme.colorScheme.onPrimaryContainer
                        method.contains(
                            "Finalist",
                        ) -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                    },
            )
        }
    }
}
