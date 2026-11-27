package com.thebluealliance.android.ui.events

import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.navigation.Screen
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventsViewModelSeasonTest {
    private val seasons: SeasonRepository = mockk()
    private val auth: AuthRepository = mockk()

    private fun viewModel(year: Int) =
        EventsViewModel(
            navKey = Screen.Events(year),
            eventRepository = mockk(relaxed = true),
            districtRepository = mockk(relaxed = true),
            seasons = seasons,
            myTBARepository = mockk(relaxed = true),
            authRepository = auth,
            liveWebcastRepository = mockk(relaxed = true),
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
}
