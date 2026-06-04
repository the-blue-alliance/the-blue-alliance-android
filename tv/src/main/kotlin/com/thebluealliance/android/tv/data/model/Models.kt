package com.thebluealliance.android.tv.data.model

import java.time.LocalDate

/** Webcast platforms we can deep-link into on TV. Everything else has no native TV app. */
enum class WebcastType(
    val label: String,
) {
    YOUTUBE("YouTube"),
    TWITCH("Twitch"),
    OTHER("Web"),
    ;

    companion object {
        fun fromApi(type: String): WebcastType =
            when (type.trim().lowercase()) {
                "youtube" -> YOUTUBE
                "twitch" -> TWITCH
                else -> OTHER
            }
    }
}

data class Webcast(
    val type: WebcastType,
    /** type-specific id: youtube video id, twitch channel name, etc. */
    val channel: String,
    val file: String? = null,
    /** Some events stream a separate link per competition day; null when the cast spans the event. */
    val date: LocalDate? = null,
)

/**
 * Builds a [Webcast] from a raw TBA webcast. TBA mostly types webcasts cleanly, but generic types
 * ("livestream", "html5", "direct_link", …) sometimes carry a YouTube/Twitch URL — recover the real
 * platform and normalise [Webcast.channel] to what [WebcastLauncher] expects (a video id / channel
 * name) so we deep-link into the native app instead of leaving it as an unplayable OTHER webcast.
 */
object WebcastResolver {
    private val YOUTUBE_ID =
        Regex("""(?:youtube\.com/(?:watch\?(?:[^#]*&)?v=|live/|embed/|v/)|youtu\.be/)([\w-]{11})""")
    private val TWITCH_CHANNEL = Regex("""twitch\.tv/([A-Za-z0-9_]+)""")

    fun resolve(
        rawType: String,
        channel: String,
        file: String? = null,
        date: String? = null,
    ): Webcast {
        val day = date?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        val declared = WebcastType.fromApi(rawType)
        if (declared != WebcastType.OTHER) return Webcast(declared, channel, file, day)
        val haystack = "$channel ${file.orEmpty()}"
        YOUTUBE_ID
            .find(
                haystack,
            )?.let { return Webcast(WebcastType.YOUTUBE, it.groupValues[1], file, day) }
        TWITCH_CHANNEL.find(haystack)?.let {
            return Webcast(WebcastType.TWITCH, it.groupValues[1], file, day)
        }
        return Webcast(WebcastType.OTHER, channel, file, day)
    }
}

/** Where an event falls relative to "today" — drives the section it renders in. */
enum class EventSection { LIVE, UPCOMING, RECENT }

data class Event(
    val key: String,
    val name: String,
    val shortName: String?,
    val city: String?,
    val stateProv: String?,
    val country: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val webcasts: List<Webcast>,
) {
    val displayName: String get() = shortName?.takeIf { it.isNotBlank() } ?: name

    val location: String?
        get() =
            listOfNotNull(
                city?.takeIf { it.isNotBlank() },
                (stateProv ?: country)?.takeIf { it.isNotBlank() },
            ).joinToString(", ").ifBlank { null }

    fun sectionFor(today: LocalDate): EventSection =
        when {
            !today.isBefore(startDate) && !today.isAfter(endDate) -> EventSection.LIVE
            startDate.isAfter(today) -> EventSection.UPCOMING
            else -> EventSection.RECENT
        }
}

/** Events grouped into the sections the UI renders. */
data class EventFeed(
    val live: List<Event>,
    val upcoming: List<Event>,
    val recent: List<Event>,
) {
    val isEmpty: Boolean get() = live.isEmpty() && upcoming.isEmpty() && recent.isEmpty()

    companion object {
        const val SECTION_CAP = 20

        /** Build the feed from a flat list of events, keeping only those with webcasts. */
        fun from(
            events: List<Event>,
            today: LocalDate,
        ): EventFeed {
            val withCasts = events.filter { it.webcasts.isNotEmpty() }
            val live =
                withCasts
                    .filter { it.sectionFor(today) == EventSection.LIVE }
                    .sortedBy { it.startDate }
            val upcoming =
                withCasts
                    .filter { it.sectionFor(today) == EventSection.UPCOMING }
                    .sortedBy { it.startDate }
                    .take(SECTION_CAP)
            val recent =
                withCasts
                    .filter { it.sectionFor(today) == EventSection.RECENT }
                    .sortedByDescending { it.endDate }
                    .take(SECTION_CAP)
            return EventFeed(live = live, upcoming = upcoming, recent = recent)
        }
    }
}
