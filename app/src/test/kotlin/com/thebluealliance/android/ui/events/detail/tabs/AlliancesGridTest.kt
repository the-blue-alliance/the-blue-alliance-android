package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AlliancesGridTest {
    @Test
    fun `phones and medium windows keep one column`() {
        listOf(320.dp, 412.dp, 600.dp, 839.dp).forEach { assertFalse(alliancesUseGrid(it)) }
    }

    @Test
    fun `wide windows use the grid`() {
        listOf(840.dp, 1024.dp).forEach { assertTrue(alliancesUseGrid(it)) }
    }
}
