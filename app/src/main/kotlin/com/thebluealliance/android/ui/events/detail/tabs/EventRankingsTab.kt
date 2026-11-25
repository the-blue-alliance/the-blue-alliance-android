package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.expandableRow
import com.thebluealliance.android.ui.components.sortableHeader
import com.thebluealliance.android.ui.components.tableWidth
import com.thebluealliance.android.ui.theme.BrandHeaderContainer
import com.thebluealliance.android.ui.theme.OnBrand
import com.thebluealliance.android.ui.theme.TBAMotionTokens
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.teamNumber
import java.util.Locale

/**
 * Half of the gutter between two table columns, applied inside every cell of both the header and
 * the data rows so the columns stay aligned. The rows carry [TABLE_ROW_PADDING] instead of the
 * usual 16.dp so the outermost cells' content still lands on the screen's 16.dp margin.
 */
private val COLUMN_GUTTER = 4.dp

/** Row inset that, plus [COLUMN_GUTTER] inside the end cells, restores the 16.dp screen margin. */
private val TABLE_ROW_PADDING = 12.dp

/** Vertical inset of each ranking-row cell; the team cell adds 4.dp more for a 48dp target. */
private val ROW_CELL_PADDING = 8.dp

/** OpenType tabular figures: every digit takes the same advance, so numeric columns grid up. */
private const val TABULAR_FIGURES = "tnum"

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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
    ) {
        stickyHeader {
            RankingHeaderRow(
                primaryLabel = primaryLabel,
                secondaryLabel = secondaryLabel,
                sortState = sortState,
                onSortSelected = { selectedColumn ->
                    sortState = nextRankingSortState(sortState, selectedColumn)
                },
            )
        }
        items(sortedRankings, key = { "${it.eventKey}_${it.teamKey}" }) { ranking ->
            RankingItem(
                ranking = ranking,
                sortOrders = rankingSortOrders,
                extraStatsInfo = rankingExtraStatsInfo,
                onTeamClick = onTeamClick,
            )
        }
    }
}

@Composable
private fun RankingHeaderRow(
    primaryLabel: String,
    secondaryLabel: String,
    sortState: RankingSortState,
    onSortSelected: (RankingSortColumn) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .tableWidth()
                .background(BrandHeaderContainer)
                // 6.dp vertical here + 6.dp inside each sortable cell below: the sort targets get
                // a full-height hover band without changing the header's height. The horizontal
                // inset is the screen margin minus the cells' own COLUMN_GUTTER.
                .padding(horizontal = TABLE_ROW_PADDING, vertical = 6.dp)
                // Sortable cells fill this height, so a wrapped label doesn't leave its
                // neighbours with shorter hover bands.
                .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Rank",
            style = MaterialTheme.typography.titleSmall,
            color = OnBrand,
            modifier = Modifier.weight(0.11f).padding(horizontal = COLUMN_GUTTER),
        )
        RankingHeaderItem(
            text = "Team",
            modifier = Modifier.weight(0.22f),
            sortColumn = RankingSortColumn.TEAM,
            currentSort = sortState.column,
            ascending = sortState.ascending,
            onSortClick = { onSortSelected(RankingSortColumn.TEAM) },
        )
        Text(
            text = "Record",
            style = MaterialTheme.typography.titleSmall,
            color = OnBrand,
            modifier = Modifier.weight(0.15f).padding(horizontal = COLUMN_GUTTER),
        )
        RankingHeaderItem(
            text = primaryLabel,
            modifier = Modifier.weight(0.23f),
            numeric = true,
            sortColumn = RankingSortColumn.PRIMARY,
            currentSort = sortState.column,
            ascending = sortState.ascending,
            onSortClick = { onSortSelected(RankingSortColumn.PRIMARY) },
        )
        RankingHeaderItem(
            text = secondaryLabel,
            modifier = Modifier.weight(0.19f),
            numeric = true,
            sortColumn = RankingSortColumn.SECONDARY,
            currentSort = sortState.column,
            ascending = sortState.ascending,
            onSortClick = { onSortSelected(RankingSortColumn.SECONDARY) },
        )
        // Spacer for chevron alignment
        Spacer(modifier = Modifier.weight(0.10f))
    }
}

@Composable
private fun RankingHeaderItem(
    text: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = false,
    sortColumn: RankingSortColumn,
    currentSort: RankingSortColumn,
    ascending: Boolean,
    onSortClick: () -> Unit,
) {
    val sorted = currentSort == sortColumn
    // Clip + clickable outside the cell's own COLUMN_GUTTER, so the sort target is a rounded band
    // spanning the whole column with the label inset from both its edges, instead of a highlight
    // glued to the glyphs. The data cells carry the same gutter, so the label stays in line with
    // the column below it.
    val cellModifier =
        modifier
            .fillMaxHeight()
            .sortableHeader(MaterialTheme.shapes.small, text, sorted, ascending, onSortClick)
            .padding(horizontal = COLUMN_GUTTER, vertical = 6.dp)
    if (numeric) {
        // Numeric column: right-align the label onto the values' shared right edge, with the sort
        // carat leading it (Material data-table convention — a leading carat keeps the numeric
        // header's right edge in line with the digits below). A hand layout, because this column
        // is narrow and its label ("Ranking Score") wraps to two lines: the label (child 0) is
        // measured at the cell's full width so it never ellipsizes, then right-anchored; the carat
        // (child 1) is placed just left of the label, spilling into the column's left slack rather
        // than stealing width from the label.
        Layout(
            modifier = cellModifier,
            content = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnBrand,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
                if (sorted) {
                    SortCaret(ascending)
                }
            },
            measurePolicy = NumericHeaderMeasurePolicy,
        )
    } else {
        Row(
            modifier = cellModifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = OnBrand,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (sorted) {
                SortCaret(ascending)
            }
        }
    }
}

