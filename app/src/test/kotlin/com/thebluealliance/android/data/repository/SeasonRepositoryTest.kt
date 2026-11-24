package com.thebluealliance.android.data.repository

import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.ApiStatusDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.io.IOException
import java.time.Year

class SeasonRepositoryTest {
    private val api: TbaApi = mockk()

    @Test
    fun `fetches the status once for every caller`() =
        runTest {
            coEvery { api.getStatus() } returns ApiStatusDto(currentSeason = 2026, maxSeason = 2027)
            val seasons = SeasonRepository(api)
            assertEquals(2027, seasons.maxSeason())
            seasons.defaultSeason()
            assertEquals(2027, seasons.maxSeason())
            coVerify(exactly = 1) { api.getStatus() }
        }

    @Test
    fun `retries after a failed fetch`() =
        runTest {
            coEvery { api.getStatus() } throws IOException() andThen
                ApiStatusDto(currentSeason = 2026, maxSeason = 2027)
            val seasons = SeasonRepository(api)
            assertNull(seasons.maxSeason())
            assertEquals(2027, seasons.maxSeason())
        }

    @Test
    fun `default season is the current season`() =
        runTest {
            coEvery { api.getStatus() } returns ApiStatusDto(currentSeason = 2027, maxSeason = 2027)
            assertEquals(2027, SeasonRepository(api).defaultSeason())
        }

    @Test
    fun `default season falls back to the calendar year`() =
        runTest {
            coEvery { api.getStatus() } throws IOException()
            assertEquals(Year.now().value, SeasonRepository(api).defaultSeason())
        }
}
