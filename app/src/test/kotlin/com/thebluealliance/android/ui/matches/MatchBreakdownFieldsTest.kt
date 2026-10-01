package com.thebluealliance.android.ui.matches

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
    fun `no season uses an unqualified foul label`() {
        val bareLabels = listOf("Fouls", "Tech fouls", "Minor fouls", "Major fouls", "Foul points")
        for (year in 2023..2026) {
            val labels = labelsFor(year)
            for (bare in bareLabels) {
                assert(bare !in labels) { "$year still has '$bare'" }
            }
        }
    }
}
