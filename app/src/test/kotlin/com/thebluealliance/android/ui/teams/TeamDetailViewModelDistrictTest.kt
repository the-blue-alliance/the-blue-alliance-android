package com.thebluealliance.android.ui.teams

import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamDetailViewModelDistrictTest {
    private val api: TbaApi = mockk()
    private val seasons: SeasonRepository = mockk()
    private val teamRepository: TeamRepository = mockk(relaxed = true)
    private val eventRepository: EventRepository = mockk(relaxed = true)
    private val districtRepository: DistrictRepository = mockk(relaxed = true)
    private val rankings = MutableStateFlow<List<DistrictRanking>>(emptyList())

    private fun event(district: String?) =
        Event(
            key = "2026mitry",
            name = "Troy",
            eventCode = "mitry",
            year = 2026,
            type = null,
            district = district,
            city = null,
            state = null,
            country = null,
            startDate = null,
            endDate = null,
            week = null,
            shortName = null,
            website = null,
            timezone = null,
            locationName = null,
            address = null,
            gmapsUrl = null,
            webcasts = emptyList(),
            playoffType = PlayoffType.BRACKET_8_TEAM,
        )

    private fun viewModel() =
        TeamDetailViewModel(
            navKey = Screen.TeamDetail("frc254"),
            teamRepository = teamRepository,
            eventRepository = eventRepository,
            districtRepository = districtRepository,
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
        coEvery { api.getTeamYearsParticipated("frc254") } returns listOf(2026)
        coEvery { seasons.defaultSeason() } returns 2026
        every { teamRepository.observeTeam("frc254") } returns flowOf(null)
        every { teamRepository.observeTeamMedia("frc254", any()) } returns flowOf(emptyList())
        every { districtRepository.observeDistrict("2026fim") } returns
            flowOf(District("2026fim", "fim", "FIRST In Michigan", 2026))
        every { districtRepository.observeDistrictRankings("2026fim") } returns rankings
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `shows the team's district standing once its district's rankings load`() =
        runTest(UnconfinedTestDispatcher()) {
            every { eventRepository.observeTeamEvents("frc254", 2026) } returns
                flowOf(listOf(event("2026fim")))
            val viewModel = viewModel()
            backgroundScope.launch { viewModel.uiState.collect {} }
            assertNull(viewModel.uiState.value.districtStanding)
            coVerify(exactly = 1) { districtRepository.refreshDistrictRankings("2026fim") }

            rankings.value = listOf(DistrictRanking("2026fim", "frc254", 12, 187.0, 0.0))
            assertEquals(
                TeamDistrictStanding("2026fim", "FIRST In Michigan", 12, 187),
                viewModel.uiState.value.districtStanding,
            )
        }

    @Test
    fun `no district events means no rankings fetch and no row`() =
        runTest(UnconfinedTestDispatcher()) {
            every { eventRepository.observeTeamEvents("frc254", 2026) } returns
                flowOf(listOf(event(null)))
            val viewModel = viewModel()
            backgroundScope.launch { viewModel.uiState.collect {} }
            assertNull(viewModel.uiState.value.districtStanding)
            coVerify(exactly = 0) { districtRepository.refreshDistrictRankings(any()) }
        }

    @Test
    fun `a failed rankings fetch leaves the row hidden`() =
        runTest(UnconfinedTestDispatcher()) {
            every { eventRepository.observeTeamEvents("frc254", 2026) } returns
                flowOf(listOf(event("2026fim")))
            coEvery { districtRepository.refreshDistrictRankings("2026fim") } throws
                RuntimeException("offline")
            val viewModel = viewModel()
            backgroundScope.launch { viewModel.uiState.collect {} }
            assertNull(viewModel.uiState.value.districtStanding)
        }

    @Test
    fun `a failed rankings fetch is retried when the year comes back`() =
        runTest(UnconfinedTestDispatcher()) {
            every { eventRepository.observeTeamEvents("frc254", 2026) } returns
                flowOf(listOf(event("2026fim")))
            every { eventRepository.observeTeamEvents("frc254", 2025) } returns
                flowOf(emptyList())
            coEvery { districtRepository.refreshDistrictRankings("2026fim") } throws
                RuntimeException("offline")
            val viewModel = viewModel()
            backgroundScope.launch { viewModel.uiState.collect {} }
            coVerify(exactly = 1) { districtRepository.refreshDistrictRankings("2026fim") }

            viewModel.selectYear(2025)
            viewModel.selectYear(2026)
            coVerify(exactly = 2) { districtRepository.refreshDistrictRankings("2026fim") }
        }

    @Test
    fun `a fetched district isn't fetched again when the year comes back`() =
        runTest(UnconfinedTestDispatcher()) {
            every { eventRepository.observeTeamEvents("frc254", 2026) } returns
                flowOf(listOf(event("2026fim")))
            every { eventRepository.observeTeamEvents("frc254", 2025) } returns
                flowOf(emptyList())
            val viewModel = viewModel()
            backgroundScope.launch { viewModel.uiState.collect {} }

            viewModel.selectYear(2025)
            viewModel.selectYear(2026)
            coVerify(exactly = 1) { districtRepository.refreshDistrictRankings("2026fim") }
        }

    @Test
    fun `a refresh before the new year's events load doesn't fetch the old year's districts`() =
        runTest(UnconfinedTestDispatcher()) {
            every { eventRepository.observeTeamEvents("frc254", 2026) } returns
                flowOf(listOf(event("2026fim")))
            every { eventRepository.observeTeamEvents("frc254", 2025) } returns
                MutableSharedFlow()
            val viewModel = viewModel()
            backgroundScope.launch { viewModel.uiState.collect {} }

            viewModel.selectYear(2025)
            viewModel.refreshAll()
            coVerify(exactly = 1) { districtRepository.refreshDistrictRankings("2026fim") }
        }
}
