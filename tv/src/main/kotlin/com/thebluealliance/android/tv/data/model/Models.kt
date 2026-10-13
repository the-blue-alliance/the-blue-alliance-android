package com.thebluealliance.android.tv.data.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

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

/** TBA's `event_type` codes. Mirrors the phone app's constants; the TV module can't depend on :app. */
enum class EventType(
    private val apiCode: Int,
) {
    REGIONAL(0),
    DISTRICT(1),
    DISTRICT_CHAMPIONSHIP(2),
    CHAMPIONSHIP_DIVISION(3),
    CHAMPIONSHIP_FINALS(4),
    DISTRICT_CHAMPIONSHIP_DIVISION(5),
    FESTIVAL_OF_CHAMPIONS(6),
    REMOTE(7),
    OFFSEASON(99),
    PRESEASON(100),
    UNLABELED(-1),
    ;

    val isChampionship: Boolean
        get() = this == CHAMPIONSHIP_DIVISION || this == CHAMPIONSHIP_FINALS

    /** A district's own season: its qualifiers plus its championship (and that championship's divisions). */
    val isDistrictLevel: Boolean
        get() =
            this == DISTRICT ||
                this == DISTRICT_CHAMPIONSHIP ||
                this == DISTRICT_CHAMPIONSHIP_DIVISION

    val isOffseason: Boolean
        get() = this == OFFSEASON || this == PRESEASON

    companion object {
        fun fromApi(code: Int?): EventType = entries.firstOrNull { it.apiCode == code } ?: UNLABELED
    }
}

data class District(
    val abbreviation: String,
    val displayName: String,
)

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
    val type: EventType,
    val district: District?,
) {
    val displayName: String get() = shortName?.takeIf { it.isNotBlank() } ?: name

    val location: String?
        get() =
            listOfNotNull(
                city?.takeIf { it.isNotBlank() },
                (stateProv ?: country)?.takeIf { it.isNotBlank() },
            ).joinToString(", ").ifBlank { null }

    /**
     * Native-app platforms this event streams on, in enum order. OTHER is left out: it has no TV app,
     * so advertising it on the card would promise something a click can't deliver.
     */
    val streamPlatforms: List<WebcastType>
        get() =
            webcasts
                .map { it.type }
                .filter { it != WebcastType.OTHER }
                .distinct()
                .sorted()

    fun sectionFor(today: LocalDate): EventSection =
        when {
            !today.isBefore(startDate) && !today.isAfter(endDate) -> EventSection.LIVE
            startDate.isAfter(today) -> EventSection.UPCOMING
            else -> EventSection.RECENT
        }

    /** Where the event stands relative to [today], by dates alone — we never know if a stream is up. */
    fun statusLabel(today: LocalDate): EventStatus {
        val untilStart = ChronoUnit.DAYS.between(today, startDate)
        val sinceEnd = ChronoUnit.DAYS.between(endDate, today)
        val window = EventStatus.WEEKDAY_WINDOW_DAYS
        return when {
            untilStart == 1L -> EventStatus.StartsTomorrow
            untilStart in 2..window -> EventStatus.StartsThisWeek(startDate)
            untilStart > window -> EventStatus.StartsOn(startDate)
            sinceEnd == 1L -> EventStatus.EndedYesterday
            sinceEnd in 2..window -> EventStatus.EndedThisWeek(endDate)
            sinceEnd > window -> EventStatus.EndedOn(endDate)
            startDate == endDate -> EventStatus.Today
            else ->
                EventStatus.Running(
                    day = ChronoUnit.DAYS.between(startDate, today).toInt() + 1,
                    totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1,
                )
        }
    }
}

/** An event's date-based status for its card badge; the UI turns it into localized text. */
sealed interface EventStatus {
    /** A single-day event happening today. */
    data object Today : EventStatus

    /** A multi-day event running today, on its 1-based [day] of [totalDays]. */
    data class Running(
        val day: Int,
        val totalDays: Int,
    ) : EventStatus

    data object StartsTomorrow : EventStatus

    /** Starts within [WEEKDAY_WINDOW_DAYS], so a weekday name ("Starts Sat") is unambiguous. */
    data class StartsThisWeek(
        val date: LocalDate,
    ) : EventStatus

    data class StartsOn(
        val date: LocalDate,
    ) : EventStatus

    data object EndedYesterday : EventStatus

    /** Ended within [WEEKDAY_WINDOW_DAYS], so a weekday name ("Ended Sun") is unambiguous. */
    data class EndedThisWeek(
        val date: LocalDate,
    ) : EventStatus

    data class EndedOn(
        val date: LocalDate,
    ) : EventStatus

    companion object {
        // A week out, a weekday name would collide with today's, so switch to month + day there.
        const val WEEKDAY_WINDOW_DAYS = 6L
    }
}

