package com.thebluealliance.android.ui.matches

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MatchVideoColumnsTest {
    @Test
    fun `compact windows keep the two-column grid`() {
        assertEquals(2, matchVideoColumns(videoCount = 1, wide = false))
        assertEquals(2, matchVideoColumns(videoCount = 3, wide = false))
    }

    @Test
    fun `a lone video spans the column on wide windows`() {
        assertEquals(1, matchVideoColumns(videoCount = 1, wide = true))
    }

    @Test
    fun `several videos pair up on wide windows`() {
        assertEquals(2, matchVideoColumns(videoCount = 2, wide = true))
    }

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
}
