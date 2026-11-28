package com.thebluealliance.android.ui.teams

import android.util.Log
import app.cash.turbine.test
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Team
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
class TeamsViewModelRetryTest {
    private val teams: TeamRepository = mockk(relaxed = true)
    private val bus = UserMessageBus()
    private val team = Team("frc254", 254, null, "The Cheesy Poofs", null, null, null, 1999)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        every { teams.observeAllTeams() } returns flowOf(listOf(team))
        coEvery { teams.refreshAllTeams() } throws UnknownHostException()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun viewModel() =
        TeamsViewModel(
            teamRepository = teams,
            myTBARepository = mockk { every { observeFavorites() } returns flowOf(emptyList()) },
            authRepository = mockk { every { isSignedIn() } returns false },
            userMessages = bus,
        )

    @Test
    fun `a failed refresh over cached teams offers Retry, which refreshes again`() =
        runTest {
            viewModel()
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Couldn't refresh teams", message.text)
                assertEquals("Retry", message.action?.label)
                message.action!!.onPerform()
                coVerify(exactly = 2) { teams.refreshAllTeams() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `a failed refresh with nothing cached leaves it to the error screen`() =
        runTest {
            every { teams.observeAllTeams() } returns flowOf(emptyList())
            viewModel()
            bus.messages.test { expectNoEvents() }
        }
}
