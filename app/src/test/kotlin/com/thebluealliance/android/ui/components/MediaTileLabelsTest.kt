package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MediaTileLabelsTest {
    @Test
    fun `tiles are numbered by kind`() {
        assertEquals("Video 1 of 3", mediaTileDescription("youtube", 0, 3))
        assertEquals("Photo 3 of 3", mediaTileDescription("imgur", 2, 3))
    }

    @Test
    fun `click label names the destination`() {
        assertEquals("Open in YouTube", mediaClickLabel("youtube"))
        assertEquals("Open full image", mediaClickLabel("cdphotothread"))
    }
}
