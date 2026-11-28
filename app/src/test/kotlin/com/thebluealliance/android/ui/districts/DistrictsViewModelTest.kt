package com.thebluealliance.android.ui.districts

import android.util.Log
import app.cash.turbine.test
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CompletableDeferred
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
class DistrictsViewModelTest {
    private val repository: DistrictRepository = mockk()
    private val seasons: SeasonRepository = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repository.observeDistrictsForYear(any()) } returns flowOf(emptyList())
        coEvery { repository.refreshDistrictsForYear(any()) } returns Unit
        coEvery { seasons.maxSeason() } returns 2027
        coEvery { seasons.defaultSeason() } returns 2025
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `opens on the default season, not the max season`() {
        val viewModel = DistrictsViewModel(repository, seasons, UserMessageBus())
        assertEquals(2025, viewModel.selectedYear.value)
        coVerify { repository.refreshDistrictsForYear(2025) }
    }

    @Test
    fun `picker reaches the max season`() {
        val viewModel = DistrictsViewModel(repository, seasons, UserMessageBus())
        assertEquals(2027, viewModel.maxYear.value)
    }

    @Test
    fun `refreshes the selected year`() {
        val viewModel = DistrictsViewModel(repository, seasons, UserMessageBus())
        viewModel.selectYear(2019)
        assertEquals(2019, viewModel.selectedYear.value)
        coVerify { repository.refreshDistrictsForYear(2019) }
    }

    @Test
    fun `a late default doesn't override the user's pick`() {
        val default = CompletableDeferred<Int>()
        coEvery { seasons.defaultSeason() } coAnswers { default.await() }
        val viewModel = DistrictsViewModel(repository, seasons, UserMessageBus())
        viewModel.selectYear(2019)
        default.complete(2025)
        assertEquals(2019, viewModel.selectedYear.value)
    }

    @Test
    fun `a failed refresh over cached districts offers Retry, which refreshes again`() =
        runTest {
            mockkStatic(Log::class)
            every { Log.w(any(), any<String>(), any()) } returns 0
            val bus = UserMessageBus()
            every { repository.observeDistrictsForYear(any()) } returns
                flowOf(listOf(District("2025fim", "fim", "FIRST In Michigan", 2025)))
            var refreshes = 0
            coEvery { repository.refreshDistrictsForYear(2025) } answers {
                refreshes++
                throw UnknownHostException()
            }
            DistrictsViewModel(repository, seasons, bus)
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Couldn't refresh districts", message.text)
                assertEquals("Retry", message.action?.label)
                val before = refreshes
                message.action!!.onPerform()
                assertEquals(before + 1, refreshes)
                cancelAndIgnoreRemainingEvents()
            }
            unmockkStatic(Log::class)
        }
}
