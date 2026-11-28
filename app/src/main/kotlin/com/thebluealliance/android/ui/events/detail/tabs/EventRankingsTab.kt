package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.NO_VALUE
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.common.rememberQuantityStrings
import com.thebluealliance.android.ui.components.SortableHeaderCell
import com.thebluealliance.android.ui.components.TABULAR_FIGURES
import com.thebluealliance.android.ui.components.TableCellPadding
import com.thebluealliance.android.ui.components.TableColumnGutter
import com.thebluealliance.android.ui.components.TableHeaderBand
import com.thebluealliance.android.ui.components.TableHeaderLabel
import com.thebluealliance.android.ui.components.TableIconWidth
import com.thebluealliance.android.ui.components.TableRankCell
import com.thebluealliance.android.ui.components.TableRow
import com.thebluealliance.android.ui.components.TableRowDivider
import com.thebluealliance.android.ui.components.TableScope
import com.thebluealliance.android.ui.components.TableTeamCell
import com.thebluealliance.android.ui.components.TableValueCell
import com.thebluealliance.android.ui.components.TbaTable
import com.thebluealliance.android.ui.components.expandableRow
import com.thebluealliance.android.ui.components.rankingRowDescription
import com.thebluealliance.android.ui.components.recordDescription
import com.thebluealliance.android.ui.components.rememberTableTextWidths
import com.thebluealliance.android.ui.components.statRowWidth
import com.thebluealliance.android.ui.components.tableColumnLayout
import com.thebluealliance.android.ui.components.tableRankLabel
import com.thebluealliance.android.ui.components.tableWidth
import com.thebluealliance.android.ui.theme.TBAMotionTokens
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.teamNumber
import java.util.Locale

/** Rank, Team, Record, primary sort, secondary sort, chevron. */
private val COLUMN_WEIGHTS = listOf(0.11f, 0.22f, 0.15f, 0.23f, 0.19f, 0.10f)

private const val TEAM_COLUMN = 1

/** Rank and Team, which stay put while a row's other columns scroll sideways. */
private const val FROZEN_COLUMNS = 2

private const val PRIMARY_COLUMN = 3
private const val SECONDARY_COLUMN = 4

/** Widest a 5-digit team number, in the team column's style, is likely to be. */
private const val WIDEST_TEAM_NUMBER = "88888"

enum class RankingSortColumn {
    TEAM,
    PRIMARY,
    SECONDARY,
}

internal data class RankingSortState(
    val column: RankingSortColumn = RankingSortColumn.PRIMARY,
    val ascending: Boolean = false,
)

private val RankingSortStateSaver =
    listSaver<RankingSortState, Any>(
        save = { listOf(it.column.name, it.ascending) },
        restore = {
            RankingSortState(
                column = RankingSortColumn.valueOf(it[0] as String),
                ascending = it[1] as Boolean,
            )
        },
    )

internal fun rankingHeaderLabels(rankingSortOrders: List<RankingSortOrder>?): Pair<String, String> {
    val primaryLabel = rankingSortOrders?.getOrNull(0)?.name?.takeIf { it.isNotBlank() } ?: "RS"
    val secondaryLabel =
        rankingSortOrders?.getOrNull(1)?.name?.takeIf { it.isNotBlank() } ?: "Sort 2"
    return primaryLabel to secondaryLabel
}

internal fun nextRankingSortState(
    current: RankingSortState,
    selectedColumn: RankingSortColumn,
): RankingSortState {
    if (current.column == selectedColumn) {
        return current.copy(ascending = !current.ascending)
    }
    val ascending =
        when (selectedColumn) {
            RankingSortColumn.TEAM -> true
            RankingSortColumn.PRIMARY, RankingSortColumn.SECONDARY -> false
        }
    return RankingSortState(column = selectedColumn, ascending = ascending)
}

