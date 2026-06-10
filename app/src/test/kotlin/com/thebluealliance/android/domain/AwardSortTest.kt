package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.Award
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AwardSortTest {
    private fun award(
        awardType: Int,
        teamKey: String = "frc254",
        awardee: String? = null,
        name: String = "Award $awardType",
    ) = Award(
        eventKey = "2026casj",
        awardType = awardType,
        teamKey = teamKey,
        awardee = awardee,
        name = name,
        year = 2026,
    )

    @Test
    fun `impact award sorts first, then web prestige order, then winner and finalist`() {
        val shuffled =
            listOf(
                award(awardType = 2), // Finalist
                award(awardType = 14), // Highest Rookie Seed
                award(awardType = 1), // Winner
                award(awardType = 9), // Engineering Inspiration
                award(awardType = 0), // Chairman's/Impact
                award(awardType = 3), // Woodie Flowers
            )

        val sorted = shuffled.sortedForDisplay().map { it.awardType }

        assertEquals(listOf(0, 9, 3, 1, 2, 14), sorted)
    }

    @Test
    fun `awards not in the priority list sort after listed ones by award type`() {
        val shuffled =
            listOf(
                award(awardType = 69), // Chairman's Finalist (unlisted)
                award(awardType = 11), // Industrial Design (unlisted)
                award(awardType = 2), // Finalist (listed, last priority)
            )

        val sorted = shuffled.sortedForDisplay().map { it.awardType }

        assertEquals(listOf(2, 11, 69), sorted)
    }

    @Test
    fun `multi-recipient awards order by team number numerically`() {
        val shuffled =
            listOf(
                award(awardType = 1, teamKey = "frc1114"),
                award(awardType = 1, teamKey = "frc254"),
                award(awardType = 1, teamKey = "frc33"),
            )

        val sorted = shuffled.sortedForDisplay().map { it.teamKey }

        assertEquals(listOf("frc33", "frc254", "frc1114"), sorted)
    }

    @Test
    fun `awardee-only awards sort after team awards of the same type by awardee name`() {
        val shuffled =
            listOf(
                award(awardType = 4, teamKey = "", awardee = "Zoe Zebra"),
                award(awardType = 4, teamKey = "", awardee = "Amy Aardvark"),
                award(awardType = 4, teamKey = "frc1678", awardee = "Casey Carrot"),
            )

        val sorted = shuffled.sortedForDisplay().map { it.awardee }

        assertEquals(listOf("Casey Carrot", "Amy Aardvark", "Zoe Zebra"), sorted)
    }
}
