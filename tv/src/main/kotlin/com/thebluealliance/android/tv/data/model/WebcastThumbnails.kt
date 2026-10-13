package com.thebluealliance.android.tv.data.model

import java.time.Instant
import java.time.LocalDate
import kotlin.math.sqrt

/**
 * Keyless stream thumbnails for event cards. Both CDNs serve a still without an API key, but each
 * also serves a "nothing here" image instead of a clean error, so the missing-art rules live here as
 * pure functions the image pipeline consults.
 */
object WebcastThumbnails {
    /** Twitch rotates its live preview every few minutes; one bucket per window keeps the cache honest. */
    const val TWITCH_REFRESH_MINUTES = 5L

    /**
     * YouTube answers an unknown/removed id with a 120×90 grey slate (sometimes HTTP 200, sometimes
     * 404). Real hqdefault stills are 480px wide, so anything this narrow is the slate.
     */
    const val MIN_USABLE_WIDTH_PX = 200

    // TBA sometimes stores a channel id or @handle instead of a video id; only video ids have stills.
    private val YOUTUBE_VIDEO_ID = Regex("""[A-Za-z0-9_-]{11}""")
    private val TWITCH_CHANNEL = Regex("""\w+""")
    private const val TWITCH_PREVIEW_BASE = "https://static-cdn.jtvnw.net/previews-ttv"

    /** The cache-buster bucket for Twitch previews, stable for [TWITCH_REFRESH_MINUTES] at a time. */
    fun cacheBucket(now: Instant): Long = now.epochSecond / 60 / TWITCH_REFRESH_MINUTES

    /** An offline Twitch channel 302-redirects to a generic `404_preview` slate rather than erroring. */
    fun isTwitchOfflinePreview(finalUrl: String): Boolean = "404_preview" in finalUrl

    fun isUsableWidth(widthPx: Int): Boolean = widthPx >= MIN_USABLE_WIDTH_PX

    /**
     * A YouTube live stream that hasn't started (or has ended) serves a (near-)black frame as its
     * still. Below this mean luminance, with little spread, the frame carries no picture.
     */
    const val DARK_MEAN_LUMINANCE = 0.06f

    /** A dark frame with a bright title slate on it varies far more than this, so it still shows. */
    const val DARK_MAX_STD_DEV = 0.05f

    /** Relative luminance (0–1) of an ARGB pixel, Rec. 709 weights. */
    fun luminance(argb: Int): Float {
        val r = (argb shr 16 and 0xFF) / 255f
        val g = (argb shr 8 and 0xFF) / 255f
        val b = (argb and 0xFF) / 255f
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }

    /** Whether sampled [luminances] (0–1) describe a frame that's essentially black. */
    fun isMostlyBlack(luminances: FloatArray): Boolean {
        if (luminances.isEmpty()) return false
        val mean = luminances.average().toFloat()
        val variance = luminances.map { (it - mean) * (it - mean) }.average().toFloat()
        return mean < DARK_MEAN_LUMINANCE && sqrt(variance) < DARK_MAX_STD_DEV
    }

    fun url(
        webcast: Webcast,
        cacheBucket: Long,
    ): String? =
        when (webcast.type) {
            WebcastType.YOUTUBE ->
                webcast.channel
                    .takeIf { YOUTUBE_VIDEO_ID.matches(it) }
                    ?.let { "https://i.ytimg.com/vi/$it/hqdefault.jpg" }
            WebcastType.TWITCH ->
                webcast.channel
                    .takeIf { TWITCH_CHANNEL.matches(it) }
                    ?.let {
                        "$TWITCH_PREVIEW_BASE/live_user_${it.lowercase()}-640x360.jpg?t=$cacheBucket"
                    }
            WebcastType.OTHER -> null
        }

    /**
     * The event's thumbnail: today's cast when it streams a link per day, else its first YouTube cast,
     * else its first Twitch one. Only casts that can produce a thumbnail compete.
     */
    fun url(
        event: Event,
        today: LocalDate,
        cacheBucket: Long,
    ): String? {
        val candidates =
            event.webcasts.mapNotNull { cast -> url(cast, cacheBucket)?.let { url -> cast to url } }
        val pick =
            candidates.firstOrNull { (cast, _) -> cast.date == today }
                ?: candidates.firstOrNull { (cast, _) -> cast.type == WebcastType.YOUTUBE }
                ?: candidates.firstOrNull()
        return pick?.second
    }
}