internal fun sortRankings(
    rankings: List<Ranking>,
    sortState: RankingSortState,
): List<Ranking> =
    rankings.sortedWith { a, b ->
        val result =
            when (sortState.column) {
                RankingSortColumn.TEAM -> {
                    val teamA = a.teamKey.teamNumber.toIntOrNull() ?: 0
                    val teamB = b.teamKey.teamNumber.toIntOrNull() ?: 0
                    teamA.compareTo(teamB)
                }
                RankingSortColumn.PRIMARY -> {
                    val valA = a.sortOrders.getOrNull(0) ?: 0.0
                    val valB = b.sortOrders.getOrNull(0) ?: 0.0
                    valA.compareTo(valB)
                }
                RankingSortColumn.SECONDARY -> {
                    val valA = a.sortOrders.getOrNull(1) ?: 0.0
                    val valB = b.sortOrders.getOrNull(1) ?: 0.0
                    valA.compareTo(valB)
                }
            }
        if (sortState.ascending) result else -result
    }

@Composable
fun EventRankingsTab(
    rankings: List<Ranking>?,
    rankingSortOrders: List<RankingSortOrder>?,
    rankingExtraStatsInfo: List<RankingSortOrder>?,
    onTeamClick: (String) -> Unit = {},
    isRefreshing: Boolean = false,
    innerPadding: PaddingValues = PaddingValues.Zero,
) {
    if (rankings == null) {
        LoadingBox(
            modifier = Modifier.padding(innerPadding),
        )
        return
    }
    if (rankings.isEmpty()) {
        // An empty list while the first fetch is still in flight is loading, not empty.
        if (isRefreshing) {
            LoadingBox(modifier = Modifier.padding(innerPadding))
        } else {
            EmptyBox(modifier = Modifier.padding(innerPadding), message = "No rankings")
        }
        return
    }

    val (primaryLabel, secondaryLabel) = rankingHeaderLabels(rankingSortOrders)

    var sortState by rememberSaveable(stateSaver = RankingSortStateSaver) {
        mutableStateOf(RankingSortState())
    }

    val sortedRankings =
        remember(rankings, sortState) {
            sortRankings(rankings, sortState)
        }

    // Narrow windows widen the columns whose header words would otherwise break, and scroll
    // sideways if even that doesn't fit.
    val minWidths =
        rankingColumnMinWidths(
            primaryLabel,
            secondaryLabel,
            sortState.column,
            rankings,
            rankingSortOrders,
        )
    TbaTable(
        weights = COLUMN_WEIGHTS,
        minWidths = minWidths,
        frozenColumns = FROZEN_COLUMNS,
        contentPadding = innerPadding,
        header = {
            RankingHeaderRow(
                primaryLabel = primaryLabel,
                secondaryLabel = secondaryLabel,
                sortState = sortState,
                onSortSelected = { selectedColumn ->
                    sortState = nextRankingSortState(sortState, selectedColumn)
                },
            )
        },
    ) { table ->
        items(sortedRankings, key = { "${it.eventKey}_${it.teamKey}" }) { ranking ->
            table.RankingItem(
                ranking = ranking,
                sortOrders = rankingSortOrders,
                extraStatsInfo = rankingExtraStatsInfo,
                onTeamClick = onTeamClick,
            )
        }
    }
}

/** The rankings table's layout in a [windowWidth]-wide window (see [tableColumnLayout]). */
internal fun rankingColumnLayout(
    windowWidth: Dp,
    minWidths: List<Dp>,
) = tableColumnLayout(windowWidth, COLUMN_WEIGHTS, minWidths, FROZEN_COLUMNS)

/**
 * Narrowest each column can be without breaking a header word, the team number or any row's value,
 * at the current font scale.
 */
