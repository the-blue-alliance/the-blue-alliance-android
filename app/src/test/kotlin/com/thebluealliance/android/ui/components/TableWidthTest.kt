package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TableWidthTest {
    @Test
    fun `no inset at or below the table width`() {
        assertEquals(0.dp, tableInset(412.dp))
        assertEquals(0.dp, tableInset(720.dp))
    }

    @Test
    fun `wider windows center the table`() {
        assertEquals(60.dp, tableInset(840.dp))
        assertEquals(152.dp, tableInset(1024.dp))
    }
}
