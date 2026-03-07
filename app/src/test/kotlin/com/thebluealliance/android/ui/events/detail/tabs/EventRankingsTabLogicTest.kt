package com.thebluealliance.android.ui.events.detail.tabs

import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
import org.junit.jupiter.api.Assertions.assertEquals
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
        val labels = rankingHeaderLabels(
            listOf(
                RankingSortOrder(name = "Ranking Score", precision = 2),
                RankingSortOrder(name = "Auto Points", precision = 0),
            )
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
        val rankings = listOf(
            ranking(team = "frc111", primary = 2.3, secondary = 5.0),
            ranking(team = "frc222", primary = 3.0, secondary = 1.0),
            ranking(team = "frc333", primary = 1.1, secondary = 9.0),
        )

        val sorted = sortRankings(rankings, RankingSortState())

        assertEquals(listOf("frc222", "frc111", "frc333"), sorted.map { it.teamKey })
    }

    @Test
    fun `team sort ascending orders numerically`() {
        val rankings = listOf(
            ranking(team = "frc971", primary = 1.0, secondary = 1.0),
            ranking(team = "frc8", primary = 1.0, secondary = 1.0),
            ranking(team = "frc254", primary = 1.0, secondary = 1.0),
        )

        val sorted = sortRankings(
            rankings,
            RankingSortState(column = RankingSortColumn.TEAM, ascending = true),
        )

        assertEquals(listOf("frc8", "frc254", "frc971"), sorted.map { it.teamKey })
    }

    @Test
    fun `secondary sort descending orders by second sort order value`() {
        val rankings = listOf(
            ranking(team = "frc1", primary = 4.0, secondary = 0.2),
            ranking(team = "frc2", primary = 1.0, secondary = 9.4),
            ranking(team = "frc3", primary = 2.0, secondary = 7.7),
        )

        val sorted = sortRankings(
            rankings,
            RankingSortState(column = RankingSortColumn.SECONDARY, ascending = false),
        )

        assertEquals(listOf("frc2", "frc3", "frc1"), sorted.map { it.teamKey })
    }
}

