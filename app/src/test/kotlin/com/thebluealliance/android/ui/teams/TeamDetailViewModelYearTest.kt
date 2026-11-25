package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.ApiStatusDto
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamDetailViewModelYearTest {
    private val api: TbaApi = mockk()

    private fun viewModel() =
        TeamDetailViewModel(
            navKey = Screen.TeamDetail("frc254"),
            teamRepository = mockk(relaxed = true),
            eventRepository = mockk(relaxed = true),
            myTBARepository = mockk(relaxed = true),
            authRepository = mockk(relaxed = true),
            tbaApi = api,
            shortcutManager = mockk(relaxed = true),
            userMessages = UserMessageBus(),
        )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        coEvery { api.getTeamYearsParticipated("frc254") } returns listOf(2025, 2027, 2026)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `opens on the current season when the team is registered for the next`() {
        coEvery { api.getStatus() } returns ApiStatusDto(currentSeason = 2026, maxSeason = 2027)
        val viewModel = viewModel()
        assertEquals(2026, viewModel.selectedYear.value)
        assertEquals(listOf(2027, 2026, 2025), viewModel.yearsParticipated.value)
    }

    @Test
    fun `opens on the newest season once it is current`() {
        coEvery { api.getStatus() } returns ApiStatusDto(currentSeason = 2027, maxSeason = 2027)
        assertEquals(2027, viewModel().selectedYear.value)
    }

    @Test
    fun `refreshes the default season's data`() {
        coEvery { api.getStatus() } returns ApiStatusDto(currentSeason = 2026, maxSeason = 2027)
        val eventRepository = mockk<EventRepository>(relaxed = true)
        TeamDetailViewModel(
            navKey = Screen.TeamDetail("frc254"),
            teamRepository = mockk(relaxed = true),
            eventRepository = eventRepository,
            myTBARepository = mockk(relaxed = true),
            authRepository = mockk(relaxed = true),
            tbaApi = api,
            shortcutManager = mockk(relaxed = true),
            userMessages = UserMessageBus(),
        )
        coVerify { eventRepository.refreshTeamEvents("frc254", 2026) }
    }
}
