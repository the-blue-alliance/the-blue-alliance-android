package com.thebluealliance.android.ui.events

import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.navigation.Screen
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import java.time.Year

@OptIn(ExperimentalCoroutinesApi::class)
class EventsViewModelSeasonTest {
    private val seasons: SeasonRepository = mockk()
    private val auth: AuthRepository = mockk()
    private val events: EventRepository = mockk(relaxed = true)

    private fun viewModel(year: Int) = viewModel(Screen.Events(year))

    private fun viewModel(navKey: Screen.Events) =
        EventsViewModel(
            navKey = navKey,
            eventRepository = events,
            districtRepository = mockk(relaxed = true),
            seasons = seasons,
            myTBARepository = mockk(relaxed = true),
            authRepository = auth,
        )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { auth.isSignedIn() } returns false
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `picker reaches the shared max season`() {
        coEvery { seasons.maxSeason() } returns 2027
        assertEquals(2027, viewModel(2019).maxYear.value)
    }

    @Test
    fun `opens on the year it was given`() {
        coEvery { seasons.maxSeason() } returns 2027
        assertEquals(2019, viewModel(2019).selectedYear.value)
    }

    @Test
    fun `keeps the calendar-year fallback without a status`() {
        coEvery { seasons.maxSeason() } returns null
        assertEquals(
            java.time.Year
                .now()
                .value,
            viewModel(2019).maxYear.value,
        )
    }

    @Test
    fun `opens on TBA's current season when no year is given`() {
        val currentSeason = Year.now().value + 1
        coEvery { seasons.maxSeason() } returns currentSeason
        coEvery { seasons.defaultSeason() } returns currentSeason
        val viewModel = viewModel(Screen.Events())
        assertEquals(currentSeason, viewModel.selectedYear.value)
        coVerify { events.refreshEventsForYear(currentSeason) }
    }

    @Test
    fun `a late current season doesn't override the user's pick`() {
        val currentSeason = CompletableDeferred<Int>()
        coEvery { seasons.maxSeason() } returns 2027
        coEvery { seasons.defaultSeason() } coAnswers { currentSeason.await() }
        val viewModel = viewModel(Screen.Events())
        viewModel.selectYear(2019)
        currentSeason.complete(2027)
        assertEquals(2019, viewModel.selectedYear.value)
    }

    @Test
    fun `resolves the opening season before asking for the max season`() {
        val currentSeason = CompletableDeferred<Int>()
        coEvery { seasons.maxSeason() } returns 2027
        coEvery { seasons.defaultSeason() } coAnswers { currentSeason.await() }
        viewModel(Screen.Events())
        coVerify(exactly = 0) { seasons.maxSeason() }
        currentSeason.complete(2026)
        coVerify { events.refreshEventsForYear(2026) }
        coVerify { seasons.maxSeason() }
    }
}
