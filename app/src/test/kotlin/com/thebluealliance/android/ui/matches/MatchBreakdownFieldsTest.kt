package com.thebluealliance.android.ui.matches

import com.thebluealliance.android.domain.formatBreakdownValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatchBreakdownFieldsTest {
    private fun labelsFor(year: Int): List<String> =
        getOrderedBreakdownFields(year, emptyMap(), emptyMap()).map { it.second }

    private fun foulRows(year: Int): List<String> {
        val labels = labelsFor(year)
        val end = labels.indexOf("Foul points received")
        val start = labels.indexOfFirst { it.contains("fouls committed", ignoreCase = true) }
        return labels.subList(start, end + 1)
    }

    @Test
    fun `2026 labels minor and major fouls as committed then points received`() {
        assertEquals(
            listOf("Minor fouls committed", "Major fouls committed", "Foul points received"),
            foulRows(2026),
        )
    }

    @Test
    fun `2023 to 2025 label fouls and tech fouls as committed then points received`() {
        for (year in 2023..2025) {
            assertEquals(
                listOf("Fouls committed", "Tech fouls committed", "Foul points received"),
                foulRows(year),
                "year $year",
            )
        }
    }

    @Test
    fun `2015 labels fouls as committed then points deducted`() {
        val labels = labelsFor(2015)
        val start = labels.indexOf("Fouls committed")
        assertEquals(
            listOf("Fouls committed", "Foul points deducted"),
            labels.subList(start, start + 2),
        )
    }

    @Test
    fun `2015 uses explicit snake case keys rather than the fallback`() {
        val red = mapOf("foul_count" to "1", "foul_points" to "6", "total_points" to "64")
        val fields = getOrderedBreakdownFields(2015, red, emptyMap()).toMap()
        assertEquals("Fouls committed", fields["foul_count"])
        assertEquals("Foul points deducted", fields["foul_points"])
        assertEquals("Total", fields["total_points"])
    }

    @Test
    fun `2015 foul points format as a deduction`() {
        assertEquals("−6", formatBreakdownValue("foul_points", "6"))
        assertEquals("0", formatBreakdownValue("foul_points", "0"))
        assertEquals("6", formatBreakdownValue("foulPoints", "6"))
    }

    @Test
    fun `no season uses an unqualified foul label`() {
        val bareLabels = listOf("Fouls", "Tech fouls", "Minor fouls", "Major fouls", "Foul points")
        for (year in listOf(2015) + (2023..2026)) {
            val labels = labelsFor(year)
            for (bare in bareLabels) {
                assert(bare !in labels) { "$year still has '$bare'" }
            }
        }
    }
}
