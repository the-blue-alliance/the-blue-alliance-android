package com.thebluealliance.android.ui.more

import android.util.Log
import com.thebluealliance.android.data.remote.GitHubApi
import com.thebluealliance.android.ui.common.ResourceQuantityStrings
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class ThanksTest {
    private val strings = ResourceQuantityStrings

    private val gitHubApi: GitHubApi = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `one contribution is singular`() {
        assertEquals("1 contribution", contributionsLabel(1, strings))
    }

    @Test
    fun `other counts are plural`() {
        assertEquals("0 contributions", contributionsLabel(0, strings))
        assertEquals("2 contributions", contributionsLabel(2, strings))
    }

    @Test
    fun `a failed load shows a friendly message, not the exception`() {
        coEvery { gitHubApi.getContributors(any(), any(), any()) } throws
            UnknownHostException("Unable to resolve host \"api.github.com\"")

        val viewModel = ThanksViewModel(gitHubApi)

        assertEquals(ThanksUiState.Error("Couldn't load contributors"), viewModel.uiState.value)
    }
}