@Composable
private fun rankingColumnMinWidths(
    primaryLabel: String,
    secondaryLabel: String,
    sortColumn: RankingSortColumn,
    rankings: List<Ranking>,
    sortOrders: List<RankingSortOrder>?,
): List<Dp> {
    val widths = rememberTableTextWidths()
    val headerStyle = MaterialTheme.typography.titleSmall
    val teamStyle = MaterialTheme.typography.titleMedium
    val rankStyle = MaterialTheme.typography.labelLarge
    val recordStyle = MaterialTheme.typography.bodyMedium
    val valueStyle = rankStyle.copy(fontFeatureSettings = TABULAR_FIGURES)
    return remember(
        primaryLabel,
        secondaryLabel,
        sortColumn,
        rankings,
        sortOrders,
        headerStyle,
        teamStyle,
        rankStyle,
        recordStyle,
        widths,
    ) {
        val gutters = TableColumnGutter * 2

        fun caret(column: RankingSortColumn) = if (sortColumn == column) TableIconWidth else 0.dp

        fun sortValues(index: Int) =
            rankings.map {
                sortOrderValue(it, index, sortOrders)
                    ?: NO_VALUE
            }
        listOf(
            maxOf(
                widths.widestWord("Rank", headerStyle),
                widths.widestValue(rankings.map { tableRankLabel(it.rank) }, rankStyle),
            ) + gutters,
            maxOf(
                widths.widestWord("Team", headerStyle) + caret(RankingSortColumn.TEAM),
                widths.widestWord(WIDEST_TEAM_NUMBER, teamStyle),
            ) + gutters,
            maxOf(
                widths.widestWord("Record", headerStyle),
                widths.widestValue(rankings.map { recordLabel(it) }, recordStyle),
            ) + gutters,
            maxOf(
                widths.widestWord(primaryLabel, headerStyle) + caret(RankingSortColumn.PRIMARY),
                widths.widestValue(sortValues(0), valueStyle),
            ) + gutters,
            maxOf(
                widths.widestWord(secondaryLabel, headerStyle) +
                    caret(RankingSortColumn.SECONDARY),
                widths.widestValue(sortValues(1), valueStyle),
            ) + gutters,
            TableIconWidth + gutters,
        )
    }
}

private fun recordLabel(ranking: Ranking): String =
    "${ranking.wins}-${ranking.losses}-${ranking.ties}"

/** Sort order [index] of [ranking], formatted to its declared precision, or null if missing. */
private fun sortOrderValue(
    ranking: Ranking,
    index: Int,
    sortOrders: List<RankingSortOrder>?,
): String? =
    ranking.sortOrders.getOrNull(index)?.let { value ->
        val precision = sortOrders?.getOrNull(index)?.precision ?: 2
        String.format(Locale.US, "%.${precision}f", value)
    }

@Composable
private fun TableScope.RankingHeaderRow(
    primaryLabel: String,
    secondaryLabel: String,
    sortState: RankingSortState,
    onSortSelected: (RankingSortColumn) -> Unit,
) {
    // 6.dp here + 6.dp inside each sortable cell below: the sort targets get a full-height hover
    // band without changing the header's height.
    TableHeaderBand(
        verticalPadding = 6.dp,
        frozen = {
            TableHeaderLabel(
                text = "Rank",
                modifier =
                    Modifier
                        .tableColumn(this, 0)
                        .padding(horizontal = TableColumnGutter),
            )
            SortableHeaderCell(
                text = "Team",
                sorted = sortState.column == RankingSortColumn.TEAM,
                ascending = sortState.ascending,
                onClick = { onSortSelected(RankingSortColumn.TEAM) },
                modifier = Modifier.tableColumn(this, TEAM_COLUMN),
            )
        },
    ) {
        TableHeaderLabel(
            text = "Record",
            modifier =
                Modifier
                    .tableColumn(this, 2)
                    .padding(horizontal = TableColumnGutter),
        )
        SortableHeaderCell(
            text = primaryLabel,
            sorted = sortState.column == RankingSortColumn.PRIMARY,
            ascending = sortState.ascending,
            onClick = { onSortSelected(RankingSortColumn.PRIMARY) },
            modifier = Modifier.tableColumn(this, PRIMARY_COLUMN),
            numeric = true,
        )
        SortableHeaderCell(
            text = secondaryLabel,
            sorted = sortState.column == RankingSortColumn.SECONDARY,
            ascending = sortState.ascending,
            onClick = { onSortSelected(RankingSortColumn.SECONDARY) },
            modifier = Modifier.tableColumn(this, SECONDARY_COLUMN),
            numeric = true,
        )
        // Spacer for chevron alignment
        Spacer(modifier = Modifier.tableColumn(this, 5))
    }
}

