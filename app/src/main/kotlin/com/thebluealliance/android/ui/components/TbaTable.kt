package com.thebluealliance.android.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.NO_VALUE
import com.thebluealliance.android.ui.theme.BrandFocusRing
import com.thebluealliance.android.ui.theme.BrandHeaderContainer
import com.thebluealliance.android.ui.theme.OnBrand
import com.thebluealliance.android.ui.theme.TbaIcons
import com.thebluealliance.android.util.teamNumber
import kotlin.math.roundToInt

// The house data table: a brand header band over rows of weighted columns, one table width for
// the band, rows and dividers. [TbaTable] is the scaffold; the rest are its building blocks, for
// tables that need only some of them.

/**
 * Half of the gutter between two table columns, applied inside every cell of both the header and
 * the data rows so the columns stay aligned. Rows carry [TableRowPadding] instead of the usual
 * 16.dp so the outermost cells' content still lands on the screen's 16.dp margin.
 */
val TableColumnGutter: Dp = 4.dp

/** Row inset that, plus [TableColumnGutter] inside the end cells, restores the 16.dp margin. */
val TableRowPadding: Dp = 12.dp

/** Vertical inset of a plain data cell; the team cell adds 4.dp more for a 48dp target. */
val TableCellPadding: Dp = 8.dp

/** Width of a sort caret or a row chevron. */
val TableIconWidth: Dp = 24.dp

/** Thickness of the divider under each row: Material's default, across the table's width. */
val TableDividerThickness: Dp = DividerDefaults.Thickness

/** OpenType tabular figures: every digit takes the same advance, so numeric columns grid up. */
const val TABULAR_FIGURES = "tnum"

/** A rank as every table shows it. */
fun tableRankLabel(rank: Int): String = "#$rank"

/** What a table cell shows for [value], with [NO_VALUE] for a missing one. */
fun tableValue(value: String?): String = value ?: NO_VALUE

/** What activating a team cell, or a row that opens its team, does: read after the number. */
fun viewTeamClickLabel(teamKey: String): String = "View team ${teamKey.teamNumber}"

/**
 * Column widths, null when weighted; the side-scrolling row width, null when rows fit; and the
 * width the table needs, for [tableWidth].
 */
internal data class TableColumnLayout(
    val columnWidths: List<Dp>?,
    val rowWidth: Dp?,
    val tableWidth: Dp,
)

/**
 * Lays [weights] columns out within a [windowWidth]-wide window. The table needs its natural
 * width, or more when its columns' [minWidths] do, and gets that capped at the reading column.
 * Columns that don't fit even then scroll sideways, all but the first [frozenCount].
 */
internal fun tableColumnLayout(
    windowWidth: Dp,
    weights: List<Float>,
    minWidths: List<Dp>,
    frozenCount: Int,
): TableColumnLayout {
    val tableWidth =
        maxOf(TableNaturalWidth, minWidths.fold(TableRowPadding * 2) { a, b -> a + b })
    val viewport = tableSpan(windowWidth, tableWidth).width
    val columnWidths =
        fitColumnWidths(
            (viewport - TableRowPadding * 2).value,
            weights,
            minWidths.map { it.value },
        )
    val rowWidth =
        sideScrollRowWidth(
            viewport.value,
            columnWidths,
            TableRowPadding.value,
            frozenCount = frozenCount,
        )
    return TableColumnLayout(columnWidths?.map { it.dp }, rowWidth?.dp, tableWidth)
}

/** Sideways scrolling shared by the header and every row when the columns overflow. */
data class TableRowScroll internal constructor(
    val state: ScrollState,
    val rowWidth: Dp,
    val cueColor: Color,
)

/**
 * One laid-out table: its column [weights], their fitted [columnWidths] (null when weighted), the
 * [rowScroll] its rows share when they overflow, and the [tableWidth] its band, rows and dividers
 * all take.
 */
@Stable
data class TableScope internal constructor(
    val weights: List<Float>,
    val columnWidths: List<Dp>?,
    val rowScroll: TableRowScroll?,
    val tableWidth: Dp,
) {
    /** Sizes column [index] of [row]: its weight, or its fitted width. */
    fun Modifier.tableColumn(
        row: RowScope,
        index: Int,
    ): Modifier =
        if (columnWidths == null) {
            with(row) { weight(weights[index]) }
        } else {
            width(columnWidths[index])
        }
}

/** How Tab reaches a table's sticky header. */
enum class TableHeaderFocus {
    /** The header has targets and so do the rows: Tab reaches the header first. */
    BeforeRows,

    /** Only the header has targets; a focused header must not scroll the list. */
    InPlace,

    /** The header has no targets, only labels: Tab goes straight to the rows. */
    None,
}

