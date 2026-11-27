package com.thebluealliance.android.ui.components

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MediaColumnsTest {
    @Test
    fun `a lone tile spans the column`() {
        assertEquals(1, mediaColumns(count = 1, columnsThatFit = 2))
        assertEquals(1, mediaColumns(count = 1, columnsThatFit = 5))
    }

    @Test
    fun `two tiles split the column in half`() {
        assertEquals(2, mediaColumns(count = 2, columnsThatFit = 2))
        assertEquals(2, mediaColumns(count = 2, columnsThatFit = 5))
    }

    @Test
    fun `three or more tiles fill the columns that fit`() {
        assertEquals(2, mediaColumns(count = 3, columnsThatFit = 2))
        assertEquals(5, mediaColumns(count = 3, columnsThatFit = 5))
        assertEquals(3, mediaColumns(count = 9, columnsThatFit = 3))
    }

    @Test
    fun `two tiles stack where only one fits`() {
        assertEquals(1, mediaColumns(count = 2, columnsThatFit = 1))
    }

    @Test
    fun `a window too narrow for one tile still gets a column`() {
        assertEquals(1, mediaColumns(count = 4, columnsThatFit = 0))
    }

    @Test
    fun `a lone tile's cell is the whole width`() {
        assertEquals(listOf(824), cells(MediaGridCells(240.dp, count = 1), 824))
    }

    @Test
    fun `two tiles' cells split the width around the spacing`() {
        assertEquals(listOf(408, 408), cells(MediaGridCells(240.dp, count = 2), 824))
    }

    @Test
    fun `three or more tiles get the adaptive grid's cells`() {
        for (width in listOf(304, 344, 824, 1264)) {
            assertEquals(
                cells(GridCells.Adaptive(240.dp), width),
                cells(MediaGridCells(240.dp, count = 7), width),
            )
            assertEquals(
                cells(GridCells.Adaptive(160.dp), width),
                cells(MediaGridCells(160.dp, count = 3), width),
            )
        }
    }

    private fun cells(
        gridCells: GridCells,
        width: Int,
    ): List<Int> = with(gridCells) { Density(1f).calculateCrossAxisCellSizes(width, 8) }
}
