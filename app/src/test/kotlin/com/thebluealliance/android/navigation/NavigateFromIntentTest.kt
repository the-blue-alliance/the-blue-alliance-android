package com.thebluealliance.android.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.ui.events.detail.EventDetailTab
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NavigateFromIntentTest {
    private val tabs: List<NavKey> =
        listOf(Screen.Events(), Screen.Teams, Screen.Districts, Screen.More)

    private fun stateOn(
        tab: NavKey,
        startRoute: NavKey = Screen.Events(),
    ): NavigationState =
        NavigationState(
            startRoute = startRoute,
            topLevelRoute = mutableStateOf(tab),
            backStacks = tabs.associateWith { NavBackStack(it) },
        )

    private fun NavigationState.navigateFromIntent(
        backStack: List<NavKey>,
        matchBesideEvent: Boolean = false,
    ) = Navigator(this, mockk(relaxed = true)).navigateFromIntent(backStack, matchBesideEvent)

    private val matchLink =
        listOf(
            Screen.Events(),
            Screen.EventDetail("2025hop", initialTab = EventDetailTab.MATCHES),
            Screen.MatchDetail("2025hop_qm1"),
        )

    @Test
    fun `notification opens under events from another tab`() {
        val state = stateOn(Screen.More)
        state.navigateFromIntent(listOf(Screen.EventDetail("2025hop")))
        assertEquals(Screen.Events(), state.topLevelRoute)
        assertEquals(
            listOf(Screen.Events(), Screen.EventDetail("2025hop")),
            state.currentStack.toList(),
        )
        assertEquals(listOf<NavKey>(Screen.More), state.backStacks.getValue(Screen.More).toList())
    }

    @Test
    fun `deep link opens in its root tab`() {
        val state = stateOn(Screen.Districts)
        state.navigateFromIntent(listOf(Screen.Teams, Screen.TeamDetail("frc254")))
        assertEquals(Screen.Teams, state.topLevelRoute)
        assertEquals(listOf(Screen.Teams, Screen.TeamDetail("frc254")), state.currentStack.toList())
    }

    @Test
    fun `tab deep link switches to that tab`() {
        val state = stateOn(Screen.More)
        state.navigateFromIntent(listOf(Screen.Teams))
        assertEquals(Screen.Teams, state.topLevelRoute)
        assertEquals(listOf<NavKey>(Screen.Teams), state.currentStack.toList())
    }

    @Test
    fun `destination already on top is not pushed again`() {
        val state = stateOn(Screen.Events())
        state.currentStack.add(Screen.EventDetail("2025hop"))
        state.navigateFromIntent(listOf(Screen.EventDetail("2025hop")))
        assertEquals(
            listOf(Screen.Events(), Screen.EventDetail("2025hop")),
            state.currentStack.toList(),
        )
    }

    @Test
    fun `destination on top of another tab is reopened without a copy`() {
        val state = stateOn(Screen.More)
        state.backStacks.getValue(Screen.Events()).add(Screen.EventDetail("2025hop"))
        state.navigateFromIntent(listOf(Screen.EventDetail("2025hop")))
        assertEquals(Screen.Events(), state.topLevelRoute)
        assertEquals(
            listOf(Screen.Events(), Screen.EventDetail("2025hop")),
            state.currentStack.toList(),
        )
    }

    @Test
    fun `match link keeps its event under it`() {
        val state = stateOn(Screen.More)
        val link =
            listOf(
                Screen.Events(),
                Screen.EventDetail("2025casj", EventDetailTab.MATCHES),
                Screen.MatchDetail("2025casj_qm1"),
            )
        state.navigateFromIntent(link)
        assertEquals(Screen.Events(), state.topLevelRoute)
        assertEquals(link, state.currentStack.toList())
    }

    @Test
    fun `pit map link keeps its event under it`() {
        val state = stateOn(Screen.Teams)
        val link =
            listOf(Screen.Events(), Screen.EventDetail("2025casj"), Screen.EventPitMap("2025casj"))
        state.navigateFromIntent(link)
        assertEquals(link, state.currentStack.toList())
    }

    @Test
    fun `events link from another tab drops a stale detail`() {
        val state = stateOn(Screen.More)
        state.backStacks.getValue(Screen.Events()).add(Screen.EventDetail("2025hop"))
        state.navigateFromIntent(listOf(Screen.Events()))
        assertEquals(Screen.Events(), state.topLevelRoute)
        assertEquals(listOf<NavKey>(Screen.Events()), state.currentStack.toList())
    }

    @Test
    fun `events link keeps its year`() {
        val state = stateOn(Screen.Teams)
        state.navigateFromIntent(listOf(Screen.Events(2024)))
        assertEquals(Screen.Events(), state.topLevelRoute)
        assertEquals(listOf<NavKey>(Screen.Events(2024)), state.currentStack.toList())
    }

    @Test
    fun `back from a warm events year link returns to the start tab`() {
        val state = stateOn(Screen.Teams, startRoute = Screen.Teams)
        state.navigateFromIntent(listOf(Screen.Events(2024)))
        Navigator(state, mockk(relaxed = true)).goBack()
        assertEquals(Screen.Teams, state.topLevelRoute)
        assertEquals(
            listOf<NavKey>(Screen.Events(2024)),
            state.backStacks.getValue(Screen.Events()).toList(),
        )
    }

    @Test
    fun `event link replaces a stale event`() {
        val state = stateOn(Screen.Events())
        state.currentStack.add(Screen.EventDetail("2025hop"))
        state.navigateFromIntent(listOf(Screen.Events(), Screen.EventDetail("2025casj")))
        assertEquals(
            listOf(Screen.Events(), Screen.EventDetail("2025casj")),
            state.currentStack.toList(),
        )
    }

    @Test
    fun `notification replaces a stale event on the events root`() {
        val state = stateOn(Screen.Events())
        state.currentStack.clear()
        state.currentStack.addAll(listOf(Screen.Events(2024), Screen.EventDetail("2024hop")))
        state.navigateFromIntent(listOf(Screen.EventDetail("2025casj")))
        assertEquals(
            listOf(Screen.Events(2024), Screen.EventDetail("2025casj")),
            state.currentStack.toList(),
        )
    }

    @Test
    fun `navigate does not repeat the top destination`() {
        val state = stateOn(Screen.Teams)
        val navigator = Navigator(state, mockk(relaxed = true))
        navigator.navigate(Screen.TeamDetail("frc254"))
        navigator.navigate(Screen.TeamDetail("frc254"))
        assertEquals(listOf(Screen.Teams, Screen.TeamDetail("frc254")), state.currentStack.toList())
    }

    @Test
    fun `match link on a wide window opens beside its event`() {
        val state = stateOn(Screen.Teams)
        state.navigateFromIntent(matchLink, matchBesideEvent = true)
        assertEquals(Screen.Events(), state.topLevelRoute)
        assertEquals(matchLink, state.currentStack.toList())
    }

    @Test
    fun `match link on a wide window replaces the match selected in its event`() {
        val state = stateOn(Screen.Events())
        state.currentStack.add(Screen.EventDetail("2025hop"))
        state.currentStack.add(Screen.MatchDetail("2025hop_qm9"))
        state.navigateFromIntent(matchLink, matchBesideEvent = true)
        assertEquals(
            listOf(
                Screen.Events(),
                Screen.EventDetail("2025hop"),
                Screen.MatchDetail("2025hop_qm1"),
            ),
            state.currentStack.toList(),
        )
    }

    @Test
    fun `match notification on a wide window opens beside its event`() {
        val state = stateOn(Screen.Events())
        state.navigateFromIntent(listOf(Screen.MatchDetail("2025hop_qm1")), matchBesideEvent = true)
        assertEquals(matchLink, state.currentStack.toList())
    }

    @Test
    fun `match notification on a narrower window opens the match alone`() {
        val state = stateOn(Screen.Teams)
        state.navigateFromIntent(listOf(Screen.MatchDetail("2025hop_qm1")))
        assertEquals(
            listOf(Screen.Events(), Screen.MatchDetail("2025hop_qm1")),
            state.currentStack.toList(),
        )
    }
}
