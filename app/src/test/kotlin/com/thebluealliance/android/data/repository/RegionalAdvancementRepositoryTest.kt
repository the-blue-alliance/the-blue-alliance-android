package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.RegionalAdvancementDto
import com.thebluealliance.android.data.remote.dto.RegionalEventPointsDto
import com.thebluealliance.android.data.remote.dto.RegionalRankingDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RegionalAdvancementRepositoryTest {

    private val api: TbaApi = mockk()
    private val repository = RegionalAdvancementRepository(api)

    @Test
    fun `getRegionalRankings maps and sorts rankings`() = runTest {
        coEvery { api.getRegionalAdvancementRankings(2026) } returns listOf(
            RegionalRankingDto(
                teamKey = "frc254",
                rank = 2,
                pointTotal = 67.0,
                eventPoints = listOf(RegionalEventPointsDto(eventKey = "2026cafr", total = 34.0)),
            ),
            RegionalRankingDto(
                teamKey = "frc1114",
                rank = 1,
                pointTotal = 70.0,
                rookieBonus = 0.0,
                singleEventBonus = 3.0,
                eventPoints = listOf(RegionalEventPointsDto(eventKey = "2026oncmp", total = 40.0)),
            ),
        )
        coEvery { api.getRegionalAdvancement(2026) } returns emptyMap()

        val rankings = repository.getRegionalRankings(2026)

        assertEquals(2, rankings.size)
        assertEquals("frc1114", rankings[0].teamKey)
        assertEquals(1, rankings[0].rank)
        assertEquals("2026oncmp", rankings[0].eventPoints.first().eventKey)
        assertEquals(3.0, rankings[0].singleEventBonus)
    }

    @Test
    fun `getRegionalRankings includes championship qualification status`() = runTest {
        coEvery { api.getRegionalAdvancementRankings(2026) } returns listOf(
            RegionalRankingDto(
                teamKey = "frc1816",
                rank = 1,
                pointTotal = 85.0,
                eventPoints = listOf(
                    RegionalEventPointsDto(eventKey = "2026week1", total = 45.0),
                    RegionalEventPointsDto(eventKey = "2026week2", total = 40.0),
                ),
            ),
            RegionalRankingDto(
                teamKey = "frc254",
                rank = 2,
                pointTotal = 67.0,
                eventPoints = listOf(RegionalEventPointsDto(eventKey = "2026cafr", total = 67.0)),
            ),
        )
        coEvery { api.getRegionalAdvancement(2026) } returns mapOf(
            "frc1816" to RegionalAdvancementDto(
                cmp = true,
                cmpStatus = "PreQualified",
                qualifyingPoolWeek = 1,
            )
        )

        val rankings = repository.getRegionalRankings(2026)

        assertEquals(2, rankings.size)
        assertEquals("frc1816", rankings[0].teamKey)
        assertEquals("PreQualified", rankings[0].advancementMethod)
        assertEquals("frc254", rankings[1].teamKey)
        assertNull(rankings[1].advancementMethod)
    }

    @Test
    fun `getRegionalRankings handles various championship statuses`() = runTest {
        coEvery { api.getRegionalAdvancementRankings(2026) } returns listOf(
            RegionalRankingDto(teamKey = "frc1816", rank = 1, pointTotal = 90.0),
            RegionalRankingDto(teamKey = "frc254", rank = 2, pointTotal = 85.0),
            RegionalRankingDto(teamKey = "frc1114", rank = 3, pointTotal = 80.0),
            RegionalRankingDto(teamKey = "frc971", rank = 4, pointTotal = 75.0),
        )
        coEvery { api.getRegionalAdvancement(2026) } returns mapOf(
            "frc1816" to RegionalAdvancementDto(
                cmp = true,
                cmpStatus = "PreQualified",
                qualifyingPoolWeek = 1,
            ),
            "frc254" to RegionalAdvancementDto(
                cmp = true,
                cmpStatus = "Qualified",
                qualifyingPoolWeek = 3,
            ),
            "frc1114" to RegionalAdvancementDto(
                cmp = true,
                cmpStatus = "Waitlisted",
                qualifyingPoolWeek = null,
            ),
        )

        val rankings = repository.getRegionalRankings(2026)

        assertEquals(4, rankings.size)
        assertEquals("PreQualified", rankings[0].advancementMethod)
        assertEquals("Qualified", rankings[1].advancementMethod)
        assertEquals("Waitlisted", rankings[2].advancementMethod)
        assertNull(rankings[3].advancementMethod)
    }

    @Test
    fun `getRegionalRankings handles null API responses`() = runTest {
        coEvery { api.getRegionalAdvancementRankings(2026) } returns null
        coEvery { api.getRegionalAdvancement(2026) } returns null

        val rankings = repository.getRegionalRankings(2026)

        assertEquals(0, rankings.size)
    }

    @Test
    fun `getRegionalRankings handles empty event points`() = runTest {
        coEvery { api.getRegionalAdvancementRankings(2026) } returns listOf(
            RegionalRankingDto(
                teamKey = "frc254",
                rank = 1,
                pointTotal = 50.0,
                rookieBonus = 20.0,
                singleEventBonus = 30.0,
                eventPoints = emptyList(),
            ),
        )
        coEvery { api.getRegionalAdvancement(2026) } returns emptyMap()

        val rankings = repository.getRegionalRankings(2026)

        assertEquals(1, rankings.size)
        assertEquals("frc254", rankings[0].teamKey)
        assertEquals(50.0, rankings[0].pointTotal)
        assertEquals(20.0, rankings[0].rookieBonus)
        assertEquals(30.0, rankings[0].singleEventBonus)
        assertEquals(0, rankings[0].eventPoints.size)
    }

    @Test
    fun `getRegionalRankings sorts by rank ascending`() = runTest {
        coEvery { api.getRegionalAdvancementRankings(2026) } returns listOf(
            RegionalRankingDto(teamKey = "frc254", rank = 5, pointTotal = 50.0),
            RegionalRankingDto(teamKey = "frc1114", rank = 2, pointTotal = 80.0),
            RegionalRankingDto(teamKey = "frc971", rank = 3, pointTotal = 75.0),
            RegionalRankingDto(teamKey = "frc1816", rank = 1, pointTotal = 90.0),
            RegionalRankingDto(teamKey = "frc118", rank = 4, pointTotal = 60.0),
        )
        coEvery { api.getRegionalAdvancement(2026) } returns emptyMap()

        val rankings = repository.getRegionalRankings(2026)

        assertEquals(5, rankings.size)
        assertEquals(1, rankings[0].rank)
        assertEquals(2, rankings[1].rank)
        assertEquals(3, rankings[2].rank)
        assertEquals(4, rankings[3].rank)
        assertEquals(5, rankings[4].rank)
    }
}

