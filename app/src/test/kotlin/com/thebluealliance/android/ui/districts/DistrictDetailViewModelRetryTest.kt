package com.thebluealliance.android.ui.districts

import android.util.Log
import app.cash.turbine.test
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class DistrictDetailViewModelRetryTest {
    private val districts: DistrictRepository = mockk(relaxed = true)
    private val events: EventRepository = mockk(relaxed = true)
    private val bus = UserMessageBus()
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

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        every { districts.observeDistrict(any()) } returns flowOf(null)
        every { districts.observeDistrictRankings(any()) } returns flowOf(emptyList())
        every { events.observeDistrictEvents(any()) } returns flowOf(listOf(event))
        coEvery { districts.getDistrictHistory(any()) } returns listOf(2024)
        coEvery { events.refreshDistrictEvents(any()) } throws UnknownHostException()
        coEvery { districts.refreshDistrictRankings(any()) } throws UnknownHostException()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `a failed refresh over cached data offers Retry, which refreshes again`() =
        runTest {
            DistrictDetailViewModel(Screen.DistrictDetail("2024fim"), districts, events, bus)
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Couldn't refresh district", message.text)
                assertEquals("Retry", message.action?.label)
                message.action!!.onPerform()
                coVerify(exactly = 2) { events.refreshDistrictEvents("2024fim") }
                coVerify(exactly = 2) { districts.refreshDistrictRankings("2024fim") }
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun TestScope.subscribedViewModel(): DistrictDetailViewModel {
        val viewModel =
            DistrictDetailViewModel(Screen.DistrictDetail("2024fim"), districts, events, bus)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        return viewModel
    }

    @Test
    fun `an uncached district that fails to load shows errors, not a spinner`() =
        runTest {
            every { events.observeDistrictEvents(any()) } returns flowOf(emptyList())
            val state = subscribedViewModel().uiState.value
            assertTrue(state.loadFailed(DistrictDetailTab.EVENTS))
            assertTrue(state.loadFailed(DistrictDetailTab.RANKINGS))
        }

    @Test
    fun `a district with no events says so once loaded`() =
        runTest {
            every { events.observeDistrictEvents(any()) } returns flowOf(emptyList())
            coEvery { events.refreshDistrictEvents(any()) } returns Unit
            val state = subscribedViewModel().uiState.value
            assertFalse(state.loadFailed(DistrictDetailTab.EVENTS))
            assertEquals(emptyList<Any>(), state.eventSections)
        }

    @Test
    fun `retrying a failed tab loads it`() =
        runTest {
            every { districts.observeDistrictRankings(any()) } returns flowOf(emptyList())
            val viewModel = subscribedViewModel()
            coEvery { districts.refreshDistrictRankings(any()) } returns Unit
            viewModel.refreshTab(DistrictDetailTab.RANKINGS)
            assertFalse(viewModel.uiState.value.loadFailed(DistrictDetailTab.RANKINGS))
        }
}
