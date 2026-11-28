package com.thebluealliance.android.ui.teams

import android.util.Log
import app.cash.turbine.test
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.Media
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CompletableDeferred
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class TeamDetailViewModelLoadTest {
    private val teams: TeamRepository = mockk(relaxed = true)
    private val events: EventRepository = mockk(relaxed = true)
    private val bus = UserMessageBus()

    private val team = Team("frc254", 254, null, "The Cheesy Poofs", null, null, null, 1999)
    private val event =
        Event(
            key = "2024casj",
            name = "Silicon Valley Regional",
            eventCode = "casj",
            year = 2024,
            type = 0,
            district = null,
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
    private val photo = Media("frc254", "imgur", "abc", 2024, false, null)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        cache(team = null, events = emptyList(), media = emptyList())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun cache(
        team: Team?,
        events: List<Event>,
        media: List<Media>,
    ) {
        every { teams.observeTeam(any()) } returns flowOf(team)
        every { this@TeamDetailViewModelLoadTest.events.observeTeamEvents(any(), any()) } returns
            flowOf(events)
        every { teams.observeTeamMedia(any(), any()) } returns flowOf(media)
    }

    private fun refreshesThrow(error: Exception) {
        coEvery { teams.refreshTeam(any()) } throws error
        coEvery { events.refreshTeamEvents(any(), any()) } throws error
        coEvery { teams.refreshTeamMedia(any(), any()) } throws error
    }

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    private fun TestScope.subscribedViewModel(): TeamDetailViewModel {
        val viewModel =
            TeamDetailViewModel(
                navKey = Screen.TeamDetail("254"),
                teamRepository = teams,
                eventRepository = events,
                myTBARepository = mockk(relaxed = true),
                authRepository = mockk(relaxed = true),
                tbaApi = mockk(relaxed = true),
                seasons = mockk(relaxed = true),
                shortcutManager = mockk(relaxed = true),
                userMessages = bus,
            )
        backgroundScope.launch(
            UnconfinedTestDispatcher(testScheduler),
        ) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun `every tab shows a load error when its fetch fails offline and nothing is cached`() =
        runTest {
            refreshesThrow(UnknownHostException())
            val state = subscribedViewModel().uiState.value
            TeamDetailTab.entries.forEach { tab ->
                assertTrue(state.loadFailed(tab), "$tab should show a load error")
            }
        }

    @Test
    fun `a server error with nothing cached is a load error`() =
        runTest {
            refreshesThrow(httpError(503))
            val state = subscribedViewModel().uiState.value
            TeamDetailTab.entries.forEach { tab -> assertTrue(state.loadFailed(tab), "$tab") }
        }

    @Test
    fun `a fetch that succeeds with no data is empty, not an error`() =
        runTest {
            val state = subscribedViewModel().uiState.value
            TeamDetailTab.entries.forEach { tab -> assertFalse(state.loadFailed(tab), "$tab") }
        }

    @Test
    fun `a missing team is not a load error`() =
        runTest {
            refreshesThrow(httpError(404))
            val state = subscribedViewModel().uiState.value
            TeamDetailTab.entries.forEach { tab -> assertFalse(state.loadFailed(tab), "$tab") }
        }

    @Test
    fun `cached data stays and the failed refresh goes to the message bus`() =
        runTest {
            cache(team = team, events = listOf(event), media = listOf(photo))
            refreshesThrow(UnknownHostException())
            val state = subscribedViewModel().uiState.value
            TeamDetailTab.entries.forEach { tab -> assertFalse(state.loadFailed(tab), "$tab") }
            bus.messages.test { assertEquals("Couldn't refresh team", awaitItem().text) }
        }

    @Test
    fun `a failed pull to refresh over cached data goes to the message bus`() =
        runTest {
            cache(team = team, events = listOf(event), media = listOf(photo))
            val viewModel = subscribedViewModel()
            refreshesThrow(UnknownHostException())
            viewModel.refreshTab(TeamDetailTab.EVENTS)
            bus.messages.test { assertEquals("Couldn't refresh team", awaitItem().text) }
        }

    @Test
    fun `a failed refresh over cached data offers Retry, which refreshes again`() =
        runTest {
            cache(team = team, events = listOf(event), media = listOf(photo))
            refreshesThrow(UnknownHostException())
            subscribedViewModel()
            bus.messages.test {
                val action = awaitItem().action
                assertEquals("Retry", action?.label)
                action!!.onPerform()
                coVerify(exactly = 2) { teams.refreshTeam(any()) }
                coVerify(exactly = 2) { events.refreshTeamEvents(any(), any()) }
                assertEquals("Couldn't refresh team", awaitItem().text)
            }
        }

    @Test
    fun `a failed pull to refresh over cached data offers Retry`() =
        runTest {
            cache(team = team, events = listOf(event), media = listOf(photo))
            val viewModel = subscribedViewModel()
            refreshesThrow(UnknownHostException())
            viewModel.refreshTab(TeamDetailTab.EVENTS)
            bus.messages.test {
                val action = awaitItem().action
                assertEquals("Retry", action?.label)
                action!!.onPerform()
                coVerify(exactly = 3) { events.refreshTeamEvents(any(), any()) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `retrying any tab with no team cached retries every tab`() =
        runTest {
            refreshesThrow(UnknownHostException())
            val viewModel = subscribedViewModel()
            coEvery { teams.refreshTeam(any()) } returns Unit
            coEvery { events.refreshTeamEvents(any(), any()) } returns Unit
            coEvery { teams.refreshTeamMedia(any(), any()) } returns Unit
            viewModel.retry(TeamDetailTab.EVENTS)
            val state = viewModel.uiState.value
            TeamDetailTab.entries.forEach { tab -> assertFalse(state.loadFailed(tab), "$tab") }
        }

    @Test
    fun `a slow failure for a season the user left doesn't fail the new season`() =
        runTest {
            val slow = CompletableDeferred<Unit>()
            coEvery { events.refreshTeamEvents(any(), 2023) } coAnswers {
                slow.await()
                throw UnknownHostException()
            }
            val viewModel = subscribedViewModel()
            viewModel.selectYear(2023)
            viewModel.selectYear(2022)
            slow.complete(Unit)
            assertFalse(viewModel.uiState.value.loadFailed(TeamDetailTab.EVENTS))
        }

    @Test
    fun `a left season's late failure doesn't report over the new season`() =
        runTest {
            cache(team = team, events = listOf(event), media = listOf(photo))
            val slow = CompletableDeferred<Unit>()
            coEvery { events.refreshTeamEvents(any(), 2023) } throws UnknownHostException()
            coEvery { teams.refreshTeamMedia(any(), 2023) } coAnswers { slow.await() }
            val viewModel = subscribedViewModel()
            viewModel.selectYear(2023)
            viewModel.selectYear(2022)
            bus.messages.test {
                slow.complete(Unit)
                expectNoEvents()
            }
        }

    @Test
    fun `a late failure from an earlier visit to the same season is ignored`() =
        runTest {
            val slow = CompletableDeferred<Unit>()
            var calls = 0
            coEvery { events.refreshTeamEvents(any(), 2023) } coAnswers {
                if (calls++ == 0) {
                    slow.await()
                    throw UnknownHostException()
                }
            }
            val viewModel = subscribedViewModel()
            viewModel.selectYear(2023)
            viewModel.selectYear(2022)
            viewModel.selectYear(2023)
            slow.complete(Unit)
            assertFalse(viewModel.uiState.value.loadFailed(TeamDetailTab.EVENTS))
        }
}
