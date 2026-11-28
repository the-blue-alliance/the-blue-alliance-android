package com.thebluealliance.android.ui.regional

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thebluealliance.android.R
import com.thebluealliance.android.domain.NO_VALUE
import com.thebluealliance.android.domain.model.RegionalEventPoints
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.QuantityStrings
import com.thebluealliance.android.ui.common.StateContent
import com.thebluealliance.android.ui.common.UiState
import com.thebluealliance.android.ui.common.rememberQuantityStrings
import com.thebluealliance.android.ui.components.TBATopAppBar
import com.thebluealliance.android.ui.components.TableCellHighlightPadding
import com.thebluealliance.android.ui.components.TableCellPadding
import com.thebluealliance.android.ui.components.TableColumnGutter
import com.thebluealliance.android.ui.components.TableHeaderBand
import com.thebluealliance.android.ui.components.TableHeaderFocus
import com.thebluealliance.android.ui.components.TableHeaderLabel
import com.thebluealliance.android.ui.components.TableRankCell
import com.thebluealliance.android.ui.components.TableRow
import com.thebluealliance.android.ui.components.TableRowDivider
import com.thebluealliance.android.ui.components.TableScope
import com.thebluealliance.android.ui.components.TableValueCell
import com.thebluealliance.android.ui.components.TbaTable
import com.thebluealliance.android.ui.components.TooltipIconButton
import com.thebluealliance.android.ui.components.TopBarAction
import com.thebluealliance.android.ui.components.TopBarYearPicker
import com.thebluealliance.android.ui.components.TopLevelContentWindowInsets
import com.thebluealliance.android.ui.components.bleedHorizontally
import com.thebluealliance.android.ui.components.handCursor
import com.thebluealliance.android.ui.components.onlyBottom
import com.thebluealliance.android.ui.components.pageBackground
import com.thebluealliance.android.ui.components.rememberTableTextWidths
import com.thebluealliance.android.ui.components.returnsFocus
import com.thebluealliance.android.ui.components.tableRankLabel
import com.thebluealliance.android.ui.components.tableValue
import com.thebluealliance.android.ui.components.tableWidth
import com.thebluealliance.android.ui.components.viewTeamClickLabel
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
                        title = { Text("Regional Advancement") },
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
            val minWidths = regionalColumnMinWidths(rankings)
            val strings = rememberQuantityStrings()
            TbaTable(
                weights = COLUMN_WEIGHTS,
                minWidths = minWidths,
                frozenColumns = FROZEN_COLUMNS,
                listState = listState,
                contentPadding = innerPadding.onlyBottom(),
                headerFocus = TableHeaderFocus.None,
                header = { RankingsTableHeader() },
            ) { table ->
                items(rankings, key = { "${it.year}_${it.teamKey}" }) { ranking ->
                    table.RegionalRankingRow(
                        ranking = ranking,
                        onNavigateToTeam = onNavigateToTeam,
                        onNavigateToEvent = onNavigateToEvent,
                        strings = strings,
                    )
                }
            }
        }
    }
}

/** Rank, Team, Event 1, Event 2, Bonus, Total Points, Qualified. */
private val COLUMN_WEIGHTS = listOf(0.12f, 0.18f, 0.15f, 0.15f, 0.15f, 0.18f, 0.25f)

/** Rank and Team, which stay put while a row's other columns scroll sideways. */
private const val FROZEN_COLUMNS = 2

/** Horizontal padding inside an advancement badge, on each side. */
private val BADGE_PADDING = 8.dp

private val HEADER_LABELS =
    listOf("Rank", "Team", "Event 1", "Event 2", "Bonus", "Total Points", "Qualified")

/** A ranking's event totals in event-key order, as the Event 1 and Event 2 columns show them. */
private fun eventTotals(ranking: RegionalRanking): List<RegionalEventPoints> =
    ranking.eventPoints.sortedBy { it.eventKey }

private fun bonusLabel(ranking: RegionalRanking): String? =
    (ranking.rookieBonus + ranking.singleEventBonus).toInt().takeIf { it > 0 }?.let { "+$it" }

/** One spoken summary of a regional advancement row; a null value is an empty cell. */
internal fun regionalRowDescription(
    rank: Int,
    teamNumber: String,
    event1: Int?,
    event2: Int?,
    bonus: Int,
    total: Int,
    qualified: String?,
    strings: QuantityStrings,
): String =
    buildList {
        add("Rank $rank")
        add("Team $teamNumber")
        add(event1?.let { "Event 1: ${strings.get(R.plurals.points, it)}" } ?: "Event 1 none")
        add(event2?.let { "Event 2: ${strings.get(R.plurals.points, it)}" } ?: "Event 2 none")
        add(if (bonus > 0) "bonus $bonus" else "bonus none")
        add(strings.get(R.plurals.total_points, total))
        add(qualified?.takeIf { it.isNotBlank() }?.let { "qualified: $it" } ?: "not qualified")
    }.joinToString(", ")

/**
 * Narrowest each column can be without breaking a header word or any row's value, at the current
 * font scale.
 */
