package com.thebluealliance.android.ui.events.detail

import android.util.Log
import app.cash.turbine.test
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelMessageTest {
    private val myTBA: MyTBARepository = mockk(relaxed = true)
    private val auth: AuthRepository = mockk(relaxed = true)
    private val bus = UserMessageBus()

    private fun viewModel() =
        EventDetailViewModel(
            navKey = Screen.EventDetail("2025casj"),
            eventRepository = mockk(relaxed = true),
            teamRepository = mockk(relaxed = true),
            matchRepository = mockk(relaxed = true),
            districtRepository = mockk(relaxed = true),
            regionalAdvancementRepository = mockk(relaxed = true),
            myTBARepository = myTBA,
            authRepository = auth,
            shortcutManager = mockk(relaxed = true),
            userMessages = bus,
        )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        // Relaxed repositories make some initial refreshes fail, which RefreshableViewModel logs.
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        every { auth.isSignedIn() } returns true
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `a failed favorite toggle goes to the message bus`() =
        runTest {
            coEvery { myTBA.addFavorite(any(), any()) } throws RuntimeException()
            viewModel().toggleFavorite()
            bus.messages.test { assertEquals("Couldn't update favorite", awaitItem().text) }
        }

    @Test
    fun `a failed preferences save goes to the message bus`() =
        runTest {
            coEvery { myTBA.updatePreferences(any(), any(), any(), any()) } throws
                RuntimeException()
            viewModel().updatePreferences(favorite = true, notifications = emptyList())
            bus.messages.test {
                assertEquals("Failed to save notification preferences", awaitItem().text)
            }
        }
}