/**
 * The house data table: a sticky [header] over the [rows], in a list as wide as the window with
 * the table at the reading column's start edge. [minWidths], one per column, widen columns whose
 * words would otherwise break, and scroll the rows sideways, all but the first [frozenColumns],
 * when even that doesn't fit.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TbaTable(
    weights: List<Float>,
    minWidths: List<Dp>,
    frozenColumns: Int,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues.Zero,
    headerFocus: TableHeaderFocus = TableHeaderFocus.BeforeRows,
    header: @Composable TableScope.() -> Unit,
    rows: LazyListScope.(TableScope) -> Unit,
) {
    val sideScroll = rememberScrollState()
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val layout = tableColumnLayout(maxWidth, weights, minWidths, frozenColumns)
        // Only the header and row grids scroll, together, so expanded details stay on screen.
        // The frozen columns stay put, so every scrolled value keeps its row's name beside it.
        val cueColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.24f)
        val rowScroll = layout.rowWidth?.let { TableRowScroll(sideScroll, it, cueColor) }
        val table = TableScope(weights, layout.columnWidths, rowScroll, layout.tableWidth)
        val focusOrder = rememberStickyHeaderFocusOrder()
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .then(
                        if (headerFocus == TableHeaderFocus.BeforeRows) {
                            Modifier.stickyHeaderFocusOrder(focusOrder)
                        } else {
                            Modifier
                        },
                    ).verticalScrollbar(listState, contentPadding),
            contentPadding = contentPadding,
        ) {
            when (headerFocus) {
                TableHeaderFocus.BeforeRows -> tabOrderedStickyHeader(focusOrder) { table.header() }
                TableHeaderFocus.InPlace -> inPlaceStickyHeader { table.header() }
                TableHeaderFocus.None -> stickyHeader { table.header() }
            }
            rows(table)
        }
    }
}

/**
 * A header or data row: [frozen] cells, then [scrolling] cells, inside [TableRowPadding]. With a
 * [TableScope.rowScroll], the [scrolling] cells and the end padding scroll sideways in the space
 * the [frozen] cells leave; otherwise all the cells share one row. [modifier] comes before the
 * padding and [contentModifier] after it.
 */
@Composable
fun TableScope.TableRow(
    modifier: Modifier,
    contentModifier: Modifier = Modifier,
    verticalPadding: Dp = 0.dp,
    frozen: @Composable RowScope.() -> Unit,
    scrolling: @Composable RowScope.() -> Unit,
) {
    val rowScroll = rowScroll
    if (rowScroll == null) {
        Row(
            modifier =
                modifier
                    .padding(horizontal = TableRowPadding, vertical = verticalPadding)
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
                .padding(start = TableRowPadding)
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
                    .padding(end = TableRowPadding)
                    .padding(vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            scrolling()
        }
    }
}

/**
 * The header band's look: [naturalWidth] wide like the rows beneath it, the brand blue, and a hit
 * target that keeps input on its plain labels from reaching the row scrolled under it.
 */
fun Modifier.tableHeaderBand(naturalWidth: Dp = TableNaturalWidth): Modifier =
    this
        .tableWidth(naturalWidth)
        .blocksRowsBeneath()
        .background(BrandHeaderContainer)

/**
 * The table's header row on the brand band. Its cells fill its height, so a wrapped label doesn't
 * leave its neighbours with shorter sort highlights; sort targets draw the brand focus ring.
 */
@Composable
fun TableScope.TableHeaderBand(
    verticalPadding: Dp,
    frozen: @Composable RowScope.() -> Unit,
    scrolling: @Composable RowScope.() -> Unit,
) {
    BrandFocusRing(BrandHeaderContainer) {
        TableRow(
            modifier = Modifier.tableHeaderBand(tableWidth),
            contentModifier = Modifier.height(IntrinsicSize.Min),
            verticalPadding = verticalPadding,
            frozen = frozen,
            scrolling = scrolling,
        )
    }
}

/** A plain header label on the brand band. */
@Composable
fun TableHeaderLabel(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = OnBrand,
        textAlign = textAlign,
        modifier = modifier,
    )
}

/**
 * A header cell that sorts its column, its rounded highlight spanning the whole cell. A [numeric]
 * column right-aligns its label onto the values' shared right edge and leads it with the sort
 * caret (the Material data-table convention); a text column trails the caret. [modifier] sizes the
 * cell; [verticalPadding] goes inside the highlight.
 */
