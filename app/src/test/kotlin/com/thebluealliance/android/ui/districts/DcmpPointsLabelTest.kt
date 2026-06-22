package com.thebluealliance.android.ui.districts

import com.thebluealliance.android.domain.model.DistrictEventPoints
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DcmpPointsLabelTest {
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

        assertEquals("75", dcmpPointsLabel(eventPoints))
    }

    @Test
    fun `single dcmp entry shows its total`() {
        val eventPoints =
            listOf(
                points("2026micmp", 52.0, districtCmp = true),
                points("2026miket", 70.0, districtCmp = false),
            )

        assertEquals("52", dcmpPointsLabel(eventPoints))
    }

    @Test
    fun `no dcmp entries shows a dash`() {
        val eventPoints = listOf(points("2026nhgrs", 60.0, districtCmp = false))

        assertEquals("-", dcmpPointsLabel(eventPoints))
    }
}
