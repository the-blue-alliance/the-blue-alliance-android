package com.thebluealliance.android.ui.components

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MediaUrlsTest {
    @Test
    fun `imgur tiles load the large thumbnail`() {
        assertEquals("https://i.imgur.com/aBc123l.jpg", mediaUrl("imgur", "aBc123"))
    }

    @Test
    fun `imgur tiles link out to the original`() {
        assertEquals("https://imgur.com/aBc123", mediaLinkUrl("imgur", "aBc123"))
    }
}
