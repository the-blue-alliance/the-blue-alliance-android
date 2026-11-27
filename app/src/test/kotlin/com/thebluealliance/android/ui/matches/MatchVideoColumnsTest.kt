package com.thebluealliance.android.ui.matches

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MatchVideoColumnsTest {
    @Test
    fun `video sits beside the score on expanded windows`() {
        assertTrue(matchVideoBesideScore(expandedWidth = true, videoCount = 1))
    }

    @Test
    fun `video stays below the score on narrower windows`() {
        assertFalse(matchVideoBesideScore(expandedWidth = false, videoCount = 2))
    }

    @Test
    fun `no side column without a video`() {
        assertFalse(matchVideoBesideScore(expandedWidth = true, videoCount = 0))
    }

    @Test
    fun `score columns split the row either side of the separator`() {
        assertEquals(102f to 318f, scoreColumnCenters(420f, 44f, 16f))
    }

    @Test
    fun `score column centres mirror about the row centre`() {
        val (red, blue) = scoreColumnCenters(380f, 50f, 16f)
        assertEquals(190f, (red + blue) / 2)
    }

    @Test
    fun `breakdown values sit under the scores when the label fits between them`() {
        val scores = 102f to 318f
        assertEquals(scores, breakdownValueCentres(scores, 420f, 100f, 30f, 30f, 8f))
    }

    @Test
    fun `a long label pushes the values outward`() {
        val (red, blue) = breakdownValueCentres(102f to 318f, 420f, 200f, 60f, 40f, 8f)
        assertEquals(72f, red)
        assertEquals(338f, blue)
    }
}
