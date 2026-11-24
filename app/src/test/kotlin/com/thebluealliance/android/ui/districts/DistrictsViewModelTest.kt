package com.thebluealliance.android.ui.districts

import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.ApiStatusDto
import com.thebluealliance.android.data.repository.DistrictRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DistrictsViewModelTest {
    private val repository: DistrictRepository = mockk()
    private val api: TbaApi = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repository.observeDistrictsForYear(any()) } returns flowOf(emptyList())
        coEvery { repository.refreshDistrictsForYear(any()) } returns Unit
        coEvery { api.getStatus() } returns ApiStatusDto(currentSeason = 2026, maxSeason = 2027)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `opens on the current season, not the max season`() {
        val viewModel = DistrictsViewModel(repository, api)
        assertEquals(2026, viewModel.selectedYear.value)
        coVerify { repository.refreshDistrictsForYear(2026) }
    }

    @Test
    fun `picker reaches the max season`() {
        val viewModel = DistrictsViewModel(repository, api)
        assertEquals(2027, viewModel.maxYear.value)
    }

    @Test
    fun `refreshes the selected year`() {
        val viewModel = DistrictsViewModel(repository, api)
        viewModel.selectYear(2025)
        assertEquals(2025, viewModel.selectedYear.value)
        coVerify { repository.refreshDistrictsForYear(2025) }
    }
}
