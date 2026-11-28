package com.thebluealliance.android.ui.mytba

import android.util.Log
import app.cash.turbine.test
import com.google.firebase.auth.FirebaseUser
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.data.repository.MatchRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.data.repository.MyTBAServerException
import com.thebluealliance.android.data.repository.TeamRepository
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.domain.model.Subscription
import com.thebluealliance.android.messaging.DeviceRegistrationManager
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.ui.common.UserMessageBus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class MyTBAViewModelTest {
    private val user: FirebaseUser =
        mockk {
            every { displayName } returns "Test User"
            every { email } returns null
            every { photoUrl } returns null
        }
    private val authRepository: AuthRepository =
        mockk {
            every { currentUser } returns flowOf(user)
            every { isSignedIn() } returns true
        }
    private val myTBARepository: MyTBARepository = mockk()
    private val bus = UserMessageBus()
    private val deviceRegistrationManager: DeviceRegistrationManager = mockk(relaxed = true)
    private val shortcutManager: TBAShortcutManager =
        mockk { every { canPinShortcuts() } returns false }
    private val teamRepository: TeamRepository =
        mockk { every { observeTeams(any()) } returns flowOf(emptyList()) }
    private val eventRepository: EventRepository =
        mockk { every { observeEvents(any()) } returns flowOf(emptyList()) }
    private val matchRepository: MatchRepository =
        mockk { every { observeMatches(any()) } returns flowOf(emptyList()) }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any()) } returns 0
        every { myTBARepository.observeSubscriptions() } returns flowOf(emptyList())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun viewModel() =
        MyTBAViewModel(
            authRepository,
            myTBARepository,
            deviceRegistrationManager,
            shortcutManager,
            teamRepository,
            eventRepository,
            matchRepository,
            bus,
        )

    @Test
    fun `failed fetches with nothing cached show errors, not empty lists`() =
        runTest {
            every { myTBARepository.observeFavorites() } returns flowOf(emptyList())
            coEvery { myTBARepository.refreshFavorites() } throws MyTBAServerException(401, "")
            coEvery { myTBARepository.refreshSubscriptions() } throws MyTBAServerException(401, "")

            val state = viewModel().uiState.first { it.isSignedIn }

            assertEquals("Couldn't load favorites", state.favoritesError)
            assertEquals("Couldn't load notifications", state.subscriptionsError)
        }

    @Test
    fun `successful empty fetches show empty lists`() =
        runTest {
            every { myTBARepository.observeFavorites() } returns flowOf(emptyList())
            coEvery { myTBARepository.refreshFavorites() } returns Unit
            coEvery { myTBARepository.refreshSubscriptions() } returns Unit

            val state = viewModel().uiState.first { it.isSignedIn }

            assertNull(state.favoritesError)
            assertNull(state.subscriptionsError)
        }

    @Test
    fun `failed fetch keeps showing cached favorites`() =
        runTest {
            val cached = listOf(Favorite(modelKey = "frc254", modelType = ModelType.TEAM))
            every { myTBARepository.observeFavorites() } returns flowOf(cached)
            coEvery { myTBARepository.refreshFavorites() } throws MyTBAServerException(401, "")
            coEvery { myTBARepository.refreshSubscriptions() } returns Unit

            val state = viewModel().uiState.first { it.isSignedIn }

            assertEquals(cached, state.favorites)
            assertNull(state.favoritesError)
        }

    @Test
    fun `a failed refresh over cached favorites offers Retry, which refreshes again`() =
        runTest {
            val cached = listOf(Favorite(modelKey = "frc254", modelType = ModelType.TEAM))
            every { myTBARepository.observeFavorites() } returns flowOf(cached)
            coEvery { myTBARepository.refreshFavorites() } throws UnknownHostException()
            coEvery { myTBARepository.refreshSubscriptions() } returns Unit
            viewModel().uiState.first { it.isSignedIn }
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Couldn't refresh myTBA", message.text)
                assertEquals("Retry", message.action?.label)
                message.action!!.onPerform()
                coVerify(exactly = 2) { myTBARepository.refreshFavorites() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `a failed refresh with nothing cached leaves it to the error screen`() =
        runTest {
            every { myTBARepository.observeFavorites() } returns flowOf(emptyList())
            coEvery { myTBARepository.refreshFavorites() } throws UnknownHostException()
            coEvery { myTBARepository.refreshSubscriptions() } throws UnknownHostException()
            viewModel().uiState.first { it.isSignedIn }
            bus.messages.test { expectNoEvents() }
        }

    @Test
    fun `failed notifications with only favorites cached leave it to the error screen`() =
        runTest {
            every { myTBARepository.observeFavorites() } returns
                flowOf(listOf(Favorite(modelKey = "frc254", modelType = ModelType.TEAM)))
            every { myTBARepository.observeSubscriptions() } returns flowOf(emptyList())
            coEvery { myTBARepository.refreshFavorites() } returns Unit
            coEvery { myTBARepository.refreshSubscriptions() } throws UnknownHostException()
            val viewModel = viewModel()
            viewModel.uiState.first { it.isSignedIn }
            viewModel.refreshTab(MyTBATab.NOTIFICATIONS)
            bus.messages.test { expectNoEvents() }
        }

    private val team = Favorite(modelKey = "frc254", modelType = ModelType.TEAM)

    private fun signedInWithNothingToRefresh() {
        every { myTBARepository.observeFavorites() } returns flowOf(listOf(team))
        coEvery { myTBARepository.refreshFavorites() } returns Unit
        coEvery { myTBARepository.refreshSubscriptions() } returns Unit
    }

    @Test
    fun `removing a favorite offers Undo, which adds it back`() =
        runTest {
            signedInWithNothingToRefresh()
            coEvery { myTBARepository.removeFavorite(any(), any()) } returns Unit
            coEvery { myTBARepository.addFavorite(any(), any()) } returns Unit
            viewModel().removeFavorite(team)
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Removed from favorites", message.text)
                assertEquals("Undo", message.action?.label)
                message.action!!.onPerform()
                coVerify { myTBARepository.addFavorite("frc254", ModelType.TEAM) }
            }
        }

    @Test
    fun `a failed favorite removal offers Retry, which removes it again`() =
        runTest {
            signedInWithNothingToRefresh()
            coEvery { myTBARepository.removeFavorite(any(), any()) } throws UnknownHostException()
            viewModel().removeFavorite(team)
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Couldn't remove favorite", message.text)
                assertEquals("Retry", message.action?.label)
                message.action!!.onPerform()
                coVerify(exactly = 2) { myTBARepository.removeFavorite("frc254", ModelType.TEAM) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `removing notifications offers Undo, which restores them`() =
        runTest {
            signedInWithNothingToRefresh()
            val subscription = Subscription("frc254", ModelType.TEAM, listOf("upcoming_match"))
            every { myTBARepository.observeSubscriptions() } returns flowOf(listOf(subscription))
            coEvery { myTBARepository.updatePreferences(any(), any(), any(), any()) } returns Unit
            val viewModel = viewModel()
            viewModel.uiState.first { it.subscriptions.isNotEmpty() }
            viewModel.removeSubscription("frc254", ModelType.TEAM)
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Removed from notifications", message.text)
                assertEquals("Undo", message.action?.label)
                message.action!!.onPerform()
                coVerify {
                    myTBARepository.updatePreferences(
                        "frc254",
                        ModelType.TEAM,
                        true,
                        listOf("upcoming_match"),
                    )
                }
            }
        }

    @Test
    fun `a failed notification removal offers Retry`() =
        runTest {
            signedInWithNothingToRefresh()
            coEvery { myTBARepository.updatePreferences(any(), any(), any(), any()) } throws
                UnknownHostException()
            viewModel().removeSubscription("frc254", ModelType.TEAM)
            bus.messages.test {
                val message = awaitItem()
                assertEquals("Couldn't remove notifications", message.text)
                assertEquals("Retry", message.action?.label)
                message.action!!.onPerform()
                coVerify(exactly = 2) {
                    myTBARepository.updatePreferences("frc254", ModelType.TEAM, any(), emptyList())
                }
                cancelAndIgnoreRemainingEvents()
            }
        }
}
