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

            val result = loadShortcutTeam("frc254", teamRepository, 2026, mutableSetOf())

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

            val result = loadShortcutTeam("frc254", teamRepository, 2026, mutableSetOf())

            assertEquals(team to avatar, result)
            coVerify(exactly = 1) { teamRepository.refreshTeam("frc254") }
            coVerify(exactly = 1) { teamRepository.refreshTeamMedia("frc254", 2026) }
        }

    @Test
    fun `failed refresh still uses the cache`() =
        runTest {
            every { teamRepository.observeTeam("frc254") } returns flowOf(team)
            every { teamRepository.observeTeamMedia("frc254", any()) } returns flowOf(emptyList())
            coEvery { teamRepository.refreshTeamMedia(any(), any()) } throws
                RuntimeException("offline")

            assertEquals(
                team to null,
                loadShortcutTeam("frc254", teamRepository, 2026, mutableSetOf()),
            )
        }

    @Test
    fun `falls back to last season's cached avatar`() =
        runTest {
            val lastYear = avatar.copy(year = 2025)
            every { teamRepository.observeTeam("frc254") } returns flowOf(team)
            every { teamRepository.observeTeamMedia("frc254", 2026) } returns flowOf(emptyList())
            every { teamRepository.observeTeamMedia("frc254", 2025) } returns
                flowOf(listOf(lastYear))

            assertEquals(
                team to lastYear,
                loadShortcutTeam("frc254", teamRepository, 2026, mutableSetOf()),
            )
            coVerify(exactly = 1) { teamRepository.refreshTeamMedia("frc254", 2026) }
            coVerify(exactly = 0) { teamRepository.refreshTeamMedia("frc254", 2025) }
        }

    @Test
    fun `fetched current avatar beats last season's`() =
        runTest {
            every { teamRepository.observeTeam("frc254") } returns flowOf(team)
            every { teamRepository.observeTeamMedia("frc254", 2026) } returnsMany
                listOf(flowOf(emptyList()), flowOf(listOf(avatar)))

            assertEquals(
                team to avatar,
                loadShortcutTeam("frc254", teamRepository, 2026, mutableSetOf()),
            )
        }

    @Test
    fun `a team without an avatar is fetched once per session`() =
        runTest {
            val lastYear = avatar.copy(year = 2025)
            val attempted = mutableSetOf<Pair<String, Int>>()
            every { teamRepository.observeTeam("frc254") } returns flowOf(team)
            every { teamRepository.observeTeamMedia("frc254", 2026) } returns flowOf(emptyList())
            every { teamRepository.observeTeamMedia("frc254", 2025) } returns
                flowOf(listOf(lastYear))

            repeat(3) {
                assertEquals(
                    team to lastYear,
                    loadShortcutTeam("frc254", teamRepository, 2026, attempted),
                )
            }

            coVerify(exactly = 1) { teamRepository.refreshTeamMedia("frc254", 2026) }
        }

    @Test
    fun `a new season is fetched even after last season was tried`() =
        runTest {
            val attempted = mutableSetOf("frc254" to 2025)
            every { teamRepository.observeTeam("frc254") } returns flowOf(team)
            every { teamRepository.observeTeamMedia("frc254", any()) } returns flowOf(emptyList())

            loadShortcutTeam("frc254", teamRepository, 2026, attempted)

            coVerify(exactly = 1) { teamRepository.refreshTeamMedia("frc254", 2026) }
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
