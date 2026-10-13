package com.thebluealliance.android.tv

import com.thebluealliance.android.tv.data.model.Event
import com.thebluealliance.android.tv.data.model.EventType
import com.thebluealliance.android.tv.data.model.Webcast
import com.thebluealliance.android.tv.data.model.WebcastThumbnails
import com.thebluealliance.android.tv.data.model.WebcastType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

class WebcastThumbnailsTest {
    private val today = LocalDate.of(2026, 3, 14)
    private val bucket = 42L

    private fun event(vararg webcasts: Webcast) =
        Event(
            key = "2026test",
            name = "Test Regional",
            shortName = "Test",
            city = null,
            stateProv = null,
            country = null,
            startDate = today,
            endDate = today.plusDays(2),
            webcasts = webcasts.toList(),
            type = EventType.REGIONAL,
            district = null,
        )

    @Test
    fun `youtube video id builds an hqdefault url`() {
        assertEquals(
            "https://i.ytimg.com/vi/abc_DEF-123/hqdefault.jpg",
            WebcastThumbnails.url(Webcast(WebcastType.YOUTUBE, "abc_DEF-123"), bucket),
        )
    }

    @Test
    fun `youtube channel ids and handles have no thumbnail`() {
        listOf("UCxxxxxxxxxxxxxxxxxxxxxx", "@FIRSTinspires", "short", "abc DEF 123").forEach {
            assertNull(WebcastThumbnails.url(Webcast(WebcastType.YOUTUBE, it), bucket), it)
        }
    }

    @Test
    fun `twitch preview lowercases the channel and carries the cache buster`() {
        assertEquals(
            "https://static-cdn.jtvnw.net/previews-ttv/live_user_firstinspires-640x360.jpg?t=42",
            WebcastThumbnails.url(Webcast(WebcastType.TWITCH, "FIRSTinspires"), bucket),
        )
        assertNull(WebcastThumbnails.url(Webcast(WebcastType.TWITCH, ""), bucket))
    }

    @Test
    fun `other webcasts have no thumbnail`() {
        assertNull(WebcastThumbnails.url(Webcast(WebcastType.OTHER, "https://example.com"), bucket))
    }

    @Test
    fun `event prefers todays cast, then youtube, then twitch`() {
        val twitch = Webcast(WebcastType.TWITCH, "firstinspires")
        val youtube = Webcast(WebcastType.YOUTUBE, "aaaaaaaaaaa")
        val dayTwo = Webcast(WebcastType.YOUTUBE, "bbbbbbbbbbb", date = today.plusDays(1))
        val todays = Webcast(WebcastType.TWITCH, "todaycast", date = today)

        fun pick(vararg casts: Webcast) = WebcastThumbnails.url(event(*casts), today, bucket)

        assertEquals(WebcastThumbnails.url(todays, bucket), pick(youtube, dayTwo, todays))
        assertEquals(WebcastThumbnails.url(youtube, bucket), pick(twitch, youtube))
        assertEquals(
            WebcastThumbnails.url(twitch, bucket),
            pick(Webcast(WebcastType.OTHER, "x"), twitch),
        )
        // A today-dated cast that can't produce a still doesn't block the fallback.
        assertEquals(
            WebcastThumbnails.url(twitch, bucket),
            pick(Webcast(WebcastType.YOUTUBE, "@handle", date = today), twitch),
        )
        assertNull(pick(Webcast(WebcastType.OTHER, "x")))
        assertNull(pick())
    }

    @Test
    fun `cache bucket is stable within five minutes and changes across them`() {
        val start = Instant.parse("2026-03-14T12:00:00Z")
        assertEquals(
            WebcastThumbnails.cacheBucket(start),
            WebcastThumbnails.cacheBucket(start.plusSeconds(4 * 60 + 59)),
        )
        assertNotEquals(
            WebcastThumbnails.cacheBucket(start),
            WebcastThumbnails.cacheBucket(start.plusSeconds(5 * 60)),
        )
    }

    @Test
    fun `twitch offline redirect target is missing art`() {
        assertTrue(
            WebcastThumbnails.isTwitchOfflinePreview(
                "https://static-cdn.jtvnw.net/ttv-static/404_preview-640x360.jpg",
            ),
        )
        assertFalse(
            WebcastThumbnails.isTwitchOfflinePreview(
                "https://static-cdn.jtvnw.net/previews-ttv/live_user_firstinspires-640x360.jpg?t=1",
            ),
        )
    }

    @Test
    fun `youtube grey slate is too narrow to use`() {
        assertFalse(WebcastThumbnails.isUsableWidth(120))
        assertTrue(WebcastThumbnails.isUsableWidth(480))
        assertTrue(WebcastThumbnails.isUsableWidth(640))
    }

    @Test
    fun `luminance spans black to white`() {
        assertEquals(0f, WebcastThumbnails.luminance(0xFF000000.toInt()), 1e-4f)
        assertEquals(1f, WebcastThumbnails.luminance(0xFFFFFFFF.toInt()), 1e-4f)
        // Green dominates perceived brightness.
        assertTrue(
            WebcastThumbnails.luminance(0xFF00FF00.toInt()) >
                WebcastThumbnails.luminance(0xFFFF0000.toInt()),
        )
    }

    @Test
    fun `black and near-black frames are missing art`() {
        assertTrue(WebcastThumbnails.isMostlyBlack(FloatArray(144)))
        assertTrue(
            WebcastThumbnails.isMostlyBlack(
                FloatArray(144) {
                    if (it % 2 ==
                        0
                    ) {
                        0.02f
                    } else {
                        0.05f
                    }
                },
            ),
        )
    }

    @Test
    fun `real pictures are not black`() {
        // Mid-grey and a bright frame.
        assertFalse(WebcastThumbnails.isMostlyBlack(FloatArray(144) { 0.5f }))
        assertFalse(WebcastThumbnails.isMostlyBlack(FloatArray(144) { 0.9f }))
        // A dark slate with white title text: the mean is low but the spread gives it away.
        assertFalse(WebcastThumbnails.isMostlyBlack(FloatArray(144) { if (it < 8) 1f else 0f }))
        assertFalse(WebcastThumbnails.isMostlyBlack(FloatArray(0)))
    }
}
