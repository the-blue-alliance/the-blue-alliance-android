package com.thebluealliance.android.ui.teamevent

import android.util.Log
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.UserMessageBus
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
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamEventDetailViewModelKeyTest {
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
    fun `an event key shorter than a year opens without crashing`() {
        assertDoesNotThrow {
            TeamEventDetailViewModel(
                navKey = Screen.TeamEventDetail("frc2471", "ab"),
                teamRepository = mockk(relaxed = true),
                eventRepository = mockk(relaxed = true),
                matchRepository = mockk(relaxed = true),
                regionalAdvancementRepository = mockk(relaxed = true),
                userMessages = UserMessageBus(),
            )
        }
    }
}
