package com.thebluealliance.android.ui.regional

import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.data.repository.SeasonRepository
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
        val viewModel = RegionalAdvancementViewModel(repository, seasons)
        assertEquals(2025, viewModel.selectedYear.value)
        assertEquals(2027, viewModel.maxYear.value)
        coVerify { repository.getRegionalRankings(2025) }
    }

    @Test
    fun `a late default doesn't override the user's pick`() {
        val default = CompletableDeferred<Int>()
        coEvery { seasons.defaultSeason() } coAnswers { default.await() }
        val viewModel = RegionalAdvancementViewModel(repository, seasons)
        viewModel.selectYear(2019)
        default.complete(2025)
        assertEquals(2019, viewModel.selectedYear.value)
    }
}
