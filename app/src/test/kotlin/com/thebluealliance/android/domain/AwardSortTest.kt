package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.AwardType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AwardSortTest {
    private fun award(
        awardType: Int,
        teamKey: String = "frc254",
        awardee: String? = null,
    ) = Award(
        eventKey = "2026casj",
        awardType = awardType,
        teamKey = teamKey,
        awardee = awardee,
        name = "Award $awardType",
        year = 2026,
    )

    private fun award(
        type: AwardType,
        teamKey: String = "frc254",
        awardee: String? = null,
    ) = award(awardType = type.code, teamKey = teamKey, awardee = awardee)

    @Test
    fun `impact award sorts first, then web prestige order, then winner and finalist`() {
        val highestRookieSeedCode = 14 // no named constant — stays a raw code
        val shuffled =
            listOf(
                award(AwardType.FINALIST),
                award(awardType = highestRookieSeedCode),
                award(AwardType.WINNER),
                award(AwardType.ENGINEERING_INSPIRATION),
                award(AwardType.CHAIRMANS),
                award(AwardType.WOODIE_FLOWERS),
            )

        val sorted = shuffled.sortedForDisplay().map { it.awardType }

        assertEquals(
            listOf(
                AwardType.CHAIRMANS.code,
                AwardType.ENGINEERING_INSPIRATION.code,
                AwardType.WOODIE_FLOWERS.code,
                AwardType.WINNER.code,
                AwardType.FINALIST.code,
                highestRookieSeedCode,
            ),
            sorted,
        )
    }

    @Test
    fun `awards without a named constant sort after listed ones by code`() {
        val chairmansFinalistCode = 69
        val industrialDesignCode = 11
        val shuffled =
            listOf(
                award(awardType = chairmansFinalistCode),
                award(awardType = industrialDesignCode),
                award(AwardType.FINALIST),
            )

        val sorted = shuffled.sortedForDisplay().map { it.awardType }

        assertEquals(
            listOf(AwardType.FINALIST.code, industrialDesignCode, chairmansFinalistCode),
            sorted,
        )
    }

    @Test
    fun `multi-recipient awards order by team number numerically`() {
        val shuffled =
            listOf(
                award(AwardType.WINNER, teamKey = "frc1114"),
                award(AwardType.WINNER, teamKey = "frc254"),
                award(AwardType.WINNER, teamKey = "frc33"),
            )

        val sorted = shuffled.sortedForDisplay().map { it.teamKey }

        assertEquals(listOf("frc33", "frc254", "frc1114"), sorted)
    }

    @Test
    fun `awardee-only awards sort after team awards of the same type by awardee name`() {
        val shuffled =
            listOf(
                award(AwardType.DEANS_LIST, teamKey = "", awardee = "Zoe Zebra"),
                award(AwardType.DEANS_LIST, teamKey = "", awardee = "Amy Aardvark"),
                award(AwardType.DEANS_LIST, teamKey = "frc1678", awardee = "Casey Carrot"),
            )

        val sorted = shuffled.sortedForDisplay().map { it.awardee }

        assertEquals(listOf("Casey Carrot", "Amy Aardvark", "Zoe Zebra"), sorted)
    }
}
