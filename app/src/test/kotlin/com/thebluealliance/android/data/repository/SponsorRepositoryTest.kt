package com.thebluealliance.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.thebluealliance.android.core.network.TbaClientFactory
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.SponsorDto
import com.thebluealliance.android.data.remote.dto.SponsorsResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.IOException

class SponsorRepositoryTest {
    @TempDir
    lateinit var tempDir: File

    private val api: TbaApi = mockk()
    private var now = 1_000_000_000L

    private val andyMark =
        SponsorDto(
            name = "AndyMark",
            url = "https://www.andymark.com/",
            logoUrl = "https://x/am.png",
        )
    private val quillfeather =
        SponsorDto(name = "Quillfeather Robotics", url = "https://example.com/", logoStyle = "mark")

    private fun TestScope.dataStore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = backgroundScope) {
            tempDir.resolve("test.preferences_pb")
        }

    private fun TestScope.repository(store: DataStore<Preferences> = dataStore()) =
        SponsorRepository(api, store, TbaClientFactory.json) { now }

    private fun notFound() = HttpException(Response.error<Any>(404, "".toResponseBody()))

    @Test
    fun `empty before any fetch`() =
        runTest {
            assertTrue(repository().sponsors.first().isEmpty())
        }

    @Test
    fun `refresh stores sponsors in order`() =
        runTest {
            coEvery { api.getSponsors() } returns
                SponsorsResponseDto(listOf(andyMark, quillfeather))
            val repository = repository()

            repository.refreshIfStale()

            assertEquals(
                listOf(
                    "AndyMark",
                    "Quillfeather Robotics",
                ),
                repository.sponsors.first().map {
                    it.name
                },
            )
        }

    @Test
    fun `cache persists to a new repository on the same store`() =
        runTest {
            coEvery { api.getSponsors() } returns SponsorsResponseDto(listOf(andyMark))
            val store = dataStore()
            repository(store).refreshIfStale()

            assertEquals(listOf("AndyMark"), repository(store).sponsors.first().map { it.name })
        }

    @Test
    fun `fresh cache skips the network`() =
        runTest {
            coEvery { api.getSponsors() } returns SponsorsResponseDto(listOf(andyMark))
            val repository = repository()
            repository.refreshIfStale()

            now += SponsorRepository.CACHE_TTL_MILLIS - 1
            repository.refreshIfStale()

            coVerify(exactly = 1) { api.getSponsors() }
        }

    @Test
    fun `stale cache refetches`() =
        runTest {
            coEvery { api.getSponsors() } returnsMany
                listOf(
                    SponsorsResponseDto(listOf(andyMark)),
                    SponsorsResponseDto(listOf(quillfeather)),
                )
            val repository = repository()
            repository.refreshIfStale()

            now += SponsorRepository.CACHE_TTL_MILLIS
            repository.refreshIfStale()

            coVerify(exactly = 2) { api.getSponsors() }
            assertEquals(
                listOf("Quillfeather Robotics"),
                repository.sponsors.first().map { it.name },
            )
        }

    @Test
    fun `empty response clears the credit`() =
        runTest {
            coEvery { api.getSponsors() } returnsMany
                listOf(SponsorsResponseDto(listOf(andyMark)), SponsorsResponseDto(emptyList()))
            val repository = repository()
            repository.refreshIfStale()

            now += SponsorRepository.CACHE_TTL_MILLIS
            repository.refreshIfStale()

            assertTrue(repository.sponsors.first().isEmpty())
        }

    @Test
    fun `404 with no cache shows nothing`() =
        runTest {
            coEvery { api.getSponsors() } throws notFound()
            val repository = repository()

            repository.refreshIfStale()

            assertTrue(repository.sponsors.first().isEmpty())
        }

    @Test
    fun `failed refetch keeps the cached sponsors`() =
        runTest {
            coEvery { api.getSponsors() } returns SponsorsResponseDto(listOf(andyMark))
            val repository = repository()
            repository.refreshIfStale()
            coEvery { api.getSponsors() } throws IOException("offline")

            now += SponsorRepository.CACHE_TTL_MILLIS
            repository.refreshIfStale()

            assertEquals(listOf("AndyMark"), repository.sponsors.first().map { it.name })
        }

    @Test
    fun `failed fetch retries on the next refresh`() =
        runTest {
            coEvery { api.getSponsors() } throws notFound()
            val repository = repository()
            repository.refreshIfStale()
            coEvery { api.getSponsors() } returns SponsorsResponseDto(listOf(andyMark))

            repository.refreshIfStale()

            assertEquals(listOf("AndyMark"), repository.sponsors.first().map { it.name })
        }

    @Test
    fun `unreadable cache shows nothing`() =
        runTest {
            val store = dataStore()
            store.edit { it[SponsorRepository.SPONSORS_JSON_KEY] = "not json" }

            assertTrue(repository(store).sponsors.first().isEmpty())
        }
}
