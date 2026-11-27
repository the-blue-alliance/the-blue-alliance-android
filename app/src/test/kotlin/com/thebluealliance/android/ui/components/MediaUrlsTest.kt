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

    @Test
    fun `large imgur tiles load the huge thumbnail`() {
        assertEquals("https://i.imgur.com/aBc123h.jpg", mediaUrl("imgur", "aBc123", large = true))
    }

    @Test
    fun `youtube tiles load the high-quality thumbnail`() {
        assertEquals("https://img.youtube.com/vi/xyz/hqdefault.jpg", mediaUrl("youtube", "xyz"))
    }

    @Test
    fun `large youtube tiles load the max-resolution thumbnail`() {
        assertEquals(
            "https://img.youtube.com/vi/xyz/maxresdefault.jpg",
            mediaUrl("youtube", "xyz", large = true),
        )
    }

    @Test
    fun `large instagram tiles ask for the large size`() {
        assertEquals(
            "https://www.instagram.com/p/abc/media/?size=l",
            mediaUrl("instagram-image", "abc", large = true),
        )
    }

    @Test
    fun `chief delphi photos have one size`() {
        assertEquals(mediaUrl("cdphotothread", "42"), mediaUrl("cdphotothread", "42", large = true))
    }
}
