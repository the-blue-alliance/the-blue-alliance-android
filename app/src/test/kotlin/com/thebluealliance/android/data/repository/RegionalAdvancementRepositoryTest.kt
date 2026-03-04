package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.RegionalEventPointsDto
import com.thebluealliance.android.data.remote.dto.RegionalRankingDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
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

        val rankings = repository.getRegionalRankings(2026)

        assertEquals(2, rankings.size)
        assertEquals("frc1114", rankings[0].teamKey)
        assertEquals(1, rankings[0].rank)
        assertEquals("2026oncmp", rankings[0].eventPoints.first().eventKey)
        assertEquals(3.0, rankings[0].singleEventBonus)
    }
}

