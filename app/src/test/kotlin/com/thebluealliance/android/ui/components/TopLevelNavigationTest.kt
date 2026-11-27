package com.thebluealliance.android.ui.components

import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.TOP_LEVEL_DESTINATIONS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TopLevelNavigationTest {
    private fun sizeClass(
        widthDp: Float,
        heightDp: Float,
    ) = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp, heightDp)

    private fun selectedLabels(route: androidx.navigation3.runtime.NavKey) =
        TOP_LEVEL_DESTINATIONS.filter { it.isSelectedFor(route) }.map { it.label }

    @Test
    fun `compact widths keep the bottom bar`() {
        assertFalse(useNavigationRail(sizeClass(412f, 915f)))
        assertFalse(useNavigationRail(sizeClass(360f, 480f)))
        assertFalse(useNavigationRail(sizeClass(599f, 800f)))
    }

    @Test
    fun `medium and expanded widths use the rail`() {
        assertTrue(useNavigationRail(sizeClass(600f, 225f)))
        assertTrue(useNavigationRail(sizeClass(840f, 600f)))
        assertTrue(useNavigationRail(sizeClass(1024f, 640f)))
        assertTrue(useNavigationRail(sizeClass(1280f, 800f)))
    }

    @Test
    fun `short windows use the rail at any width`() {
        assertTrue(useNavigationRail(sizeClass(360f, 225f)))
        assertTrue(useNavigationRail(sizeClass(599f, 479f)))
    }

    @Test
    fun `events tab is selected for any year`() {
        assertEquals(listOf("Events"), selectedLabels(Screen.Events(year = 2019)))
    }

    @Test
    fun `more tab is selected on its sub screens`() {
        listOf(
            Screen.More,
            Screen.MyTBA,
            Screen.RegionalAdvancement(),
            Screen.Settings,
            Screen.About,
            Screen.Thanks,
        ).forEach {
            assertEquals(listOf("More"), selectedLabels(it))
        }
    }

    @Test
    fun `more tab is selected on regional advancement for any year`() {
        assertEquals(listOf("More"), selectedLabels(Screen.RegionalAdvancement(2025)))
        assertEquals(
            listOf("More"),
            railSelectedLabels(Screen.RegionalAdvancement(2025), Screen.More, true),
        )
    }

    private fun railSelectedLabels(
        route: androidx.navigation3.runtime.NavKey,
        stackRoot: androidx.navigation3.runtime.NavKey,
        onTopLevelScreen: Boolean,
    ) = TOP_LEVEL_DESTINATIONS
        .filter { it.isRailSelectedFor(route, stackRoot, onTopLevelScreen) }
        .map { it.label }

    @Test
    fun `rail matches the bottom bar on top-level screens`() {
        assertEquals(listOf("More"), railSelectedLabels(Screen.MyTBA, Screen.More, true))
        assertEquals(
            listOf("More"),
            railSelectedLabels(Screen.RegionalAdvancement(), Screen.More, true),
        )
    }

    @Test
    fun `rail marks the owning tab on detail screens`() {
        assertEquals(
            listOf("Events"),
            railSelectedLabels(Screen.EventDetail("2026casj"), Screen.Events(), false),
        )
        assertEquals(
            listOf("More"),
            railSelectedLabels(Screen.TeamDetail("frc254"), Screen.More, false),
        )
    }
}