@Composable
private fun TableScope.RankingItem(
    ranking: Ranking,
    sortOrders: List<RankingSortOrder>?,
    extraStatsInfo: List<RankingSortOrder>?,
    onTeamClick: (String) -> Unit,
) {
    var expanded by rememberSaveable(ranking.teamKey) { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = TBAMotionTokens.fastSpatialSpec(),
        label = "chevron_rotation",
    )

    val strings = rememberQuantityStrings()
    // The first two sort order values, without labels (the header has them).
    val (primaryValue, secondaryValue) = (0..1).map { i -> sortOrderValue(ranking, i, sortOrders) }
    val rowDescription =
        rankingRowDescription(
            rank = ranking.rank,
            teamNumber = ranking.teamKey.teamNumber,
            record = recordDescription(ranking.wins, ranking.losses, ranking.ties, strings),
            sortLabels = rankingHeaderLabels(sortOrders).toList(),
            sortValues = listOf(primaryValue, secondaryValue),
        )

    Column(
        modifier = Modifier.tableWidth(tableWidth),
    ) {
        // Vertical padding lives in the cells: TableCellPadding on each plain cell and 4.dp more
        // inside the team cell, so the row keeps its height while the team target spans 48dp.
        TableRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    // The cells below clear their own semantics, so the row reads as one stop with
                    // column names; the team cell stays a separate target.
                    .semantics { contentDescription = rowDescription }
                    .expandableRow(expanded) { expanded = !expanded },
            frozen = {
                TableRankCell(ranking.rank, Modifier.tableColumn(this, 0))
                TableTeamCell(
                    teamKey = ranking.teamKey,
                    onClick = onTeamClick,
                    modifier = Modifier.tableColumn(this, TEAM_COLUMN),
                )
            },
        ) {
            TableValueCell(
                value = recordLabel(ranking),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.tableColumn(this, 2),
            )
            TableValueCell(
                value = primaryValue,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.tableColumn(this, PRIMARY_COLUMN),
                numeric = true,
            )
            TableValueCell(
                value = secondaryValue,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.tableColumn(this, SECONDARY_COLUMN),
                numeric = true,
            )

            Icon(
                imageVector = TbaIcons.KeyboardArrowDown,
                // The row's stateDescription and click label already say this.
                contentDescription = null,
                modifier =
                    Modifier
                        .rotate(rotationAngle)
                        .tableColumn(this, 5)
                        .padding(horizontal = TableColumnGutter, vertical = TableCellPadding),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter =
                expandVertically(
                    animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                ) + fadeIn(TBAMotionTokens.defaultEffectsSpec()),
            exit =
                shrinkVertically(
                    animationSpec = TBAMotionTokens.defaultSpatialSpec(),
                ) + fadeOut(TBAMotionTokens.fastEffectsSpec()),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .padding(start = 8.dp),
            ) {
                // Show all other sort orders (tiebreakers)
                if (sortOrders != null && ranking.sortOrders.size > 2) {
                    Text(
                        text = "Tiebreakers",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp).semantics { heading() },
                    )

                    for (i in 2 until minOf(ranking.sortOrders.size, sortOrders.size)) {
                        val sortOrder = sortOrders[i]
                        val value = ranking.sortOrders[i]
                        // null = a missing component kept in place to preserve column alignment.
                        val formattedValue =
                            value?.let {
                                String.format(Locale.US, "%.${sortOrder.precision}f", it)
                            } ?: NO_VALUE

                        Row(
                            modifier =
                                Modifier
                                    .statRowWidth()
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .semantics(mergeDescendants = true) {},
                        ) {
                            Text(
                                // Blank when a null sort_order_info element was coalesced to a
                                // placeholder; fall back like the header/extra-stats rows.
                                text = sortOrder.name.ifBlank { "Sort ${i + 1}" },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formattedValue,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }

                // Show extra stats if available
                if (ranking.extraStats.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.statRowWidth().padding(vertical = 4.dp),
                    )
                    Text(
                        text = "Additional stats",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp).semantics { heading() },
                    )
                    ranking.extraStats.forEachIndexed { index, value ->
                        val info = extraStatsInfo?.getOrNull(index)
                        val statName = info?.name ?: "Stat ${index + 1}"
                        val precision = info?.precision ?: 2
                        Row(
                            modifier =
                                Modifier
                                    .statRowWidth()
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .semantics(mergeDescendants = true) {},
                        ) {
                            Text(
                                text = statName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text =
                                    value?.let { String.format(Locale.US, "%.${precision}f", it) }
                                        ?: NO_VALUE,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            }
        }

        TableRowDivider()
    }
}
