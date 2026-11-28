package com.thebluealliance.android.ui.common

import androidx.compose.ui.unit.Density
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserMessageInsetsTest {
    // Compose's default: scroll the least distance that shows the whole item in the container.
    private val default: (Float, Float, Float) -> Float = { offset, size, container ->
        val trailing = offset + size
        when {
            offset >= 0 && trailing <= container -> 0f
            offset < 0 && trailing > container -> 0f
            kotlin.math.abs(offset) < kotlin.math.abs(trailing - container) -> offset
            else -> trailing - container
        }
    }

    private fun distance(
        offset: Float,
        size: Float,
        container: Float = 1000f,
        covered: Float,
    ) = scrollDistanceAbove(offset, size, container, covered, default)

    @Test
    fun `a row under the snackbar scrolls up until it clears it`() {
        assertEquals(120f, distance(offset = 800f, size = 200f, covered = 120f))
    }

    @Test
    fun `a row just above the snackbar stays put`() {
        assertEquals(0f, distance(offset = 600f, size = 200f, covered = 120f))
    }

    @Test
    fun `a row above the container still scrolls down to its top`() {
        assertEquals(-50f, distance(offset = -50f, size = 200f, covered = 120f))
    }

    @Test
    fun `with no snackbar the default applies to the whole container`() {
        assertEquals(0f, distance(offset = 800f, size = 200f, covered = 0f))
    }

    @Test
    fun `an item too long to fit above the snackbar keeps the default`() {
        assertEquals(0f, distance(offset = 0f, size = 1000f, covered = 120f))
    }

    @Test
    fun `a whole pager page never moves sideways for the snackbar`() {
        assertEquals(0f, distance(offset = 0f, size = 1000f, container = 1000f, covered = 300f))
    }

    @Test
    fun `the inset reports the covered height at the bottom only`() {
        val insets = UserMessageInsets { 240 }
        val density = Density(1f)
        assertEquals(240, insets.getBottom(density))
        assertEquals(0, insets.getTop(density))
    }
}
