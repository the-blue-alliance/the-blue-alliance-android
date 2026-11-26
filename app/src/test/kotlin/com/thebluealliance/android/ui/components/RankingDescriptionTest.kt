package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RankingDescriptionTest {
    @Test
    fun `record names wins, losses and ties`() {
        assertEquals("7 wins, 3 losses, 0 ties", recordDescription(7, 3, 0))
    }

    @Test
    fun `record uses singular for one`() {
        assertEquals("1 win, 1 loss, 1 tie", recordDescription(1, 1, 1))
    }

    @Test
    fun `row names rank, team, record and each sort column`() {
        assertEquals(
            "Rank 23, Team 33, 7 wins, 3 losses, 0 ties, Ranking Score 2.30, Avg Coop 145.20",
            rankingRowDescription(
                rank = 23,
                teamNumber = "33",
                record = recordDescription(7, 3, 0),
                sortLabels = listOf("Ranking Score", "Avg Coop"),
                sortValues = listOf("2.30", "145.20"),
            ),
        )
    }

    @Test
    fun `missing sort value is read as none`() {
        assertEquals(
            "Rank 1, Team 254, 0 wins, 0 losses, 0 ties, RS none, Sort 2 none",
            rankingRowDescription(
                1,
                "254",
                recordDescription(0, 0, 0),
                listOf("RS", "Sort 2"),
                listOf(null, null),
            ),
        )
    }
}
