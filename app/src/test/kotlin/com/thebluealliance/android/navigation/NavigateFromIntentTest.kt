package com.thebluealliance.android.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NavigateFromIntentTest {
    private val tabs: List<NavKey> =
        listOf(Screen.Events(), Screen.Teams, Screen.Districts, Screen.More)

    private fun stateOn(tab: NavKey): NavigationState =
        NavigationState(
            startRoute = Screen.Events(),
            topLevelRoute = mutableStateOf(tab),
            backStacks = tabs.associateWith { NavBackStack(it) },
        )

    private fun NavigationState.navigateFromIntent(backStack: List<NavKey>) =
        Navigator(this, mockk(relaxed = true)).navigateFromIntent(backStack)

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
}
