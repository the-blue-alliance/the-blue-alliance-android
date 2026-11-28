package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.NO_VALUE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TbaTableTest {
    private val weights = listOf(1f, 1f, 2f)

    @Test
    fun `ranks read with a hash`() {
        assertEquals("#1", tableRankLabel(1))
        assertEquals("#42", tableRankLabel(42))
    }

    @Test
    fun `a missing value shows the no-value mark`() {
        assertEquals(NO_VALUE, tableValue(null))
        assertEquals("12.50", tableValue("12.50"))
    }

    @Test
    fun `team click label names the team number, not the key`() {
        assertEquals("View team 254", viewTeamClickLabel("frc254"))
    }

    @Test
    fun `row dividers are Material's 1dp`() {
        assertEquals(1.dp, TableDividerThickness)
    }

    @Test
    fun `cell gutter plus row padding lands on the 16dp margin`() {
        assertEquals(16.dp, TableRowPadding + TableColumnGutter)
    }

    @Test
    fun `columns that fit their weights stay weighted at the natural width`() {
        val layout = tableColumnLayout(1024.dp, weights, List(3) { 40.dp }, frozenCount = 1)
        assertNull(layout.columnWidths)
        assertNull(layout.rowWidth)
        assertEquals(TableNaturalWidth, layout.tableWidth)
    }

    @Test
    fun `a narrow window gives short columns their minimum`() {
        val layout = tableColumnLayout(360.dp, weights, listOf(120.dp, 40.dp, 40.dp), 1)
        assertEquals(listOf(120.dp, 72.dp, 144.dp), layout.columnWidths)
        assertNull(layout.rowWidth)
    }

    @Test
    fun `columns too wide for the window scroll all but the frozen ones`() {
        val minWidths = listOf(100.dp, 200.dp, 200.dp)
        val layout = tableColumnLayout(360.dp, weights, minWidths, frozenCount = 1)
        assertEquals(minWidths, layout.columnWidths)
        assertEquals(200.dp + 200.dp + TableRowPadding, layout.rowWidth)
    }

    @Test
    fun `the band, rows and dividers share the table's one width`() {
        val layout = tableColumnLayout(1024.dp, weights, List(3) { 300.dp }, 1)
        assertEquals(924.dp, layout.tableWidth)
        assertEquals(ReadingMaxWidth, tableSpan(1024.dp, layout.tableWidth).width)
    }

    @Test
    fun `caret sits just left of a single-line label`() {
        assertEquals(40, numericHeaderCaretX(labelLeft = 64, labelInkLeft = 0f, caretWidth = 24))
    }

    @Test
    fun `caret sits beside a wrapped label's widest line, not the cell edge`() {
        assertEquals(30, numericHeaderCaretX(labelLeft = 24, labelInkLeft = 30.4f, caretWidth = 24))
    }

    @Test
    fun `caret never leaves the cell`() {
        assertEquals(0, numericHeaderCaretX(labelLeft = 10, labelInkLeft = 0f, caretWidth = 24))
    }
}
