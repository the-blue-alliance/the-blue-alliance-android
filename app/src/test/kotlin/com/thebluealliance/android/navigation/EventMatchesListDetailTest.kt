package com.thebluealliance.android.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EventMatchesListDetailTest {
    private val events = emptyMap<String, Any>()
    private val event = eventMatchesListPane("2025mifer")
    private val match = matchDetailPane("2025mifer_qm12")

    private fun sizeClass(widthDp: Float) =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp, 640f)

    @Test
    fun `two panes start at the expanded breakpoint`() {
        assertFalse(showsEventMatchesTwoPane(sizeClass(360f)))
        assertFalse(showsEventMatchesTwoPane(sizeClass(600f)))
        assertFalse(showsEventMatchesTwoPane(sizeClass(800f)))
        assertFalse(showsEventMatchesTwoPane(sizeClass(839f)))
        assertTrue(showsEventMatchesTwoPane(sizeClass(840f)))
        assertTrue(showsEventMatchesTwoPane(sizeClass(1024f)))
    }

    @Test
    fun `event on top is the scene with nothing selected`() {
        assertEquals(EventMatchesPanes(1, null), eventMatchesPanes(listOf(events, event)))
    }

    @Test
    fun `match of the event below it is selected`() {
        assertEquals(
            EventMatchesPanes(1, "2025mifer_qm12"),
            eventMatchesPanes(listOf(events, event, match)),
        )
    }

    @Test
    fun `match of another event stays full screen`() {
        assertNull(eventMatchesPanes(listOf(event, matchDetailPane("2025hop_qm1"))))
    }

    @Test
    fun `match not directly above an event stays full screen`() {
        assertNull(eventMatchesPanes(listOf(events, match)))
        assertNull(eventMatchesPanes(listOf(event, emptyMap(), match)))
    }

    @Test
    fun `other screens on top are not the scene`() {
        assertNull(eventMatchesPanes(listOf(event, match, emptyMap())))
        assertNull(eventMatchesPanes(emptyList()))
    }

    @Test
    fun `selecting a match pushes it over the event`() {
        val stack = mutableListOf<NavKey>(Screen.EventDetail("2025mifer"))
        stack.selectMatch(Screen.MatchDetail("2025mifer_qm1"))
        assertEquals(
            listOf(Screen.EventDetail("2025mifer"), Screen.MatchDetail("2025mifer_qm1")),
            stack,
        )
    }

    @Test
    fun `selecting another match replaces the selection`() {
        val stack =
            mutableListOf<NavKey>(
                Screen.EventDetail("2025mifer"),
                Screen.MatchDetail("2025mifer_qm1"),
            )
        stack.selectMatch(Screen.MatchDetail("2025mifer_sf1m1"))
        assertEquals(
            listOf(Screen.EventDetail("2025mifer"), Screen.MatchDetail("2025mifer_sf1m1")),
            stack,
        )
    }

    @Test
    fun `selecting the selected match changes nothing`() {
        val stack =
            mutableListOf<NavKey>(
                Screen.EventDetail("2025mifer"),
                Screen.MatchDetail("2025mifer_qm1"),
            )
        stack.selectMatch(Screen.MatchDetail("2025mifer_qm1"))
        assertEquals(2, stack.size)
    }

    private fun entry(key: NavKey) =
        NavEntry(
            key = key,
            contentKey = key,
            metadata =
                when (key) {
                    is Screen.EventDetail -> eventMatchesListPane(key.eventKey)
                    is Screen.MatchDetail -> matchDetailPane(key.matchKey)
                    else -> emptyMap()
                },
        ) {}

    private fun scene(
        vararg keys: NavKey,
        twoPane: Boolean = true,
    ): Scene<NavKey>? =
        with(EventMatchesSceneStrategy<NavKey>(twoPane)) {
            SceneStrategyScope<NavKey>().calculateScene(keys.map(::entry))
        }

    @Test
    fun `scene shows the event and its selected match together`() {
        val scene =
            requireNotNull(
                scene(
                    Screen.Events(),
                    Screen.EventDetail("2025hop"),
                    Screen.MatchDetail("2025hop_qm1"),
                ),
            )
        assertEquals(
            listOf(Screen.EventDetail("2025hop"), Screen.MatchDetail("2025hop_qm1")),
            scene.entries.map { it.contentKey },
        )
        // Back clears the selection; the event stays.
        assertEquals(
            listOf(Screen.Events(), Screen.EventDetail("2025hop")),
            scene.previousEntries.map { it.contentKey },
        )
    }

    @Test
    fun `changing or clearing the selection keeps the scene in place`() {
        val none = scene(Screen.Events(), Screen.EventDetail("2025hop"))
        val qm1 =
            scene(Screen.Events(), Screen.EventDetail("2025hop"), Screen.MatchDetail("2025hop_qm1"))
        val f1 =
            scene(
                Screen.Events(),
                Screen.EventDetail("2025hop"),
                Screen.MatchDetail("2025hop_f1m1"),
            )
        assertEquals(none!!.key, qm1!!.key)
        assertEquals(qm1.key, f1!!.key)
    }

    @Test
    fun `narrower windows get no scene`() {
        assertNull(
            scene(
                Screen.Events(),
                Screen.EventDetail("2025hop"),
                Screen.MatchDetail("2025hop_qm1"),
                twoPane = false,
            ),
        )
    }

    @Test
    fun `match from outside the event gets no scene`() {
        assertNull(
            scene(
                Screen.EventDetail("2025hop"),
                Screen.TeamEventDetail("frc254", "2025hop"),
                Screen.MatchDetail("2025hop_qm1"),
            ),
        )
    }
}
