package com.thebluealliance.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.ApiStatusDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.IOException
import java.time.Year

class SeasonRepositoryTest {
    @TempDir
    lateinit var tempDir: File

    private val api: TbaApi = mockk()

    private fun TestScope.dataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = backgroundScope) {
            tempDir.resolve("test.preferences_pb")
        }

    // One repository per app session; sessions share the on-disk store.
    private fun TestScope.session(store: DataStore<Preferences> = dataStore()) =
        SeasonRepository(api, store)

    private fun nextSeason() = Year.now().value + 1

    @Test
    fun `fetches the status once for every caller`() =
        runTest {
            coEvery { api.getStatus() } returns ApiStatusDto(currentSeason = 2026, maxSeason = 2027)
            val seasons = session()
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
            val seasons = session()
            assertNull(seasons.maxSeason())
            assertEquals(2027, seasons.maxSeason())
        }

    @Test
    fun `default season is the current season`() =
        runTest {
            coEvery { api.getStatus() } returns ApiStatusDto(currentSeason = 2027, maxSeason = 2027)
            assertEquals(2027, session().defaultSeason())
        }

    @Test
    fun `default season falls back to the calendar year when nothing was ever fetched`() =
        runTest {
            coEvery { api.getStatus() } throws IOException()
            assertEquals(Year.now().value, session().defaultSeason())
        }

    @Test
    fun `an offline start opens on the last fetched season`() =
        runTest {
            val store = dataStore()
            val season = nextSeason()
            coEvery { api.getStatus() } returns
                ApiStatusDto(currentSeason = season, maxSeason = season)
            session(store).defaultSeason()
            coEvery { api.getStatus() } throws IOException()
            val offline = session(store)
            assertEquals(season, offline.defaultSeason())
            assertEquals(season, offline.maxSeason())
        }

    @Test
    fun `a fresh fetch replaces the remembered season`() =
        runTest {
            val store = dataStore()
            val season = nextSeason()
            coEvery { api.getStatus() } returns
                ApiStatusDto(currentSeason = season - 1, maxSeason = season - 1)
            session(store).defaultSeason()
            coEvery { api.getStatus() } returns
                ApiStatusDto(currentSeason = season, maxSeason = season)
            assertEquals(season, session(store).defaultSeason())
            coEvery { api.getStatus() } throws IOException()
            assertEquals(season, session(store).defaultSeason())
        }

    @Test
    fun `an offline start still retries the fetch`() =
        runTest {
            val store = dataStore()
            val season = nextSeason()
            coEvery { api.getStatus() } returns
                ApiStatusDto(currentSeason = season, maxSeason = season)
            session(store).defaultSeason()
            coEvery { api.getStatus() } throws IOException() andThen
                ApiStatusDto(currentSeason = season + 1, maxSeason = season + 1)
            val seasons = session(store)
            assertEquals(season, seasons.defaultSeason())
            assertEquals(season + 1, seasons.defaultSeason())
            coVerify(exactly = 3) { api.getStatus() }
        }
}
