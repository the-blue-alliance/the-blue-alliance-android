package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
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
    private val seasons: SeasonRepository = mockk()

    private fun viewModel(eventRepository: EventRepository = mockk(relaxed = true)) =
        TeamDetailViewModel(
            navKey = Screen.TeamDetail("frc254"),
            teamRepository = mockk(relaxed = true),
            eventRepository = eventRepository,
            myTBARepository = mockk(relaxed = true),
            authRepository = mockk(relaxed = true),
            tbaApi = api,
            seasons = seasons,
            shortcutManager = mockk(relaxed = true),
            userMessages = UserMessageBus(),
        )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        coEvery { api.getTeamYearsParticipated("frc254") } returns listOf(2023, 2027, 2024)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `opens on the default season when the team is registered for the next`() {
        coEvery { seasons.defaultSeason() } returns 2024
        val viewModel = viewModel()
        assertEquals(2024, viewModel.selectedYear.value)
        assertEquals(listOf(2027, 2024, 2023), viewModel.yearsParticipated.value)
    }

    @Test
    fun `opens on the newest season once it is the default`() {
        coEvery { seasons.defaultSeason() } returns 2027
        assertEquals(2027, viewModel().selectedYear.value)
    }

    @Test
    fun `refreshes the default season's data`() {
        coEvery { seasons.defaultSeason() } returns 2024
        val eventRepository = mockk<EventRepository>(relaxed = true)
        viewModel(eventRepository)
        coVerify { eventRepository.refreshTeamEvents("frc254", 2024) }
    }

    @Test
    fun `a late default doesn't override the user's pick`() {
        val default = CompletableDeferred<Int>()
        coEvery { seasons.defaultSeason() } coAnswers { default.await() }
        val viewModel = viewModel()
        viewModel.selectYear(2023)
        default.complete(2024)
        assertEquals(2023, viewModel.selectedYear.value)
    }
}
