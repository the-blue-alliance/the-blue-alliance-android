package com.thebluealliance.android.ui.regional

import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UiState
import io.mockk.coEvery
import io.mockk.coVerify
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
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class RegionalAdvancementViewModelTest {
    private val repository: RegionalAdvancementRepository = mockk()
    private val seasons: SeasonRepository = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        coEvery { repository.getRegionalRankings(any()) } returns emptyList()
        coEvery { seasons.maxSeason() } returns 2027
        coEvery { seasons.defaultSeason() } returns 2025
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `opens on the default season, not the max season`() {
        val viewModel =
            RegionalAdvancementViewModel(Screen.RegionalAdvancement(), repository, seasons)
        assertEquals(2025, viewModel.selectedYear.value)
        assertEquals(2027, viewModel.maxYear.value)
        coVerify { repository.getRegionalRankings(2025) }
    }

    @Test
    fun `a late default doesn't override the user's pick`() {
        val default = CompletableDeferred<Int>()
        coEvery { seasons.defaultSeason() } coAnswers { default.await() }
        val viewModel =
            RegionalAdvancementViewModel(Screen.RegionalAdvancement(), repository, seasons)
        viewModel.selectYear(2019)
        default.complete(2025)
        assertEquals(2019, viewModel.selectedYear.value)
    }

    @Test
    fun `a slower load for the previous year doesn't replace the selected year's`() {
        val launchYear = Calendar.getInstance().get(Calendar.YEAR)
        val stale = CompletableDeferred<List<RegionalRanking>>()
        coEvery { repository.getRegionalRankings(launchYear) } coAnswers { stale.await() }
        val default = CompletableDeferred<Int>()
        coEvery { seasons.defaultSeason() } coAnswers { default.await() }
        val viewModel =
            RegionalAdvancementViewModel(Screen.RegionalAdvancement(), repository, seasons)
        default.complete(launchYear + 1)
        stale.complete(listOf(mockk(relaxed = true)))
        assertEquals(UiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `switching year stops the spinner without waiting for the abandoned load`() {
        val stale = CompletableDeferred<List<RegionalRanking>>()
        coEvery { repository.getRegionalRankings(2025) } coAnswers { stale.await() }
        val viewModel =
            RegionalAdvancementViewModel(Screen.RegionalAdvancement(), repository, seasons)
        assertEquals(true, viewModel.isRefreshing.value)
        viewModel.selectYear(2019)
        assertEquals(false, viewModel.isRefreshing.value)
        assertEquals(UiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `opens on the year from the link`() {
        assertEquals(2019, linkedViewModel(2019).selectedYear.value)
    }

    @Test
    fun `loads only the year from the link`() {
        linkedViewModel(2019)
        coVerify(exactly = 1) { repository.getRegionalRankings(2019) }
        coVerify(exactly = 1) { repository.getRegionalRankings(any()) }
    }

    @Test
    fun `a late default doesn't override the year from the link`() {
        val default = CompletableDeferred<Int>()
        coEvery { seasons.defaultSeason() } coAnswers { default.await() }
        val viewModel = linkedViewModel(2019)
        default.complete(2025)
        assertEquals(2019, viewModel.selectedYear.value)
    }

    private fun linkedViewModel(year: Int) =
        RegionalAdvancementViewModel(Screen.RegionalAdvancement(year), repository, seasons)
}
