package com.thebluealliance.android.ui.districts

import com.thebluealliance.android.domain.model.DistrictEventPoints
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DistrictRankingsRowTest {
    private fun points(
        eventKey: String,
        total: Double,
        districtCmp: Boolean,
    ) = DistrictEventPoints(eventKey = eventKey, total = total, districtCmp = districtCmp)

    @Test
    fun `sums division and finals points at a dcmp with divisions`() {
        val eventPoints =
            listOf(
                points("2026necmp1", 45.0, districtCmp = true),
                points("2026necmp", 30.0, districtCmp = true),
                points("2026nhgrs", 60.0, districtCmp = false),
            )

        assertEquals(75, dcmpPoints(eventPoints))
    }

    @Test
    fun `single dcmp entry shows its total`() {
        val eventPoints =
            listOf(
                points("2026micmp", 52.0, districtCmp = true),
                points("2026miket", 70.0, districtCmp = false),
            )

        assertEquals(52, dcmpPoints(eventPoints))
    }

    @Test
    fun `no dcmp entries has no points`() {
        val eventPoints = listOf(points("2026nhgrs", 60.0, districtCmp = false))

        assertNull(dcmpPoints(eventPoints))
    }

    @Test
    fun `row description names each column and reads empty cells as none`() {
        assertEquals(
            "Rank 3, Team 254, Event 1: 45 points, Event 2: 52 points, DCMP none, " +
                "rookie bonus none, 97 total points",
            districtRowDescription(3, "254", 45, 52, null, 0, 97),
        )
    }

    @Test
    fun `row description reads a rookie with one event and dcmp points`() {
        assertEquals(
            "Rank 40, Team 10254, Event 1: 30 points, Event 2 none, DCMP: 12 points, " +
                "rookie bonus 10, 52 total points",
            districtRowDescription(40, "10254", 30, null, 12, 10, 52),
        )
    }

    @Test
    fun `row description reads one point as singular`() {
        assertEquals(
            "Rank 5, Team 254, Event 1: 1 point, Event 2: 2 points, DCMP: 0 points, " +
                "rookie bonus none, 1 total point",
            districtRowDescription(5, "254", 1, 2, 0, 0, 1),
        )
    }
}
