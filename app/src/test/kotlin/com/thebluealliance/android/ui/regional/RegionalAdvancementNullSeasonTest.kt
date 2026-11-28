package com.thebluealliance.android.ui.regional

import android.util.Log
import com.thebluealliance.android.core.network.TbaClientFactory
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UiState
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegionalAdvancementNullSeasonTest {
    private val server = MockWebServer()
    private val seasons: SeasonRepository = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        server.start()
        coEvery { seasons.maxSeason() } returns 2027
        coEvery { seasons.defaultSeason() } returns 2027
    }

    @AfterEach
    fun tearDown() {
        server.close()
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun answerEvery(response: MockResponse) {
        server.dispatcher =
            object : Dispatcher() {
                override fun dispatch(request: RecordedRequest) = response
            }
    }

    private fun settledState(): UiState<*> {
        val api =
            TbaClientFactory
                .retrofit(server.url("/").toString(), OkHttpClient())
                .create(TbaApi::class.java)
        val viewModel =
            RegionalAdvancementViewModel(
                Screen.RegionalAdvancement(),
                RegionalAdvancementRepository(api, mockk()),
                seasons,
                UserMessageBus(),
            )
        return runBlocking {
            withTimeout(
                5_000,
            ) { viewModel.uiState.first { it != UiState.Loading } }
        }
    }

    @Test
    fun `a season the API answers with null shows the empty state`() {
        answerEvery(MockResponse(code = 200, body = "null\n"))
        assertEquals(UiState.Empty, settledState())
    }

    @Test
    fun `a server error still shows the error state`() {
        answerEvery(MockResponse(code = 500, body = ""))
        assertTrue(settledState() is UiState.Error)
    }
}