@Composable
fun SortableHeaderCell(
    text: String,
    sorted: Boolean,
    ascending: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    numeric: Boolean = false,
    verticalPadding: Dp = 6.dp,
) {
    // The highlight bleeds past the label's edge so the label keeps 8dp of room inside it while
    // staying TableColumnGutter from the column edge, in line with the data cells below.
    val cellModifier =
        modifier
            .fillMaxHeight()
            .sortableHeader(
                shape = MaterialTheme.shapes.small,
                label = text,
                sorted = sorted,
                ascending = ascending,
                gutter = TableColumnGutter,
                labelAtEnd = numeric,
                onClick = onClick,
            ).padding(vertical = verticalPadding)
    if (numeric) {
        // A hand layout, because a narrow numeric column's label ("Ranking Score") wraps to two
        // lines: the caret (child 1) reserves its width first and the label (child 0) is measured
        // in what's left, then right-anchored, with the caret just left of its widest line.
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
    layout?.let { result ->
        (0 until result.lineCount).minOfOrNull { result.getLineLeft(it) }
    } ?: 0f

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
 * Lays out a numeric header cell: the label (child 0) right-anchored, the sort caret (optional
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
        // Reserve the caret's width up front, then measure the label in what's left, so a
        // right-aligned (full-width) two-line label can't swallow the whole column and clip
        // the caret off the cell's leading edge.
        val caret = measurables.getOrNull(1)?.measure(loose)
        val caretWidth = caret?.width ?: 0
        val width = constraints.maxWidth
        val labelMax = (width - caretWidth).coerceAtLeast(0)
        val label = measurables[0].measure(loose.copy(maxWidth = labelMax))
        val height = maxOf(label.height, caret?.height ?: 0, constraints.minHeight)
        // Right-anchor the label on the cell's right edge (the values' shared edge), then seat
        // the caret directly left of its text.
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
        imageVector = if (ascending) TbaIcons.ArrowDropUp else TbaIcons.ArrowDropDown,
        // The header cell's stateDescription announces the sort.
        contentDescription = null,
        tint = OnBrand,
    )
}

/**
 * A row's rank, "#1". Its semantics are cleared: the row reads as one stop with column names.
 * [modifier] sizes the cell.
 */
@Composable
fun TableRankCell(
    rank: Int,
    modifier: Modifier = Modifier,
) {
    TableValueCell(
        value = tableRankLabel(rank),
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier,
    )
}

/**
 * A data cell showing [value], or [NO_VALUE] when it's missing, on one line. A [numeric] value is
 * set in tabular figures and right-aligned, so a column of them stacks digit for digit. Its
 * semantics are cleared: the row reads as one stop with column names. [modifier] sizes the cell.
 */
@Composable
fun TableValueCell(
    value: String?,
    style: TextStyle,
    modifier: Modifier = Modifier,
    numeric: Boolean = false,
) {
    Text(
        text = tableValue(value),
        style = if (numeric) style.copy(fontFeatureSettings = TABULAR_FIGURES) else style,
        textAlign = if (numeric) TextAlign.End else null,
        maxLines = 1,
        softWrap = false,
        modifier =
            modifier
                .padding(horizontal = TableColumnGutter, vertical = TableCellPadding)
                .clearAndSetSemantics {},
    )
}

/**
 * A team number that opens the team: link-coloured, with a rounded highlight that keeps the
 * number in line with the column. [modifier] sizes the cell; [verticalPadding] goes inside the
 * highlight, so the default gives a 48dp target in a row of [TableCellPadding] cells.
 */
@Composable
fun TableTeamCell(
    teamKey: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    verticalPadding: Dp = TableCellPadding + 4.dp,
) {
    Text(
        text = teamKey.teamNumber,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        softWrap = false,
        modifier =
            modifier
                .tableCellTarget(
                    shape = MaterialTheme.shapes.small,
                    gutter = TableColumnGutter,
                    labelAtEnd = false,
                    interaction =
                        Modifier.returnsFocus().handCursor().clickable(
                            onClickLabel = viewTeamClickLabel(teamKey),
                            role = Role.Button,
                        ) { onClick(teamKey) },
                ).padding(vertical = verticalPadding),
        color = MaterialTheme.colorScheme.primary,
    )
}

/** The divider under each row, [TableDividerThickness] and as wide as the table, never inset. */
@Composable
fun TableRowDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, thickness = TableDividerThickness)
}

/** Measures text for a table's column minimums at the current font scale. */
class TableTextWidths internal constructor(
    private val measurer: TextMeasurer,
    private val density: Density,
) {
    /** Width of [text] on one line in [style]. */
    fun of(
        text: String,
        style: TextStyle,
    ): Dp =
        with(density) {
            measurer
                .measure(text, style, softWrap = false, maxLines = 1)
                .size.width
                .toDp()
        }

    /** Width of the widest word of [text], e.g. so a header label wraps but never mid-word. */
    fun widestWord(
        text: String,
        style: TextStyle,
    ): Dp = text.split(' ').maxOf { of(it, style) }

    /** Width of the widest whole value of [values], so a cell never wraps a number. */
    fun widestValue(
        values: List<String>,
        style: TextStyle,
    ): Dp = values.distinct().maxOfOrNull { of(it, style) } ?: 0.dp
}

/** A [TableTextWidths] for the current density, for use inside a `remember`. */
@Composable
fun rememberTableTextWidths(): TableTextWidths {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(measurer, density) { TableTextWidths(measurer, density) }
}
