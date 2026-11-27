package com.thebluealliance.android.ui.matches

import android.util.Log
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.domain.model.CompLevel
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.navigation.Screen
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
class MatchDetailViewModelLoadTest {
    private val matches: MatchRepository = mockk(relaxed = true)
    private val events: EventRepository = mockk(relaxed = true)
    private val match =
        Match(
            key = "2025notreal_qm1",
            eventKey = "2025notreal",
            compLevel = CompLevel.QUAL,
            matchNumber = 1,
            setNumber = 1,
            time = null,
            predictedTime = null,
            actualTime = null,
            redTeamKeys = emptyList(),
            redScore = -1,
            blueTeamKeys = emptyList(),
            blueScore = -1,
            winningAlliance = null,
        )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        every { matches.observeMatch(any()) } returns flowOf(null)
        every { events.observeEvent(any()) } returns flowOf(null)
        every { events.observeEventAlliances(any()) } returns flowOf(emptyList())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    private fun TestScope.subscribedViewModel(): MatchDetailViewModel {
        val viewModel =
            MatchDetailViewModel(
                navKey = Screen.MatchDetail("2025notreal_qm1"),
                matchRepository = matches,
                eventRepository = events,
            )
        backgroundScope.launch(
            UnconfinedTestDispatcher(testScheduler),
        ) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun `a missing match shows not found`() =
        runTest {
            coEvery { matches.refreshMatch(any()) } throws httpError(404)
            assertTrue(subscribedViewModel().uiState.value.notFound)
        }

    // Retry won't change TBA's answer.
    @Test
    fun `a missing match is not a load error`() =
        runTest {
            coEvery { matches.refreshMatch(any()) } throws httpError(404)
            assertFalse(subscribedViewModel().uiState.value.loadFailed)
        }

    @Test
    fun `a cached match that is now missing keeps its cache`() =
        runTest {
            every { matches.observeMatch(any()) } returns flowOf(match)
            coEvery { matches.refreshMatch(any()) } throws httpError(404)
            val state = subscribedViewModel().uiState.value
            assertFalse(state.notFound)
            assertFalse(state.loadFailed)
        }

    @Test
    fun `an offline match is a load error, not missing`() =
        runTest {
            coEvery { matches.refreshMatch(any()) } throws UnknownHostException()
            val state = subscribedViewModel().uiState.value
            assertTrue(state.loadFailed)
            assertFalse(state.notFound)
        }

    @Test
    fun `a server error is a load error, not missing`() =
        runTest {
            coEvery { matches.refreshMatch(any()) } throws httpError(503)
            val state = subscribedViewModel().uiState.value
            assertTrue(state.loadFailed)
            assertFalse(state.notFound)
        }
}
