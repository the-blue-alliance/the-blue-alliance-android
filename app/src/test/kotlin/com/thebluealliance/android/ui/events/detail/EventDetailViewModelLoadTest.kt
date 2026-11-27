package com.thebluealliance.android.ui.events.detail

import android.util.Log
import app.cash.turbine.test
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.PlayoffType
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelLoadTest {
    private val eventKey = "2024casj"
    private val events: EventRepository = mockk(relaxed = true)
    private val teams: TeamRepository = mockk(relaxed = true)
    private val matches: MatchRepository = mockk(relaxed = true)
    private val districts: DistrictRepository = mockk(relaxed = true)
    private val regional: RegionalAdvancementRepository = mockk(relaxed = true)
    private val myTBA: MyTBARepository = mockk(relaxed = true)
    private val bus = UserMessageBus()

    private val event =
        Event(
            key = eventKey,
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
    private val award = Award(eventKey, 1, "frc254", null, "Regional Winner", 2024)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        cache(event = null, awards = emptyList())
        every { teams.observeEventTeamKeys(any()) } returns flowOf(emptyList())
        every { teams.observeTeams(any()) } returns flowOf(emptyList())
        every { matches.observeEventMatches(any()) } returns flowOf(emptyList())
        every { events.observeEventRankings(any()) } returns flowOf(emptyList())
        every { events.observeEventRankingSortOrders(any()) } returns flowOf(emptyList())
        every { events.observeEventRankingExtraStatsInfo(any()) } returns flowOf(emptyList())
        every { events.observeEventAlliances(any()) } returns flowOf(emptyList())
        every { events.observeEventDistrictPoints(any()) } returns flowOf(emptyList())
        every { events.observeEventRegionalPoints(any()) } returns flowOf(emptyList())
        every { events.observeEventOPRs(any()) } returns flowOf(null)
        every { events.observeEventCOPRs(any()) } returns flowOf(null)
        every { events.observeEventInsights(any()) } returns flowOf(null)
        every { events.observeEventPitLocations(any()) } returns flowOf(emptyMap())
        every { districts.observeDistrict(any()) } returns flowOf(null)
        every { myTBA.observeFavorites() } returns flowOf(emptyList())
        every { myTBA.isFavorite(any(), any()) } returns flowOf(false)
        every { myTBA.observeSubscription(any(), any()) } returns flowOf(null)
        coEvery { regional.getCmpAdvancementByTeam(any(), any()) } returns emptyMap()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun cache(
        event: Event?,
        awards: List<Award>,
    ) {
        every { events.observeEvent(any()) } returns flowOf(event)
        every { events.observeEventAwards(any()) } returns flowOf(awards)
    }

    private fun refreshesThrow(error: Exception) {
        coEvery { events.refreshEvent(any()) } throws error
        coEvery { events.refreshEventRankings(any()) } throws error
        coEvery { events.refreshEventAlliances(any()) } throws error
        coEvery { events.refreshEventAwards(any()) } throws error
        coEvery { events.refreshEventDistrictPoints(any()) } throws error
        coEvery { events.refreshEventRegionalPoints(any()) } throws error
        coEvery { events.refreshEventOPRs(any()) } throws error
        coEvery { events.refreshEventCOPRs(any()) } throws error
        coEvery { events.refreshEventInsights(any()) } throws error
        coEvery { events.refreshEventPitLocations(any()) } throws error
        coEvery { teams.refreshEventTeams(any()) } throws error
        coEvery { matches.refreshEventMatches(any()) } throws error
        coEvery { districts.refreshDistrictsForYear(any()) } throws error
    }

    private fun notFound() = HttpException(Response.error<Any>(404, "".toResponseBody()))

    private fun TestScope.subscribedViewModel(): EventDetailViewModel {
        val viewModel =
            EventDetailViewModel(
                navKey = Screen.EventDetail(eventKey),
                eventRepository = events,
                teamRepository = teams,
                matchRepository = matches,
                districtRepository = districts,
                regionalAdvancementRepository = regional,
                myTBARepository = myTBA,
                authRepository = mockk(relaxed = true),
                shortcutManager = mockk(relaxed = true),
                userMessages = bus,
            )
        backgroundScope.launch(
            UnconfinedTestDispatcher(testScheduler),
        ) { viewModel.uiState.collect {} }
        return viewModel
    }

    @Test
    fun `every tab shows a load error when its fetch fails and nothing is cached`() =
        runTest {
            refreshesThrow(UnknownHostException())
            val state = subscribedViewModel().uiState.value
            EventDetailTab.entries.forEach { tab ->
                assertTrue(state.loadFailed(tab), "$tab should show a load error")
            }
        }

    @Test
    fun `a fetch that succeeds with no data is empty, not an error`() =
        runTest {
            val state = subscribedViewModel().uiState.value
            EventDetailTab.entries.forEach { tab -> assertFalse(state.loadFailed(tab), "$tab") }
        }

    @Test
    fun `an endpoint with no data for the event is empty, not an error`() =
        runTest {
            refreshesThrow(KotlinNullPointerException("Response body was null"))
            val state = subscribedViewModel().uiState.value
            EventDetailTab.entries.forEach { tab -> assertFalse(state.loadFailed(tab), "$tab") }
        }

    @Test
    fun `cached data stays and the failed refresh goes to the message bus`() =
        runTest {
            cache(event = event, awards = listOf(award))
            refreshesThrow(UnknownHostException())
            val viewModel = subscribedViewModel()
            val state = viewModel.uiState.value
            assertFalse(state.loadFailed(EventDetailTab.INFO))
            assertFalse(state.loadFailed(EventDetailTab.AWARDS))
            bus.messages.test { assertEquals("Couldn't refresh event", awaitItem()) }
        }

    @Test
    fun `a failed pull to refresh over cached data goes to the message bus`() =
        runTest {
            cache(event = event, awards = listOf(award))
            val viewModel = subscribedViewModel()
            refreshesThrow(UnknownHostException())
            viewModel.refreshTab(EventDetailTab.AWARDS)
            bus.messages.test { assertEquals("Couldn't refresh event", awaitItem()) }
        }

    @Test
    fun `the team count is hidden while no teams have loaded`() =
        runTest {
            refreshesThrow(UnknownHostException())
            assertNull(subscribedViewModel().uiState.value.teamCount)
        }

    @Test
    fun `the team count is zero once the fetch says there are none`() =
        runTest {
            assertEquals(0, subscribedViewModel().uiState.value.teamCount)
        }

    @Test
    fun `an HTTP error other than 404 is a load error`() =
        runTest {
            refreshesThrow(HttpException(Response.error<Any>(403, "".toResponseBody())))
            val state = subscribedViewModel().uiState.value
            EventDetailTab.entries.forEach { tab -> assertTrue(state.loadFailed(tab), "$tab") }
        }

    @Test
    fun `retrying any tab with no event cached retries every tab`() =
        runTest {
            refreshesThrow(UnknownHostException())
            val viewModel = subscribedViewModel()
            refreshesSucceed()
            viewModel.retry(EventDetailTab.AWARDS)
            val state = viewModel.uiState.value
            EventDetailTab.entries.forEach { tab -> assertFalse(state.loadFailed(tab), "$tab") }
        }

    private fun refreshesSucceed() {
        coEvery { events.refreshEvent(any()) } returns Unit
        coEvery { events.refreshEventRankings(any()) } returns Unit
        coEvery { events.refreshEventAlliances(any()) } returns Unit
        coEvery { events.refreshEventAwards(any()) } returns Unit
        coEvery { events.refreshEventDistrictPoints(any()) } returns Unit
        coEvery { events.refreshEventRegionalPoints(any()) } returns Unit
        coEvery { events.refreshEventOPRs(any()) } returns Unit
        coEvery { events.refreshEventCOPRs(any()) } returns Unit
        coEvery { events.refreshEventInsights(any()) } returns Unit
        coEvery { events.refreshEventPitLocations(any()) } returns Unit
        coEvery { teams.refreshEventTeams(any()) } returns Unit
        coEvery { matches.refreshEventMatches(any()) } returns Unit
        coEvery { districts.refreshDistrictsForYear(any()) } returns Unit
    }

    @Test
    fun `a missing event shows not found`() =
        runTest {
            refreshesThrow(notFound())
            assertTrue(subscribedViewModel().uiState.value.notFound)
        }

    @Test
    fun `a missing event is not a load error`() =
        runTest {
            refreshesThrow(notFound())
            val state = subscribedViewModel().uiState.value
            EventDetailTab.entries.forEach { tab -> assertFalse(state.loadFailed(tab), "$tab") }
        }

    @Test
    fun `a cached event that is now missing keeps its cache`() =
        runTest {
            cache(event = event, awards = listOf(award))
            refreshesThrow(notFound())
            assertFalse(subscribedViewModel().uiState.value.notFound)
        }

    @Test
    fun `an event that can't be fetched offline is not missing`() =
        runTest {
            refreshesThrow(UnknownHostException())
            assertFalse(subscribedViewModel().uiState.value.notFound)
        }

    @Test
    fun `a tab with no data for a found event is not a missing event`() =
        runTest {
            coEvery { events.refreshEventRankings(any()) } throws notFound()
            assertFalse(subscribedViewModel().uiState.value.notFound)
        }
}
