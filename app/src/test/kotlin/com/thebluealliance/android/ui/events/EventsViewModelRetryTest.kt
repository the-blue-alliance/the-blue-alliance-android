package com.thebluealliance.android.ui.events

import android.util.Log
import app.cash.turbine.test
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.SeasonRepository
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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class EventsViewModelRetryTest {
    private val events: EventRepository = mockk(relaxed = true)
    private val seasons: SeasonRepository = mockk { coEvery { maxSeason() } returns 2024 }
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
        every { events.observeEventsForYear(any()) } returns flowOf(listOf(event))
        coEvery { events.refreshEventsForYear(any()) } throws UnknownHostException()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun viewModel() =
        EventsViewModel(
            navKey = Screen.Events(2024),
            eventRepository = events,
            districtRepository =
                mockk(relaxed = true) {
                    every { observeDistrictsForYear(any()) } returns flowOf(emptyList())
                },
            seasons = seasons,
            myTBARepository = mockk { every { observeFavorites() } returns flowOf(emptyList()) },
            authRepository = mockk { every { isSignedIn() } returns false },
            userMessages = bus,
        )

    @Test
    fun `a failed refresh over cached events offers Retry, which refreshes again`() =
        runTest {
            viewModel()
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Couldn't refresh events", message.text)
                assertEquals("Retry", message.action?.label)
                message.action!!.onPerform()
                coVerify(exactly = 2) { events.refreshEventsForYear(2024) }
                cancelAndIgnoreRemainingEvents()
            }
        }
}