/**
 * Lays out a numeric header cell: the label (child 0) right-anchored, the sort carat (optional
 * child 1) just left of it. Intrinsic height mirrors measure, so an `IntrinsicSize.Min` header row
 * gets the label's real wrapped height.
 */
private object NumericHeaderMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        // Reserve the carat's width up front, then measure the label in what's left, so a
        // right-aligned (full-width) two-line label can't swallow the whole column and clip
        // the carat off the cell's leading edge.
        val caret = measurables.getOrNull(1)?.measure(loose)
        val caretWidth = caret?.width ?: 0
        val width = constraints.maxWidth
        val labelMax = (width - caretWidth).coerceAtLeast(0)
        val label = measurables[0].measure(loose.copy(maxWidth = labelMax))
        val height = maxOf(label.height, caret?.height ?: 0, constraints.minHeight)
        // Right-anchor the label on the cell's right edge (the values' shared edge), then seat
        // the carat directly to its left.
        val labelLeft = (width - label.width).coerceAtLeast(caretWidth)
        return layout(width, height) {
            label.place(labelLeft, (height - label.height) / 2)
            caret?.place((labelLeft - caretWidth).coerceAtLeast(0), (height - caret.height) / 2)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ): Int = intrinsicHeight(measurables, width)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ): Int = intrinsicHeight(measurables, width)

    private fun intrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ): Int {
        val caret = measurables.getOrNull(1)
        val caretWidth = caret?.maxIntrinsicWidth(Constraints.Infinity) ?: 0
        val labelHeight = measurables[0].minIntrinsicHeight((width - caretWidth).coerceAtLeast(0))
        return maxOf(labelHeight, caret?.minIntrinsicHeight(caretWidth) ?: 0)
    }
}

@Composable
private fun SortCaret(ascending: Boolean) {
    Icon(
        imageVector =
            if (ascending) TbaIcons.ArrowDropUp else TbaIcons.ArrowDropDown,
        // The header cell's stateDescription announces the sort.
        contentDescription = null,
        tint = OnBrand,
    )
}

@Composable
private fun RankingItem(
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

    Column(
        modifier = Modifier.tableWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .expandableRow(expanded) { expanded = !expanded }
                    // Vertical padding lives in the cells: ROW_CELL_PADDING on each plain cell and
                    // ROW_CELL_PADDING + 4.dp inside the team cell, so the row keeps its height
                    // while the nested team target spans 48dp. The horizontal inset is the screen
                    // margin minus the cells' own COLUMN_GUTTER.
                    .padding(horizontal = TABLE_ROW_PADDING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "#${ranking.rank}",
                style = MaterialTheme.typography.labelLarge,
                modifier =
                    Modifier
                        .weight(0.11f)
                        .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING),
            )
            Text(
                text = ranking.teamKey.teamNumber,
                style = MaterialTheme.typography.titleMedium,
                modifier =
                    Modifier
                        .weight(0.22f)
                        .clip(MaterialTheme.shapes.small)
                        .clickable(
                            onClickLabel = "View team ${ranking.teamKey.teamNumber}",
                            role = Role.Button,
                        ) { onTeamClick(ranking.teamKey) }
                        .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING + 4.dp),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "${ranking.wins}-${ranking.losses}-${ranking.ties}",
                style = MaterialTheme.typography.bodyMedium,
                modifier =
                    Modifier
                        .weight(0.15f)
                        .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING),
            )

            // Show first two sort order values (without labels, header has them)
            val primarySortValue =
                ranking.sortOrders.getOrNull(0)?.let { value ->
                    val precision = sortOrders?.getOrNull(0)?.precision ?: 2
                    String.format(Locale.US, "%.${precision}f", value)
                } ?: "--"

            Text(
                text = primarySortValue,
                style =
                    MaterialTheme.typography.labelLarge
                        .copy(fontFeatureSettings = TABULAR_FIGURES),
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .weight(0.23f)
                        .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING),
            )

            val secondarySortValue =
                ranking.sortOrders.getOrNull(1)?.let { value ->
                    val precision = sortOrders?.getOrNull(1)?.precision ?: 2
                    String.format(Locale.US, "%.${precision}f", value)
                } ?: "--"

            Text(
                text = secondarySortValue,
                style =
                    MaterialTheme.typography.labelLarge
                        .copy(fontFeatureSettings = TABULAR_FIGURES),
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .weight(0.19f)
                        .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING),
            )

            Icon(
                imageVector = TbaIcons.KeyboardArrowDown,
                // The row's stateDescription and click label already say this.
                contentDescription = null,
                modifier =
                    Modifier
                        .rotate(rotationAngle)
                        .weight(0.10f)
                        .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING),
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
                        text = "Tiebreakers:",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )

                    for (i in 2 until minOf(ranking.sortOrders.size, sortOrders.size)) {
                        val sortOrder = sortOrders[i]
                        val value = ranking.sortOrders[i]
                        // null = a missing component kept in place to preserve column alignment.
                        val formattedValue =
                            value?.let {
                                String.format(Locale.US, "%.${sortOrder.precision}f", it)
                            } ?: "--"

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
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
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "Additional Stats:",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    ranking.extraStats.forEachIndexed { index, value ->
                        val info = extraStatsInfo?.getOrNull(index)
                        val statName = info?.name ?: "Stat ${index + 1}"
                        val precision = info?.precision ?: 2
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
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
                                        ?: "--",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider()
    }
}
