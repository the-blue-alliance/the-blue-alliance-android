package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
import com.thebluealliance.android.ui.common.EmptyBox
import com.thebluealliance.android.ui.common.LoadingBox
import com.thebluealliance.android.ui.components.TableNaturalWidth
import com.thebluealliance.android.ui.components.blocksRowsBeneath
import com.thebluealliance.android.ui.components.expandableRow
import com.thebluealliance.android.ui.components.fitColumnWidths
import com.thebluealliance.android.ui.components.rankingRowDescription
import com.thebluealliance.android.ui.components.recordDescription
import com.thebluealliance.android.ui.components.sideScrollRowWidth
import com.thebluealliance.android.ui.components.sortableHeader
import com.thebluealliance.android.ui.components.statRowWidth
import com.thebluealliance.android.ui.components.tableCellTarget
import com.thebluealliance.android.ui.components.tableSideDrag
import com.thebluealliance.android.ui.components.tableSideScroll
import com.thebluealliance.android.ui.components.tableSpan
import com.thebluealliance.android.ui.components.tableWidth
import com.thebluealliance.android.ui.theme.BrandHeaderContainer
import com.thebluealliance.android.ui.theme.OnBrand
import com.thebluealliance.android.ui.theme.TBAMotionTokens
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.teamNumber
import java.util.Locale
import kotlin.math.roundToInt

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

/** Rank, Team, Record, primary sort, secondary sort, chevron. */
private val COLUMN_WEIGHTS = listOf(0.11f, 0.22f, 0.15f, 0.23f, 0.19f, 0.10f)

private const val TEAM_COLUMN = 1

/** Rank and Team, which stay put while a row's other columns scroll sideways. */
private const val FROZEN_COLUMNS = 2

private const val PRIMARY_COLUMN = 3
private const val SECONDARY_COLUMN = 4

/** Width of a sort carat or the row chevron. */
private val ICON_WIDTH = 24.dp

/** Widest a 5-digit team number, in the team column's style, is likely to be. */
private const val WIDEST_TEAM_NUMBER = "88888"

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

    // Narrow windows widen the columns whose header words would otherwise break, and scroll
    // sideways if even that doesn't fit.
    val minWidths = rankingColumnMinWidths(primaryLabel, secondaryLabel, sortState.column)
    val sideScroll = rememberScrollState()
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val (columnWidths, rowWidth, tableWidth) = rankingColumnLayout(maxWidth, minWidths)
        // Only the header and row grids scroll, together, so expanded details stay on screen.
        // Rank and Team stay put, so every scrolled value keeps its team beside it.
        val cueColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.24f)
        val rowScroll = rowWidth?.let { RowScroll(sideScroll, it, cueColor) }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        ) {
            stickyHeader {
                RankingHeaderRow(
                    primaryLabel = primaryLabel,
                    secondaryLabel = secondaryLabel,
                    columnWidths = columnWidths,
                    rowScroll = rowScroll,
                    tableWidth = tableWidth,
                    sortState = sortState,
                    onSortSelected = { selectedColumn ->
                        sortState = nextRankingSortState(sortState, selectedColumn)
                    },
                )
            }
            items(sortedRankings, key = { "${it.eventKey}_${it.teamKey}" }) { ranking ->
                RankingItem(
                    ranking = ranking,
                    columnWidths = columnWidths,
                    rowScroll = rowScroll,
                    tableWidth = tableWidth,
                    sortOrders = rankingSortOrders,
                    extraStatsInfo = rankingExtraStatsInfo,
                    onTeamClick = onTeamClick,
                )
            }
        }
    }
}

/**
 * Column widths, null when weighted; the side-scrolling row width, null when rows fit; and the
 * width the table needs, for [tableWidth].
 */
internal data class RankingColumnLayout(
    val columnWidths: List<Dp>?,
    val rowWidth: Dp?,
    val tableWidth: Dp,
)

/**
 * Lays the columns out within a [windowWidth]-wide window. The table needs its weighted width, or
 * more when its columns' [minWidths] do, and gets that capped at the reading column.
 */
internal fun rankingColumnLayout(
    windowWidth: Dp,
    minWidths: List<Dp>,
): RankingColumnLayout {
    val tableWidth =
        maxOf(TableNaturalWidth, minWidths.fold(TABLE_ROW_PADDING * 2) { a, b -> a + b })
    val viewport = tableSpan(windowWidth, tableWidth).width
    val columnWidths =
        fitColumnWidths(
            (viewport - TABLE_ROW_PADDING * 2).value,
            COLUMN_WEIGHTS,
            minWidths.map { it.value },
        )
    val rowWidth =
        sideScrollRowWidth(
            viewport.value,
            columnWidths,
            TABLE_ROW_PADDING.value,
            frozenCount = FROZEN_COLUMNS,
        )
    return RankingColumnLayout(columnWidths?.map { it.dp }, rowWidth?.dp, tableWidth)
}

