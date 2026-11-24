package com.thebluealliance.android.ui.events.detail.tabs

import androidx.compose.ui.unit.dp
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class EventRankingsTabLogicTest {
    private fun ranking(
        team: String,
        primary: Double,
        secondary: Double,
    ) = Ranking(
        eventKey = "2026test",
        teamKey = team,
        rank = 1,
        dq = 0,
        matchesPlayed = 10,
        wins = 8,
        losses = 2,
        ties = 0,
        qualAverage = null,
        sortOrders = listOf(primary, secondary),
        extraStats = emptyList(),
    )

    @Test
    fun `header labels use metadata when provided`() {
        val labels =
            rankingHeaderLabels(
                listOf(
                    RankingSortOrder(name = "Ranking Score", precision = 2),
                    RankingSortOrder(name = "Auto Points", precision = 0),
                ),
            )

        assertEquals("Ranking Score", labels.first)
        assertEquals("Auto Points", labels.second)
    }

    @Test
    fun `header labels fall back when missing`() {
        val labels = rankingHeaderLabels(emptyList())

        assertEquals("RS", labels.first)
        assertEquals("Sort 2", labels.second)
    }

    @Test
    fun `switching to team sort defaults ascending`() {
        val initial = RankingSortState()

        val next = nextRankingSortState(initial, RankingSortColumn.TEAM)

        assertEquals(RankingSortColumn.TEAM, next.column)
        assertEquals(true, next.ascending)
    }

    @Test
    fun `switching to tiebreaker sort defaults descending`() {
        val initial = RankingSortState(column = RankingSortColumn.TEAM, ascending = true)

        val next = nextRankingSortState(initial, RankingSortColumn.SECONDARY)

        assertEquals(RankingSortColumn.SECONDARY, next.column)
        assertEquals(false, next.ascending)
    }

    @Test
    fun `selecting same sort toggles direction`() {
        val initial = RankingSortState(column = RankingSortColumn.PRIMARY, ascending = false)

        val next = nextRankingSortState(initial, RankingSortColumn.PRIMARY)

        assertEquals(RankingSortColumn.PRIMARY, next.column)
        assertEquals(true, next.ascending)
    }

    @Test
    fun `primary sort descending by default`() {
        val rankings =
            listOf(
                ranking(team = "frc111", primary = 2.3, secondary = 5.0),
                ranking(team = "frc222", primary = 3.0, secondary = 1.0),
                ranking(team = "frc333", primary = 1.1, secondary = 9.0),
            )

        val sorted = sortRankings(rankings, RankingSortState())

        assertEquals(listOf("frc222", "frc111", "frc333"), sorted.map { it.teamKey })
    }

    @Test
    fun `team sort ascending orders numerically`() {
        val rankings =
            listOf(
                ranking(team = "frc971", primary = 1.0, secondary = 1.0),
                ranking(team = "frc8", primary = 1.0, secondary = 1.0),
                ranking(team = "frc254", primary = 1.0, secondary = 1.0),
            )

        val sorted =
            sortRankings(
                rankings,
                RankingSortState(column = RankingSortColumn.TEAM, ascending = true),
            )

        assertEquals(listOf("frc8", "frc254", "frc971"), sorted.map { it.teamKey })
    }

    @Test
    fun `secondary sort descending orders by second sort order value`() {
        val rankings =
            listOf(
                ranking(team = "frc1", primary = 4.0, secondary = 0.2),
                ranking(team = "frc2", primary = 1.0, secondary = 9.4),
                ranking(team = "frc3", primary = 2.0, secondary = 7.7),
            )

        val sorted =
            sortRankings(
                rankings,
                RankingSortState(column = RankingSortColumn.SECONDARY, ascending = false),
            )

        assertEquals(listOf("frc2", "frc3", "frc1"), sorted.map { it.teamKey })
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

    @Test
    fun `wide windows scroll rows that overflow the capped table width`() {
        val minWidths = listOf(40.dp, 100.dp, 100.dp, 200.dp, 200.dp, 60.dp)
        val layout = rankingColumnLayout(1024.dp, minWidths)
        assertEquals(minWidths, layout.columnWidths)
        assertEquals(572.dp, layout.rowWidth)
    }

    @Test
    fun `wide windows don't scroll rows that fit the capped table width`() {
        val layout = rankingColumnLayout(1024.dp, List(6) { 40.dp })
        assertNull(layout.columnWidths)
        assertNull(layout.rowWidth)
    }
}
