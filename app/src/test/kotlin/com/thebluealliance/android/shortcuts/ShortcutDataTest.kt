package com.thebluealliance.android.shortcuts

import android.util.Log
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.Team
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ShortcutDataTest {
    private val team = Team("frc254", 254, null, "The Cheesy Poofs", null, null, null, 1999)
    private val avatar = Media("frc254", "avatar", "avatar_2026_frc254", 2026, false, null, "abc")
    private val teamRepository = mockk<TeamRepository>(relaxUnitFun = true)

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.w(any<String>(), any<String>()) } returns 0
    }

    @AfterEach
    fun tearDown() = unmockkStatic(Log::class)

    @Test
    fun `cached team and avatar need no network`() =
        runTest {
            every { teamRepository.observeTeam("frc254") } returns flowOf(team)
            every { teamRepository.observeTeamMedia("frc254", 2026) } returns flowOf(listOf(avatar))

            val result = loadShortcutTeam("frc254", teamRepository, FetchOnce { 2026 })

            assertEquals(team to avatar, result)
            coVerify(exactly = 0) { teamRepository.refreshTeam(any()) }
            coVerify(exactly = 0) { teamRepository.refreshTeamMedia(any(), any()) }
        }

    @Test
    fun `missing team and avatar are fetched`() =
        runTest {
            every { teamRepository.observeTeam("frc254") } returnsMany
                listOf(flowOf(null), flowOf(team))
            every { teamRepository.observeTeamMedia("frc254", 2026) } returnsMany
                listOf(flowOf(emptyList()), flowOf(listOf(avatar)))

            val result = loadShortcutTeam("frc254", teamRepository, FetchOnce { 2026 })

            assertEquals(team to avatar, result)
            coVerify(exactly = 1) { teamRepository.refreshTeam("frc254") }
            coVerify(exactly = 1) { teamRepository.refreshTeamMedia("frc254", 2026) }
        }

    @Test
    fun `failed refresh still uses the cache`() =
        runTest {
            every { teamRepository.observeTeam("frc254") } returns flowOf(team)
            every { teamRepository.observeTeamMedia("frc254", 2026) } returns flowOf(emptyList())
            coEvery { teamRepository.refreshTeamMedia(any(), any()) } throws
                RuntimeException("offline")

            assertEquals(
                team to null,
                loadShortcutTeam("frc254", teamRepository, FetchOnce { 2026 }),
            )
        }

    @Test
    fun `season is fetched once per sync`() =
        runTest {
            var calls = 0
            val season = FetchOnce { ++calls + 2025 }
            every { teamRepository.observeTeam(any()) } returns flowOf(team)
            every { teamRepository.observeTeamMedia(any(), any()) } returns flowOf(listOf(avatar))

            loadShortcutTeam("frc254", teamRepository, season)
            loadShortcutTeam("frc1678", teamRepository, season)

            assertEquals(1, calls)
        }

    @Test
    fun `failed season fetch skips team shortcuts without retrying`() =
        runTest {
            var calls = 0
            val season =
                FetchOnce<Int> {
                    calls++
                    throw RuntimeException("offline")
                }

            assertNull(loadShortcutTeam("frc254", teamRepository, season))
            assertNull(loadShortcutTeam("frc1678", teamRepository, season))
            assertEquals(1, calls)
        }

    @Test
    fun `cached event needs no network`() =
        runTest {
            val eventRepository = mockk<EventRepository>(relaxUnitFun = true)
            every { eventRepository.observeEvent("2026casj") } returns flowOf(mockk())

            loadShortcutEvent("2026casj", eventRepository)

            coVerify(exactly = 0) { eventRepository.refreshEvent(any()) }
        }

    @Test
    fun `missing event is fetched`() =
        runTest {
            val eventRepository = mockk<EventRepository>(relaxUnitFun = true)
            every { eventRepository.observeEvent("2026casj") } returns flowOf(null)

            assertNull(loadShortcutEvent("2026casj", eventRepository))

            coVerify(exactly = 1) { eventRepository.refreshEvent("2026casj") }
        }
}