/** Narrowest each column can be without breaking a header word or the team number. */
@Composable
private fun rankingColumnMinWidths(
    primaryLabel: String,
    secondaryLabel: String,
    sortColumn: RankingSortColumn,
): List<Dp> {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val headerStyle = MaterialTheme.typography.titleSmall
    val teamStyle = MaterialTheme.typography.titleMedium
    return remember(
        primaryLabel,
        secondaryLabel,
        sortColumn,
        headerStyle,
        teamStyle,
        density,
    ) {
        fun widest(
            text: String,
            style: TextStyle,
        ): Dp =
            text.split(' ').maxOf { word ->
                with(density) {
                    measurer
                        .measure(word, style, softWrap = false, maxLines = 1)
                        .size.width
                        .toDp()
                }
            }
        val gutters = COLUMN_GUTTER * 2

        fun caret(column: RankingSortColumn) = if (sortColumn == column) ICON_WIDTH else 0.dp
        listOf(
            widest("Rank", headerStyle) + gutters,
            maxOf(
                widest("Team", headerStyle) + caret(RankingSortColumn.TEAM),
                widest(WIDEST_TEAM_NUMBER, teamStyle),
            ) + gutters,
            widest("Record", headerStyle) + gutters,
            widest(primaryLabel, headerStyle) + caret(RankingSortColumn.PRIMARY) + gutters,
            widest(secondaryLabel, headerStyle) + caret(RankingSortColumn.SECONDARY) + gutters,
            ICON_WIDTH + gutters,
        )
    }
}

/** Sideways scrolling shared by the header and row grids when the columns overflow. */
private class RowScroll(
    val state: ScrollState,
    val rowWidth: Dp,
    val cueColor: Color,
)

/**
 * A header or ranking row: [frozen] cells, then [scrolling] cells, inside the table's row padding.
 * With [rowScroll], the [scrolling] cells and the end padding scroll sideways in the space the
 * [frozen] cells leave; otherwise all the cells share one row. [modifier] comes before the padding
 * and [contentModifier] after it.
 */