@Composable
private fun regionalColumnMinWidths(rankings: List<RegionalRanking>): List<Dp> {
    val widths = rememberTableTextWidths()
    val typography = MaterialTheme.typography
    return remember(rankings, typography, widths) {
        val eventTotals =
            rankings.flatMap { r -> r.eventPoints.map { "${it.total.toInt()}" } } + NO_VALUE
        val values =
            listOf(
                widths.widestValue(rankings.map { tableRankLabel(it.rank) }, typography.labelLarge),
                widths.widestValue(rankings.map { it.teamKey.teamNumber }, typography.titleMedium),
                widths.widestValue(eventTotals, typography.bodyMedium),
                widths.widestValue(eventTotals, typography.bodyMedium),
                widths.widestValue(
                    rankings.map { tableValue(bonusLabel(it)) },
                    typography.bodyMedium,
                ),
                widths.widestValue(
                    rankings.map { "${it.pointTotal.toInt()}" },
                    typography.labelLarge,
                ),
                widths.widestValue(
                    rankings.flatMap { it.advancementMethod.orEmpty().split(' ') },
                    typography.labelSmall,
                ) + BADGE_PADDING * 2,
            )
        values.mapIndexed { i, value ->
            maxOf(widths.widestWord(HEADER_LABELS[i], typography.titleSmall), value) +
                TableColumnGutter * 2
        }
    }
}

@Composable
private fun TableScope.RankingsTableHeader() {
    @Composable
    fun RowScope.label(index: Int) {
        TableHeaderLabel(
            text = HEADER_LABELS[index],
            modifier = Modifier.tableColumn(this, index).padding(horizontal = TableColumnGutter),
        )
    }
    TableHeaderBand(
        verticalPadding = 12.dp,
        frozen = { (0 until FROZEN_COLUMNS).forEach { label(it) } },
    ) {
        (FROZEN_COLUMNS until HEADER_LABELS.size).forEach { label(it) }
    }
}

@Composable
private fun TableScope.RegionalRankingRow(
    ranking: RegionalRanking,
    onNavigateToTeam: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit,
    strings: QuantityStrings,
) {
    val events = eventTotals(ranking)
    val description =
        regionalRowDescription(
            rank = ranking.rank,
            teamNumber = ranking.teamKey.teamNumber,
            event1 = events.getOrNull(0)?.total?.toInt(),
            event2 = events.getOrNull(1)?.total?.toInt(),
            bonus = (ranking.rookieBonus + ranking.singleEventBonus).toInt(),
            total = ranking.pointTotal.toInt(),
            qualified = ranking.advancementMethod,
            strings = strings,
        )
    Column(modifier = Modifier.tableWidth(tableWidth)) {
        TableRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    // A row of plain cells is shorter than one with event links; keep a 48dp target.
                    .heightIn(min = 48.dp)
                    .returnsFocus()
                    .handCursor()
                    .clickable(onClickLabel = viewTeamClickLabel(ranking.teamKey)) {
                        onNavigateToTeam(ranking.teamKey)
                    }
                    // The cells clear their own semantics, so the row reads as one stop with
                    // column names; the event scores stay separate targets.
                    .semantics { contentDescription = description },
            frozen = {
                TableRankCell(ranking.rank, Modifier.tableColumn(this, 0))
                TableValueCell(
                    value = ranking.teamKey.teamNumber,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.tableColumn(this, 1),
                )
            },
        ) {
            for (i in 0..1) {
                val event = events.getOrNull(i)
                if (event != null) {
                    EventScoreCell(
                        score = event.total.toInt(),
                        onClick = { onNavigateToEvent(event.eventKey) },
                        // The sideways-scrolling part clips at its start edge.
                        atScrollEdge = i == 0 && rowScroll != null,
                        modifier =
                            Modifier
                                .tableColumn(this, 2 + i)
                                .padding(end = TableColumnGutter),
                    )
                } else {
                    TableValueCell(
                        value = null,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.tableColumn(this, 2 + i),
                    )
                }
            }
            TableValueCell(
                value = bonusLabel(ranking),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.tableColumn(this, 4),
            )
            TableValueCell(
                value = "${ranking.pointTotal.toInt()}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.tableColumn(this, 5),
            )
            val method = ranking.advancementMethod
            if (method.isNullOrBlank()) {
                TableValueCell(
                    value = null,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.tableColumn(this, 6),
                )
            } else {
                AdvancementBadge(
                    method = method,
                    modifier =
                        Modifier
                            .tableColumn(this, 6)
                            .padding(horizontal = TableColumnGutter, vertical = TableCellPadding)
                            .clearAndSetSemantics {},
                )
            }
        }
        TableRowDivider()
    }
}

/**
 * An event's score that opens the event, in line with the column's other values. The highlight
 * bleeds past the cell's start so the score keeps [TableCellHighlightPadding] of room, except
 * [atScrollEdge], where the edge would clip it and it keeps the cell's gutter instead. The tap
 * target runs from that bleed to the cell's end.
 */
@Composable
private fun EventScoreCell(
    score: Int,
    onClick: () -> Unit,
    atScrollEdge: Boolean,
    modifier: Modifier = Modifier,
) {
    val bleed = if (atScrollEdge) 0.dp else TableCellHighlightPadding - TableColumnGutter
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .bleedHorizontally(start = bleed, end = 0.dp)
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
                    .padding(
                        start = TableColumnGutter + bleed,
                        end = TableCellHighlightPadding,
                        top = 6.dp,
                        bottom = 6.dp,
                    ),
        )
    }
}

@Composable
private fun AdvancementBadge(
    method: String,
    modifier: Modifier = Modifier,
) {
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