/** Events grouped into the sections the UI renders. */
data class EventFeed(
    val live: List<Event>,
    val upcoming: List<Event>,
    val recent: List<Event>,
) {
    val isEmpty: Boolean get() = live.isEmpty() && upcoming.isEmpty() && recent.isEmpty()

    /** The rows the home screen renders, top to bottom: today's events first, then Upcoming/Recent. */
    fun rows(): List<FeedRow> =
        buildList {
            val today = todayRows(live)
            addAll(today)
            val later =
                buildList {
                    if (upcoming.isNotEmpty()) add(FeedRow("upcoming", RowTitle.Upcoming, upcoming))
                    if (recent.isNotEmpty()) add(FeedRow("recent", RowTitle.Recent, recent))
                }
            // Only mark the boundary when there's a today block above it to close off.
            addAll(
                later.mapIndexed { i, row ->
                    if (i == 0 && today.isNotEmpty()) row.copy(startsAfterToday = true) else row
                },
            )
        }

    companion object {
        const val SECTION_CAP = 20

        /** At or below this many events today, one row reads better than several half-empty ones. */
        const val SPLIT_THRESHOLD = 6

        /** A district needs this many events today to earn its own row; otherwise it joins More Events. */
        const val MIN_DISTRICT_ROW = 2

        private val byName: Comparator<Event> =
            compareBy(String.CASE_INSENSITIVE_ORDER) { it.displayName }

        /**
         * Splits today's events into short themed rows so a mid-season Saturday (40-60 events) isn't
         * one endless row: Championship, then each busy district (alphabetically, so a district's row
         * is always in the same place), Regionals, More Events, Offseason.
         * Quiet days (or days with only one kind of event) keep a single "Happening Now" row.
         */
        fun todayRows(live: List<Event>): List<FeedRow> {
            if (live.isEmpty()) return emptyList()

            // FIRST Championship only; district championships stay in their district's row.
            val championship =
                live
                    .filter { it.type.isChampionship }
                    // Einstein (finals) is the marquee stream, so it leads the divisions.
                    .sortedWith(
                        compareBy<Event> { it.type != EventType.CHAMPIONSHIP_FINALS }.then(byName),
                    )
            val offseason = live.filter { it.type.isOffseason }.sortedWith(byName)
            val districts =
                live
                    .filter { it.type.isDistrictLevel && it.district != null }
                    .groupBy { it.district!! }
                    .filterValues { it.size >= MIN_DISTRICT_ROW }
                    .entries
                    .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.key.displayName })
            val inDistrictRow = districts.flatMap { it.value }.toSet()
            val regionals = live.filter { it.type == EventType.REGIONAL }.sortedWith(byName)
            // Leftovers — single-event districts, remote, Festival of Champions, unlabeled — are still
            // worth watching but aren't regionals, so they get a neutral row rather than a wrong label.
            val more =
                live
                    .filter {
                        !it.type.isChampionship &&
                            !it.type.isOffseason &&
                            it.type != EventType.REGIONAL &&
                            it !in inDistrictRow
                    }.sortedWith(byName)

            val groups =
                buildList {
                    if (championship.isNotEmpty()) {
                        add(FeedRow("live-championship", RowTitle.Championship, championship))
                    }
                    districts.forEach { (district, events) ->
                        add(
                            FeedRow(
                                key = "live-district-${district.abbreviation}",
                                title = RowTitle.District(district.displayName),
                                events = events.sortedWith(districtOrder),
                            ),
                        )
                    }
                    if (regionals.isNotEmpty()) {
                        add(
                            FeedRow("live-regionals", RowTitle.Regionals, regionals),
                        )
                    }
                    if (more.isNotEmpty()) add(FeedRow("live-more", RowTitle.MoreEvents, more))
                    if (offseason.isNotEmpty()) {
                        add(FeedRow("live-offseason", RowTitle.Offseason, offseason))
                    }
                }
            return if (live.size <= SPLIT_THRESHOLD || groups.size <= 1) {
                listOf(FeedRow("live", RowTitle.HappeningNow, live))
            } else {
                groups
            }
        }

        // The DCMP is the district's headline event, so it (then its divisions) leads the row.
        private val districtOrder: Comparator<Event> =
            compareBy<Event> {
                when (it.type) {
                    EventType.DISTRICT_CHAMPIONSHIP -> 0
                    EventType.DISTRICT_CHAMPIONSHIP_DIVISION -> 1
                    else -> 2
                }
            }.then(byName)

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

/** One horizontal row of the home screen. [key] is stable across refreshes so list state survives. */
data class FeedRow(
    val key: String,
    val title: RowTitle,
    val events: List<Event>,
    /** First row past the today block; the UI sets it apart so "today" reads as its own group. */
    val startsAfterToday: Boolean = false,
)

/** What a row is about; the UI turns it into localized text. */
sealed interface RowTitle {
    data object HappeningNow : RowTitle

    /** FIRST Championship (divisions + finals) — never a district championship. */
    data object Championship : RowTitle

    data class District(
        val displayName: String,
    ) : RowTitle

    data object Regionals : RowTitle

    data object MoreEvents : RowTitle

    data object Offseason : RowTitle

    data object Upcoming : RowTitle

    data object Recent : RowTitle
}
