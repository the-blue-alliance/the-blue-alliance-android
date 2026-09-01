package com.thebluealliance.android.util

import com.thebluealliance.android.domain.model.Webcast
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class WebcastLinksTest {
    private fun webcast(
        type: String,
        channel: String,
        file: String? = null,
    ) = Webcast(type = type, channel = channel, file = file, date = null)

    // ── webcastUrl ───────────────────────────────────────────────────────────

    @Test
    fun `twitch webcast url uses the channel`() {
        assertEquals(
            "https://twitch.tv/firstinspires",
            webcastUrl(webcast("twitch", "firstinspires")),
        )
    }

    @Test
    fun `youtube webcast url uses the video id`() {
        assertEquals(
            "https://youtube.com/watch?v=abc123",
            webcastUrl(webcast("youtube", "abc123")),
        )
    }

    @Test
    fun `livestream webcast url uses account and file`() {
        assertEquals(
            "https://livestream.com/accounts/12345/events/678",
            webcastUrl(webcast("livestream", "12345", file = "678")),
        )
    }

    @Test
    fun `unknown webcast type has no url`() {
        assertNull(webcastUrl(webcast("direct_link", "https://example.com/stream")))
    }

    // ── webcastAppUri ────────────────────────────────────────────────────────

    @Test
    fun `twitch webcast deep links into the twitch app`() {
        assertEquals(
            "twitch://stream/firstinspires",
            webcastAppUri(webcast("twitch", "firstinspires")),
        )
    }

    @Test
    fun `twitch webcast with a blank channel has no deep link`() {
        assertNull(webcastAppUri(webcast("twitch", "")))
        assertNull(webcastAppUri(webcast("twitch", "   ")))
    }

    @Test
    fun `non-twitch webcasts have no deep link`() {
        assertNull(webcastAppUri(webcast("youtube", "abc123")))
        assertNull(webcastAppUri(webcast("livestream", "12345", file = "678")))
        assertNull(webcastAppUri(webcast("direct_link", "https://example.com/stream")))
    }
}
