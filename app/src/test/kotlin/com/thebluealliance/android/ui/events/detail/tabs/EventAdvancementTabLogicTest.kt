package com.thebluealliance.android.ui.events.detail.tabs

import com.thebluealliance.android.domain.model.EventAdvancementPoints
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventAdvancementTabLogicTest {
    private val points =
        EventAdvancementPoints(
            teamKey = "frc340",
            qualPoints = 18,
            elimPoints = 20,
            alliancePoints = 8,
            awardPoints = 5,
            rookieBonus = 10,
            total = 61,
        )

    @Test
    fun `district breakdown excludes rookie bonus`() {
        val rows = advancementBreakdownRows(points, isDistrictEvent = true)

        assertEquals(
            listOf("Qualification", "Elimination", "Alliance", "Awards"),
            rows.map { it.first },
        )
        assertEquals(listOf(18, 20, 8, 5), rows.map { it.second })
    }

    @Test
    fun `regional breakdown includes team age`() {
        val rows = advancementBreakdownRows(points, isDistrictEvent = false)

        assertEquals(
            listOf("Qualification", "Playoff", "Alliance", "Awards", "Team Age"),
            rows.map { it.first },
        )
        assertEquals(listOf(18, 20, 8, 5, 10), rows.map { it.second })
    }
}
