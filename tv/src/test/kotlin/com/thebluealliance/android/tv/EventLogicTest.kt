package com.thebluealliance.android.tv

import com.thebluealliance.android.data.remote.dto.EventDto
import com.thebluealliance.android.data.remote.dto.WebcastDto
import com.thebluealliance.android.tv.data.api.toDomainOrNull
import com.thebluealliance.android.tv.data.model.Event
import com.thebluealliance.android.tv.data.model.EventFeed
import com.thebluealliance.android.tv.data.model.EventSection
import com.thebluealliance.android.tv.data.model.Webcast
import com.thebluealliance.android.tv.data.model.WebcastResolver
import com.thebluealliance.android.tv.data.model.WebcastType
import com.thebluealliance.android.tv.data.repository.FIXTURE_ANCHOR
import com.thebluealliance.android.tv.data.repository.anchoredToToday
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class EventLogicTest {
    private val today = LocalDate.of(2026, 5, 30)

    /**
     * A realistic event row from the bundled fixture. Tests `.copy(...)` the one field they
     * exercise so name/eventCode/year carry real values, not mocked placeholders.
     */
    private val battleAtTheBorder =
        EventDto(
            key = "2026mibatb",
            name = "Battle at the Border (Off-Season)",
            eventCode = "mibatb",
            year = 2026,
            startDate = "2026-05-30",
            endDate = "2026-05-30",
        )

    private fun event(
        key: String,
        start: LocalDate,
        end: LocalDate = start,
        webcasts: List<Webcast> = listOf(Webcast(WebcastType.TWITCH, "firstinspires")),
        shortName: String? = key,
        city: String? = "Town",
        stateProv: String? = "ST",
        country: String? = "USA",
    ) = Event(
        key = key,
        name = key,
        shortName = shortName,
        city = city,
        stateProv = stateProv,
        country = country,
        startDate = start,
        endDate = end,
        webcasts = webcasts,
    )

    // --- WebcastResolver: well-typed casts pass through unchanged ---------------------------

    @Test fun resolver_keepsDeclaredYouTube() {
        val w = WebcastResolver.resolve("youtube", "abc123XYZ_-")
        assertEquals(WebcastType.YOUTUBE, w.type)
        assertEquals("abc123XYZ_-", w.channel)
    }

    @Test fun resolver_keepsDeclaredTwitch() {
        val w = WebcastResolver.resolve("twitch", "firstinspires")
        assertEquals(WebcastType.TWITCH, w.type)
        assertEquals("firstinspires", w.channel)
    }

    // --- WebcastResolver: generic types recover the real platform from a URL ----------------

    @Test fun resolver_recoversYouTubeFromWatchUrl() {
        val w = WebcastResolver.resolve("livestream", "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        assertEquals(WebcastType.YOUTUBE, w.type)
        assertEquals("dQw4w9WgXcQ", w.channel)
    }

    @Test fun resolver_recoversYouTubeFromShortUrl() {
        val w = WebcastResolver.resolve("html5", "https://youtu.be/dQw4w9WgXcQ?t=10")
        assertEquals(WebcastType.YOUTUBE, w.type)
        assertEquals("dQw4w9WgXcQ", w.channel)
    }

    @Test fun resolver_recoversTwitchFromUrl() {
        val w = WebcastResolver.resolve("direct_link", "https://www.twitch.tv/firstupdatesnow")
        assertEquals(WebcastType.TWITCH, w.type)
        assertEquals("firstupdatesnow", w.channel)
    }

    @Test fun resolver_leavesGenericUrlAsOther() {
        val url = "https://livestream.firstinspires.org/on-summer"
        val w = WebcastResolver.resolve("livestream", url)
        assertEquals(WebcastType.OTHER, w.type)
        assertEquals(url, w.channel)
    }

    // --- WebcastResolver: per-day cast date -------------------------------------------------

    @Test fun resolver_parsesDeclaredCastDate() {
        val w = WebcastResolver.resolve("youtube", "abc123XYZ_-", date = "2026-06-01")
        assertEquals(LocalDate.of(2026, 6, 1), w.date)
    }

    @Test fun resolver_dateIsNullWhenAbsentOrUnparseable() {
        assertNull(WebcastResolver.resolve("youtube", "abc123XYZ_-").date)
        assertNull(WebcastResolver.resolve("youtube", "abc123XYZ_-", date = "not-a-date").date)
    }

    @Test fun resolver_carriesDateThroughUrlRecovery() {
        val w =
            WebcastResolver.resolve(
                "livestream",
                "https://youtu.be/dQw4w9WgXcQ",
                date = "2026-06-02",
            )
        assertEquals(WebcastType.YOUTUBE, w.type)
        assertEquals(LocalDate.of(2026, 6, 2), w.date)
    }

    // --- EventDto.toDomainOrNull -------------------------------------------------------------

    @Test fun dto_nullStartDateIsDropped() {
        assertNull(battleAtTheBorder.copy(startDate = null).toDomainOrNull())
    }

    @Test fun dto_missingEndDefaultsToStart() {
        val e = battleAtTheBorder.copy(endDate = null).toDomainOrNull()!!
        assertEquals(LocalDate.of(2026, 5, 30), e.startDate)
        assertEquals(LocalDate.of(2026, 5, 30), e.endDate)
    }

    @Test fun dto_dropsBlankChannelWebcastsAndResolvesRest() {
        val withMixedWebcasts =
            battleAtTheBorder.copy(
                webcasts =
                    listOf(
                        WebcastDto(type = "twitch", channel = ""),
                        WebcastDto(
                            type = "livestream",
                            channel = "https://youtu.be/dQw4w9WgXcQ",
                        ),
                    ),
            )
        val e = withMixedWebcasts.toDomainOrNull()!!
        assertEquals(1, e.webcasts.size)
        assertEquals(WebcastType.YOUTUBE, e.webcasts.first().type)
    }

    @Test fun dto_parsesPerDayWebcastDate() {
        val withDatedCast =
            battleAtTheBorder.copy(
                webcasts =
                    listOf(
                        WebcastDto(
                            type = "youtube",
                            channel = "day1",
                            date = "2026-05-30",
                        ),
                    ),
            )
        val e = withDatedCast.toDomainOrNull()!!
        assertEquals(LocalDate.of(2026, 5, 30), e.webcasts.first().date)
    }

    // --- Event.sectionFor boundaries ---------------------------------------------------------

    @Test fun sectionFor_classifiesAcrossBoundaries() {
        val live = event("live", today.minusDays(1), today.plusDays(1))
        val startsToday = event("startsToday", today, today)
        val endsToday = event("endsToday", today.minusDays(2), today)
        val future = event("future", today.plusDays(1))
        val past = event("past", today.minusDays(5), today.minusDays(2))

        assertEquals(EventSection.LIVE, live.sectionFor(today))
        assertEquals(EventSection.LIVE, startsToday.sectionFor(today))
        assertEquals(EventSection.LIVE, endsToday.sectionFor(today))
        assertEquals(EventSection.UPCOMING, future.sectionFor(today))
        assertEquals(EventSection.RECENT, past.sectionFor(today))
    }

    // --- EventFeed.from: filtering, grouping, ordering, capping ------------------------------

    @Test fun feed_dropsEventsWithoutWebcasts() {
        val withCast = event("a", today)
        val withoutCast = event("b", today, webcasts = emptyList())
        val feed = EventFeed.from(listOf(withCast, withoutCast), today)
        assertEquals(listOf("a"), feed.live.map { it.key })
    }

    @Test fun feed_groupsAndOrders() {
        val events =
            listOf(
                event("liveB", today, today.plusDays(1)),
                event("upLate", today.plusDays(10)),
                event("upSoon", today.plusDays(2)),
                event("recentOld", today.minusDays(20), today.minusDays(18)),
                event("recentNew", today.minusDays(3), today.minusDays(1)),
            )
        val feed = EventFeed.from(events, today)

        assertEquals(listOf("liveB"), feed.live.map { it.key })
        // upcoming ascending by start date
        assertEquals(listOf("upSoon", "upLate"), feed.upcoming.map { it.key })
        // recent descending by end date (most recently finished first)
        assertEquals(listOf("recentNew", "recentOld"), feed.recent.map { it.key })
    }

    @Test fun feed_capsEachSection() {
        val many = (1..25).map { event("up$it", today.plusDays(it.toLong())) }
        val feed = EventFeed.from(many, today)
        assertEquals(EventFeed.SECTION_CAP, feed.upcoming.size)
    }

    // --- Event display helpers ---------------------------------------------------------------

    @Test fun displayName_prefersShortNameThenFallsBack() {
        assertEquals("Short", event("k", today, shortName = "Short").displayName)
        assertEquals("k", event("k", today, shortName = "  ").displayName)
    }

    @Test fun location_joinsCityAndRegionWithCountryFallback() {
        assertEquals("Town, ST", event("k", today, city = "Town", stateProv = "ST").location)
        assertEquals(
            "Paris, France",
            event("k", today, city = "Paris", stateProv = null, country = "France").location,
        )
        assertNull(event("k", today, city = null, stateProv = null, country = null).location)
    }

    @Test fun feed_isEmptyWhenNothingHasWebcasts() {
        val feed = EventFeed.from(listOf(event("b", today, webcasts = emptyList())), today)
        assertTrue(feed.isEmpty)
    }

    // --- Bundled fixture date anchoring (AssetEventRepository) --------------------------------

    @Test fun anchored_isIdentityWhenTodayIsAnchor() {
        val events = listOf(event("a", FIXTURE_ANCHOR, FIXTURE_ANCHOR.plusDays(1)))
        assertEquals(events, events.anchoredToToday(FIXTURE_ANCHOR))
    }

    @Test fun anchored_keepsLiveClusterLiveOnAnyFutureDay() {
        // An event live on the anchor day must still be live after the whole fixture slides forward,
        // so the bundled sample always demos a Live hero regardless of the calendar date.
        val liveAtAnchor = event("live", FIXTURE_ANCHOR, FIXTURE_ANCHOR.plusDays(1))
        val newToday = FIXTURE_ANCHOR.plusDays(417)
        val shifted = listOf(liveAtAnchor).anchoredToToday(newToday).first()
        assertEquals(EventSection.LIVE, shifted.sectionFor(newToday))
        // Relative span is preserved by the shift.
        assertEquals(1L, ChronoUnit.DAYS.between(shifted.startDate, shifted.endDate))
    }

    @Test fun anchored_preservesSectionShapeAcrossWholeFeed() {
        val past = event("past", FIXTURE_ANCHOR.minusDays(20), FIXTURE_ANCHOR.minusDays(18))
        val live = event("live", FIXTURE_ANCHOR, FIXTURE_ANCHOR)
        val future = event("future", FIXTURE_ANCHOR.plusDays(30))
        val newToday = FIXTURE_ANCHOR.plusDays(365)
        val feed = EventFeed.from(listOf(past, live, future).anchoredToToday(newToday), newToday)
        assertEquals(listOf("live"), feed.live.map { it.key })
        assertEquals(listOf("future"), feed.upcoming.map { it.key })
        assertEquals(listOf("past"), feed.recent.map { it.key })
    }

    @Test fun anchored_shiftsPerDayWebcastDatesWithEvent() {
        val e =
            event(
                "live",
                FIXTURE_ANCHOR,
                FIXTURE_ANCHOR.plusDays(1),
                webcasts =
                    listOf(
                        Webcast(WebcastType.YOUTUBE, "d1", date = FIXTURE_ANCHOR),
                        Webcast(WebcastType.YOUTUBE, "d2", date = FIXTURE_ANCHOR.plusDays(1)),
                    ),
            )
        val newToday = FIXTURE_ANCHOR.plusDays(10)
        val shifted = listOf(e).anchoredToToday(newToday).first()
        assertEquals(newToday, shifted.webcasts[0].date)
        assertEquals(newToday.plusDays(1), shifted.webcasts[1].date)
    }

    @Test fun anchored_leavesUndatedWebcastNull() {
        val e = event("live", FIXTURE_ANCHOR, webcasts = listOf(Webcast(WebcastType.TWITCH, "ch")))
        val shifted = listOf(e).anchoredToToday(FIXTURE_ANCHOR.plusDays(5)).first()
        assertNull(shifted.webcasts.first().date)
    }
}
