package com.thebluealliance.android.util

import android.net.Uri
import com.thebluealliance.android.domain.model.Webcast
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URLEncoder

class WebcastLinksTest {
    // android.net.Uri is a stub in JVM tests; stand in for Uri.encode with a percent-encoder.
    @BeforeEach
    fun mockUriEncode() {
        mockkStatic(Uri::class)
        every { Uri.encode(any()) } answers {
            URLEncoder.encode(firstArg<String>(), Charsets.UTF_8).replace("+", "%20")
        }
    }

    @AfterEach
    fun unmockUriEncode() {
        unmockkStatic(Uri::class)
    }

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
    fun `webcast urls encode the channel`() {
        assertEquals(
            "https://twitch.tv/first%20inspires%2Fx%3Fy",
            webcastUrl(webcast("twitch", "first inspires/x?y")),
        )
        assertEquals(
            "https://youtube.com/watch?v=abc%26list%3Dx",
            webcastUrl(webcast("youtube", "abc&list=x")),
        )
        assertEquals(
            "https://livestream.com/accounts/1%2F2/events/3%3F4",
            webcastUrl(webcast("livestream", "1/2", file = "3?4")),
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
    fun `twitch deep link encodes the channel`() {
        assertEquals(
            "twitch://stream/first%20inspires%2Fx%3Fy",
            webcastAppUri(webcast("twitch", "first inspires/x?y")),
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
