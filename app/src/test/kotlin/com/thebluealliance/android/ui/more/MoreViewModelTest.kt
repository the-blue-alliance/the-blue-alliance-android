package com.thebluealliance.android.ui.more

import com.thebluealliance.android.data.repository.SponsorRepository
import com.thebluealliance.android.domain.model.PlatinumSponsor
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoreViewModelTest {
    private val andyMark =
        PlatinumSponsor("AndyMark", "https://www.andymark.com/", "https://x/am.png", null)
    private val repository: SponsorRepository = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repository.sponsors } returns flowOf(listOf(andyMark))
        coEvery { repository.refreshIfStale() } returns Unit
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshes sponsors when created`() {
        MoreViewModel(repository)
        coVerify(exactly = 1) { repository.refreshIfStale() }
    }

    @Test
    fun `exposes the repository's sponsors`() =
        runTest {
            val viewModel = MoreViewModel(repository)
            assertEquals(listOf(andyMark), viewModel.sponsors.first { it.isNotEmpty() })
        }
}
