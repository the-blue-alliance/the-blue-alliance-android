package com.thebluealliance.android.ui.teams

import android.util.Log
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class TeamDetailViewModelNotFoundTest {
    private val teams: TeamRepository = mockk(relaxed = true)
    private val events: EventRepository = mockk(relaxed = true)
    private val team = Team("frc99999", 99999, null, "Nobody", null, null, null, 2024)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        every { teams.observeTeam(any()) } returns flowOf(null)
        every { teams.observeTeamMedia(any(), any()) } returns flowOf(emptyList())
        every { events.observeTeamEvents(any(), any()) } returns flowOf(emptyList())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun notFound() = HttpException(Response.error<Any>(404, "".toResponseBody()))

    private fun TestScope.subscribedViewModel(): TeamDetailViewModel {
        val viewModel =
            TeamDetailViewModel(
                navKey = Screen.TeamDetail("99999"),
                teamRepository = teams,
                eventRepository = events,
                districtRepository = mockk(relaxed = true),
                myTBARepository = mockk(relaxed = true),
                authRepository = mockk(relaxed = true),
                tbaApi = mockk(relaxed = true),
                seasons = mockk(relaxed = true),
                shortcutManager = mockk(relaxed = true),
                userMessages = UserMessageBus(),
            )
        backgroundScope.launch(
            UnconfinedTestDispatcher(testScheduler),
        ) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun `a missing team shows not found`() =
        runTest {
            coEvery { teams.refreshTeam(any()) } throws notFound()
            assertTrue(subscribedViewModel().uiState.value.notFound)
        }

    @Test
    fun `a cached team that is now missing keeps its cache`() =
        runTest {
            every { teams.observeTeam(any()) } returns flowOf(team)
            coEvery { teams.refreshTeam(any()) } throws notFound()
            assertFalse(subscribedViewModel().uiState.value.notFound)
        }

    @Test
    fun `a team that can't be fetched offline is not missing`() =
        runTest {
            coEvery { teams.refreshTeam(any()) } throws UnknownHostException()
            assertFalse(subscribedViewModel().uiState.value.notFound)
        }
}
