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
        assertFalse(useNavigationRail(sizeClass(360f, 225f)))
        assertFalse(useNavigationRail(sizeClass(412f, 915f)))
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
    fun `events tab is selected for any year`() {
        assertEquals(listOf("Events"), selectedLabels(Screen.Events(year = 2019)))
    }

    @Test
    fun `more tab is selected on its sub screens`() {
        listOf(Screen.More, Screen.MyTBA, Screen.Settings, Screen.About, Screen.Thanks).forEach {
            assertEquals(listOf("More"), selectedLabels(it))
        }
    }

    @Test
    fun `no tab is selected on regional advancement`() {
        assertEquals(emptyList<String>(), selectedLabels(Screen.RegionalAdvancement))
    }
}
