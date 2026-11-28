package com.thebluealliance.android.ui.teamevent

import android.util.Log
import app.cash.turbine.test
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Event
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
class TeamEventDetailViewModelRetryTest {
    private val teams: TeamRepository = mockk(relaxed = true)
    private val events: EventRepository = mockk(relaxed = true)
    private val matches: MatchRepository = mockk(relaxed = true)
    private val regional: RegionalAdvancementRepository = mockk(relaxed = true)
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

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        every { teams.observeTeam(any()) } returns flowOf(team)
        every { teams.observeTeamMedia(any(), any()) } returns flowOf(emptyList())
        every { teams.observeTeamEventPitLocation(any(), any()) } returns flowOf(null)
        every { events.observeEvent(any()) } returns flowOf(event)
        every { events.observeEventRankings(any()) } returns flowOf(emptyList())
        every { events.observeEventAwards(any()) } returns flowOf(emptyList())
        every { events.observeEventOPRs(any()) } returns flowOf(null)
        every { events.observeEventAlliances(any()) } returns flowOf(emptyList())
        every { events.observeEventDistrictPoints(any()) } returns flowOf(emptyList())
        every { events.observeEventRegionalPoints(any()) } returns flowOf(emptyList())
        every { matches.observeEventMatches(any()) } returns flowOf(emptyList())
        coEvery { regional.getCmpAdvancementByTeam(any(), any()) } returns emptyMap()
        coEvery { teams.refreshTeam(any()) } throws UnknownHostException()
        coEvery { events.refreshEvent(any()) } throws UnknownHostException()
        coEvery { matches.refreshEventMatches(any()) } throws UnknownHostException()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `a failed refresh over cached data offers Retry, which refreshes again`() =
        runTest {
            TeamEventDetailViewModel(
                navKey = Screen.TeamEventDetail("frc254", "2024casj"),
                teamRepository = teams,
                eventRepository = events,
                matchRepository = matches,
                regionalAdvancementRepository = regional,
                userMessages = bus,
            )
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Couldn't refresh team at event", message.text)
                assertEquals("Retry", message.action?.label)
                message.action!!.onPerform()
                coVerify(exactly = 2) { teams.refreshTeam("frc254") }
                coVerify(exactly = 2) { matches.refreshEventMatches("2024casj") }
                cancelAndIgnoreRemainingEvents()
            }
        }
}
