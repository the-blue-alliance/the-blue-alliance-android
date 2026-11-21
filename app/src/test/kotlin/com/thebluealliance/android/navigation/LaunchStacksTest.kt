package com.thebluealliance.android.navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LaunchStacksTest {
    private val tabs = listOf(Screen.Events(), Screen.Teams, Screen.Districts, Screen.More)
    private val start = tabs.first()

    private fun launch(
        backStack: List<Screen>,
        newTask: Boolean,
    ) = launchStacks(backStack, newTask, tabs, start)

    @Test
    fun `plain launch opens the start tab`() {
        assertEquals(
            LaunchStacks(start, listOf(start), start),
            launch(listOf(start), newTask = true),
        )
        assertEquals(
            LaunchStacks(start, listOf(start), start),
            launch(listOf(start), newTask = false),
        )
    }

    @Test
    fun `new task keeps the full synthetic stack in its root's tab`() {
        val stack = listOf(Screen.Teams, Screen.TeamDetail("frc254"))
        assertEquals(
            LaunchStacks(Screen.Teams, stack, homeTab = start),
            launch(stack, newTask = true),
        )
    }

    @Test
    fun `existing task shows only the destination and back leaves from its tab`() {
        val stack = listOf(Screen.Teams, Screen.TeamDetail("frc254"))
        assertEquals(
            LaunchStacks(Screen.Teams, listOf(Screen.TeamDetail("frc254")), homeTab = Screen.Teams),
            launch(stack, newTask = false),
        )
    }

    @Test
    fun `lone non-tab destination is parented by the start tab`() {
        val match = Screen.MatchDetail("2024micmp4_f1m1")
        assertEquals(
            LaunchStacks(start, listOf(start, match), start),
            launch(listOf(match), newTask = true),
        )
    }

    @Test
    fun `parameterized tab root stays in its tab`() {
        val stack = listOf(Screen.Events(2024))
        assertEquals(LaunchStacks(start, stack, start), launch(stack, newTask = true))
    }
}
