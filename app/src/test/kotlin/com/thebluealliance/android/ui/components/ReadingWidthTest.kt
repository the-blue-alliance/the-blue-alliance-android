package com.thebluealliance.android.ui.components

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReadingWidthTest {
    @Test
    fun `no inset at or below the reading width`() {
        assertEquals(0.dp, readingInset(412.dp))
        assertEquals(0.dp, readingInset(840.dp))
    }

    @Test
    fun `wider windows center the column`() {
        assertEquals(92.dp, readingInset(1024.dp))
        assertEquals(220.dp, readingInset(1280.dp))
    }
}
