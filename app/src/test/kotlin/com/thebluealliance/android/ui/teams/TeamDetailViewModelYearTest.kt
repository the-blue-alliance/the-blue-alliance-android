package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.data.repository.TeamRepository
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
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class TeamDetailViewModelYearTest {
    private val api: TbaApi = mockk()
    private val seasons: SeasonRepository = mockk()

    private fun viewModel(
        eventRepository: EventRepository = mockk(relaxed = true),
        teamRepository: TeamRepository = mockk(relaxed = true),
        navKey: Screen.TeamDetail = Screen.TeamDetail("frc254"),
    ) = TeamDetailViewModel(
        navKey = navKey,
        teamRepository = teamRepository,
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

    @Test
    fun `loads only the team until the default season arrives`() {
        val default = CompletableDeferred<Int>()
        coEvery { seasons.defaultSeason() } coAnswers { default.await() }
        val eventRepository = mockk<EventRepository>(relaxed = true)
        val teamRepository = mockk<TeamRepository>(relaxed = true)
        viewModel(eventRepository, teamRepository)
        coVerify { teamRepository.refreshTeam("frc254") }
        coVerify(exactly = 0) { eventRepository.refreshTeamEvents(any(), any()) }
        coVerify(exactly = 0) { teamRepository.refreshTeamMedia(any(), any()) }
        default.complete(2024)
        coVerify(exactly = 1) { eventRepository.refreshTeamEvents(any(), any()) }
        coVerify(exactly = 1) { eventRepository.refreshTeamEvents("frc254", 2024) }
        coVerify(exactly = 1) { teamRepository.refreshTeamMedia("frc254", 2024) }
    }

    @Test
    fun `loads a default matching the initial year once`() {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        coEvery { api.getTeamYearsParticipated("frc254") } returns listOf(year)
        coEvery { seasons.defaultSeason() } returns year
        val eventRepository = mockk<EventRepository>(relaxed = true)
        viewModel(eventRepository)
        coVerify(exactly = 1) { eventRepository.refreshTeamEvents("frc254", year) }
    }

    @Test
    fun `asks for the default season while the team's years are in flight`() {
        val years = CompletableDeferred<List<Int>>()
        coEvery { api.getTeamYearsParticipated("frc254") } coAnswers { years.await() }
        coEvery { seasons.defaultSeason() } returns 2024
        viewModel()
        coVerify { seasons.defaultSeason() }
        years.complete(listOf(2024))
    }

    @Test
    fun `a late default doesn't load its season over the user's pick`() {
        val default = CompletableDeferred<Int>()
        coEvery { seasons.defaultSeason() } coAnswers { default.await() }
        val eventRepository = mockk<EventRepository>(relaxed = true)
        val viewModel = viewModel(eventRepository)
        viewModel.selectYear(2023)
        default.complete(2024)
        coVerify(exactly = 0) { eventRepository.refreshTeamEvents("frc254", 2024) }
        coVerify(exactly = 1) { eventRepository.refreshTeamEvents("frc254", 2023) }
    }

    @Test
    fun `without the team's years, loads the default season`() {
        coEvery { api.getTeamYearsParticipated("frc254") } throws RuntimeException()
        coEvery { seasons.defaultSeason() } returns 2024
        val eventRepository = mockk<EventRepository>(relaxed = true)
        val viewModel = viewModel(eventRepository)
        assertEquals(2024, viewModel.selectedYear.value)
        coVerify(exactly = 1) { eventRepository.refreshTeamEvents("frc254", 2024) }
    }

    @Test
    fun `opens on the year from the link`() {
        coEvery { seasons.defaultSeason() } returns 2027
        assertEquals(2024, viewModel(navKey = linkedYear(2024)).selectedYear.value)
    }

    @Test
    fun `loads only the year from the link`() {
        coEvery { seasons.defaultSeason() } returns 2027
        val eventRepository = mockk<EventRepository>(relaxed = true)
        val teamRepository = mockk<TeamRepository>(relaxed = true)
        viewModel(eventRepository, teamRepository, navKey = linkedYear(2024))
        coVerify(exactly = 1) { eventRepository.refreshTeamEvents("frc254", 2024) }
        coVerify(exactly = 1) { teamRepository.refreshTeamMedia("frc254", 2024) }
        coVerify(exactly = 0) { eventRepository.refreshTeamEvents("frc254", 2027) }
    }

    @Test
    fun `a late default doesn't override the year from the link`() {
        val default = CompletableDeferred<Int>()
        coEvery { seasons.defaultSeason() } coAnswers { default.await() }
        val viewModel = viewModel(navKey = linkedYear(2023))
        default.complete(2027)
        assertEquals(2023, viewModel.selectedYear.value)
    }

    /** A /team/254/<year> link's key, built by name so this compiles before the field exists. */
    private fun linkedYear(year: Int): Screen.TeamDetail =
        Json.decodeFromString("""{"teamKey":"frc254","year":$year}""")
}