@Composable
private fun RankingRow(
    rowScroll: RowScroll?,
    modifier: Modifier,
    contentModifier: Modifier = Modifier,
    verticalPadding: Dp = 0.dp,
    frozen: @Composable RowScope.() -> Unit,
    scrolling: @Composable RowScope.() -> Unit,
) {
    if (rowScroll == null) {
        Row(
            modifier =
                modifier
                    .padding(horizontal = TABLE_ROW_PADDING, vertical = verticalPadding)
                    .then(contentModifier),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            frozen()
            scrolling()
        }
        return
    }
    Row(
        modifier =
            modifier
                .tableSideDrag(rowScroll.state, LocalLayoutDirection.current)
                .padding(start = TABLE_ROW_PADDING)
                .then(contentModifier)
                // Lets the scrolling part, and its edge cue, span the row's full height.
                .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // The vertical padding goes inside each part, so the edge cue spans the padding too.
        Row(
            modifier = Modifier.padding(vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            frozen()
        }
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .tableSideScroll(rowScroll.state, rowScroll.rowWidth, rowScroll.cueColor)
                    .padding(end = TABLE_ROW_PADDING)
                    .padding(vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            scrolling()
        }
    }
}

/** Sizes column [index] of [row]: its weight, or its fitted width from [columnWidths]. */
private fun Modifier.rankingColumn(
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
private fun RankingHeaderRow(
    primaryLabel: String,
    secondaryLabel: String,
    columnWidths: List<Dp>?,
    rowScroll: RowScroll?,
    tableWidth: Dp,
    sortState: RankingSortState,
    onSortSelected: (RankingSortColumn) -> Unit,
) {
    RankingRow(
        rowScroll = rowScroll,
        modifier =
            Modifier
                .tableWidth(tableWidth)
                .blocksRowsBeneath()
                .background(BrandHeaderContainer),
        // Sortable cells fill this height, so a wrapped label doesn't leave its neighbours with
        // shorter hover bands.
        contentModifier = Modifier.height(IntrinsicSize.Min),
        // 6.dp here + 6.dp inside each sortable cell below: the sort targets get a full-height
        // hover band without changing the header's height. The horizontal inset is the screen
        // margin minus the cells' own COLUMN_GUTTER.
        verticalPadding = 6.dp,
        frozen = {
            Text(
                text = "Rank",
                style = MaterialTheme.typography.titleSmall,
                color = OnBrand,
                modifier =
                    Modifier
                        .rankingColumn(
                            this,
                            0,
                            columnWidths,
                        ).padding(horizontal = COLUMN_GUTTER),
            )
            RankingHeaderItem(
                text = "Team",
                modifier = Modifier.rankingColumn(this, TEAM_COLUMN, columnWidths),
                sortColumn = RankingSortColumn.TEAM,
                currentSort = sortState.column,
                ascending = sortState.ascending,
                onSortClick = { onSortSelected(RankingSortColumn.TEAM) },
            )
        },
    ) {
        Text(
            text = "Record",
            style = MaterialTheme.typography.titleSmall,
            color = OnBrand,
            modifier =
                Modifier
                    .rankingColumn(
                        this,
                        2,
                        columnWidths,
                    ).padding(horizontal = COLUMN_GUTTER),
        )
        RankingHeaderItem(
            text = primaryLabel,
            modifier = Modifier.rankingColumn(this, PRIMARY_COLUMN, columnWidths),
            numeric = true,
            sortColumn = RankingSortColumn.PRIMARY,
            currentSort = sortState.column,
            ascending = sortState.ascending,
            onSortClick = { onSortSelected(RankingSortColumn.PRIMARY) },
        )
        RankingHeaderItem(
            text = secondaryLabel,
            modifier = Modifier.rankingColumn(this, SECONDARY_COLUMN, columnWidths),
            numeric = true,
            sortColumn = RankingSortColumn.SECONDARY,
            currentSort = sortState.column,
            ascending = sortState.ascending,
            onSortClick = { onSortSelected(RankingSortColumn.SECONDARY) },
        )
        // Spacer for chevron alignment
        Spacer(modifier = Modifier.rankingColumn(this, 5, columnWidths))
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
    // The sort target is a rounded band spanning the whole column. It bleeds past the label's
    // edge so the label keeps 8dp of room inside it while staying COLUMN_GUTTER from the column
    // edge, in line with the data cells below.
    val cellModifier =
        modifier
            .fillMaxHeight()
            .sortableHeader(
                shape = MaterialTheme.shapes.small,
                label = text,
                sorted = sorted,
                ascending = ascending,
                gutter = COLUMN_GUTTER,
                labelAtEnd = numeric,
                onClick = onSortClick,
            ).padding(vertical = 6.dp)
    if (numeric) {
        // Numeric column: right-align the label onto the values' shared right edge, with the sort
        // carat leading it (Material data-table convention — a leading carat keeps the numeric
        // header's right edge in line with the digits below). A hand layout, because this column
        // is narrow and its label ("Ranking Score") wraps to two lines: the label (child 0) is
        // measured at the cell's full width so it never ellipsizes, then right-anchored; the carat
        // (child 1) is placed just left of the label, spilling into the column's left slack rather
        // than stealing width from the label.
        val labelLayout = remember { LabelLayoutHolder() }
        val measurePolicy = remember(labelLayout) { NumericHeaderMeasurePolicy(labelLayout) }
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
                    onTextLayout = { labelLayout.layout = it },
                )
                if (sorted) {
                    SortCaret(ascending)
                }
            },
            measurePolicy = measurePolicy,
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

/** The numeric header label's latest text layout, set as the label measures. */
private class LabelLayoutHolder {
    var layout: TextLayoutResult? = null
}

/** Left edge of the widest line in [layout], which a wrapped label reports as its full width. */
private fun inkLeft(layout: TextLayoutResult?): Float =
    layout?.let { result -> (0 until result.lineCount).minOf { result.getLineLeft(it) } } ?: 0f

/**
 * X of a numeric header's sort caret: just left of the label's widest line, which starts
 * [labelInkLeft] into a label placed at [labelLeft], so a wrapped label keeps the caret beside its
 * text rather than at the cell's far edge.
 */
internal fun numericHeaderCaretX(
    labelLeft: Int,
    labelInkLeft: Float,
    caretWidth: Int,
): Int = (labelLeft + labelInkLeft.roundToInt() - caretWidth).coerceAtLeast(0)

/**
 * Lays out a numeric header cell: the label (child 0) right-anchored, the sort carat (optional
 * child 1) just left of its widest line. Intrinsic height mirrors measure, so an
 * `IntrinsicSize.Min` header row gets the label's real wrapped height.
 */
private class NumericHeaderMeasurePolicy(
    private val labelLayout: LabelLayoutHolder,
) : MeasurePolicy {
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
        // the carat directly left of its text.
        val labelLeft = (width - label.width).coerceAtLeast(caretWidth)
        return layout(width, height) {
            label.place(labelLeft, (height - label.height) / 2)
            caret?.place(
                numericHeaderCaretX(labelLeft, inkLeft(labelLayout.layout), caretWidth),
                (height - caret.height) / 2,
            )
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
    columnWidths: List<Dp>?,
    rowScroll: RowScroll?,
    tableWidth: Dp,
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

    // The first two sort order values, without labels (the header has them).
    val (primaryValue, secondaryValue) =
        (0..1).map { i ->
            ranking.sortOrders.getOrNull(i)?.let { value ->
                val precision = sortOrders?.getOrNull(i)?.precision ?: 2
                String.format(Locale.US, "%.${precision}f", value)
            }
        }
    val rowDescription =
        rankingRowDescription(
            rank = ranking.rank,
            teamNumber = ranking.teamKey.teamNumber,
            record = recordDescription(ranking.wins, ranking.losses, ranking.ties),
            sortLabels = rankingHeaderLabels(sortOrders).toList(),
            sortValues = listOf(primaryValue, secondaryValue),
        )

    Column(
        modifier = Modifier.tableWidth(tableWidth),
    ) {
        // Vertical padding lives in the cells: ROW_CELL_PADDING on each plain cell and
        // ROW_CELL_PADDING + 4.dp inside the team cell, so the row keeps its height while the
        // nested team target spans 48dp. The horizontal inset is the screen margin minus the
        // cells' own COLUMN_GUTTER.
        RankingRow(
            rowScroll = rowScroll,
            modifier =
                Modifier
                    .fillMaxWidth()
                    // The cells below clear their own semantics, so the row reads as one stop with
                    // column names; the team cell stays a separate target.
                    .semantics { contentDescription = rowDescription }
                    .expandableRow(expanded) { expanded = !expanded },
            frozen = {
                Text(
                    text = "#${ranking.rank}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier =
                        Modifier
                            .rankingColumn(this, 0, columnWidths)
                            .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING)
                            .clearAndSetSemantics {},
                )
                Text(
                    text = ranking.teamKey.teamNumber,
                    style = MaterialTheme.typography.titleMedium,
                    modifier =
                        Modifier
                            .rankingColumn(this, TEAM_COLUMN, columnWidths)
                            .tableCellTarget(
                                shape = MaterialTheme.shapes.small,
                                gutter = COLUMN_GUTTER,
                                labelAtEnd = false,
                                interaction =
                                    Modifier.clickable(
                                        onClickLabel = "View team ${ranking.teamKey.teamNumber}",
                                        role = Role.Button,
                                    ) { onTeamClick(ranking.teamKey) },
                            ).padding(vertical = ROW_CELL_PADDING + 4.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            },
        ) {
            Text(
                text = "${ranking.wins}-${ranking.losses}-${ranking.ties}",
                style = MaterialTheme.typography.bodyMedium,
                modifier =
                    Modifier
                        .rankingColumn(this, 2, columnWidths)
                        .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING)
                        .clearAndSetSemantics {},
            )

            Text(
                text = primaryValue ?: "--",
                style =
                    MaterialTheme.typography.labelLarge
                        .copy(fontFeatureSettings = TABULAR_FIGURES),
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .rankingColumn(this, PRIMARY_COLUMN, columnWidths)
                        .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING)
                        .clearAndSetSemantics {},
            )

            Text(
                text = secondaryValue ?: "--",
                style =
                    MaterialTheme.typography.labelLarge
                        .copy(fontFeatureSettings = TABULAR_FIGURES),
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .rankingColumn(this, SECONDARY_COLUMN, columnWidths)
                        .padding(horizontal = COLUMN_GUTTER, vertical = ROW_CELL_PADDING)
                        .clearAndSetSemantics {},
            )

            Icon(
                imageVector = TbaIcons.KeyboardArrowDown,
                // The row's stateDescription and click label already say this.
                contentDescription = null,
                modifier =
                    Modifier
                        .rotate(rotationAngle)
                        .rankingColumn(this, 5, columnWidths)
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
                            } ?: "--"

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
                        text = "Additional Stats",
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
