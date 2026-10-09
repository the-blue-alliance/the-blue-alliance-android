package com.thebluealliance.android.tv

import com.thebluealliance.android.data.remote.dto.DistrictDto
import com.thebluealliance.android.data.remote.dto.EventDto
import com.thebluealliance.android.data.remote.dto.WebcastDto
import com.thebluealliance.android.tv.data.api.toDomainOrNull
import com.thebluealliance.android.tv.data.model.District
import com.thebluealliance.android.tv.data.model.Event
import com.thebluealliance.android.tv.data.model.EventFeed
import com.thebluealliance.android.tv.data.model.EventSection
import com.thebluealliance.android.tv.data.model.EventType
import com.thebluealliance.android.tv.data.model.FeedRow
import com.thebluealliance.android.tv.data.model.RowTitle
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
        type: EventType = EventType.REGIONAL,
        district: District? = null,
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
        type = type,
        district = district,
    )

    private val fim = District("fim", "FIRST In Michigan")
    private val fit = District("fit", "FIRST In Texas")
    private val ne = District("ne", "New England")

    /** An event running today; [key] doubles as its display name so name ordering is visible. */
    private fun today(
        key: String,
        type: EventType = EventType.REGIONAL,
        district: District? = null,
    ) = event(key, today, type = type, district = district)

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

    @Test fun dto_mapsTypeAndDistrict() {
        val dcmp =
            battleAtTheBorder
                .copy(
                    eventType = 2,
                    district = DistrictDto("fim", "FIRST In Michigan", "2026fim", 2026),
                ).toDomainOrNull()!!
        assertEquals(EventType.DISTRICT_CHAMPIONSHIP, dcmp.type)
        assertEquals(District("fim", "FIRST In Michigan"), dcmp.district)
    }

    @Test fun dto_missingOrUnknownTypeIsUnlabeled() {
        val bare = battleAtTheBorder.toDomainOrNull()!!
        assertEquals(EventType.UNLABELED, bare.type)
        assertNull(bare.district)
        assertEquals(
            EventType.UNLABELED,
            battleAtTheBorder.copy(eventType = 42).toDomainOrNull()!!.type,
        )
    }

    @Test fun eventType_fromApiCoversEveryCode() {
        assertEquals(EventType.REGIONAL, EventType.fromApi(0))
        assertEquals(EventType.DISTRICT_CHAMPIONSHIP_DIVISION, EventType.fromApi(5))
        assertEquals(EventType.REMOTE, EventType.fromApi(7))
        assertEquals(EventType.OFFSEASON, EventType.fromApi(99))
        assertEquals(EventType.PRESEASON, EventType.fromApi(100))
        assertEquals(EventType.UNLABELED, EventType.fromApi(-1))
        assertEquals(EventType.UNLABELED, EventType.fromApi(null))
    }

    // --- EventFeed.todayRows: splitting today's events into themed rows ------------------------

    private fun List<FeedRow>.keys() = map { it.key }

    private fun FeedRow.eventKeys() = events.map { it.key }

    @Test fun todayRows_emptyWhenNothingToday() {
        assertTrue(EventFeed.todayRows(emptyList()).isEmpty())
    }

    @Test fun todayRows_collapsesQuietDayIntoOneRow() {
        // Six events across four groups is still a quiet day: one row, nothing to theme.
        val live =
            listOf(
                today("cmp", EventType.CHAMPIONSHIP_FINALS),
                today("mi1", EventType.DISTRICT, fim),
                today("mi2", EventType.DISTRICT, fim),
                today("reg1"),
                today("reg2"),
                today("off", EventType.OFFSEASON),
            )
        val rows = EventFeed.todayRows(live)
        assertEquals(listOf("live"), rows.keys())
        assertEquals(RowTitle.HappeningNow, rows.single().title)
        assertEquals(live, rows.single().events)
    }

    @Test fun todayRows_collapsesWhenOnlyOneGroup() {
        // A busy off-season Saturday is still one kind of event — no point in a lone themed row.
        val live = (1..9).map { today("off$it", EventType.OFFSEASON) }
        val rows = EventFeed.todayRows(live)
        assertEquals(listOf("live"), rows.keys())
        assertEquals(RowTitle.HappeningNow, rows.single().title)
    }

    @Test fun todayRows_ordersGroupsAndTitlesThem() {
        val live =
            listOf(
                today("off", EventType.PRESEASON),
                today("reg"),
                today("remote", EventType.REMOTE),
                today("tx1", EventType.DISTRICT, fit),
                today("tx2", EventType.DISTRICT, fit),
                today("mi1", EventType.DISTRICT, fim),
                today("mi2", EventType.DISTRICT, fim),
                today("mi3", EventType.DISTRICT, fim),
                today("div", EventType.CHAMPIONSHIP_DIVISION),
            )
        val rows = EventFeed.todayRows(live)
        assertEquals(
            listOf(
                "live-championship",
                "live-district-fim",
                "live-district-fit",
                "live-regionals",
                "live-more",
                "live-offseason",
            ),
            rows.keys(),
        )
        assertEquals(
            listOf(
                RowTitle.Championship,
                RowTitle.District("FIRST In Michigan"),
                RowTitle.District("FIRST In Texas"),
                RowTitle.Regionals,
                RowTitle.MoreEvents,
                RowTitle.Offseason,
            ),
            rows.map { it.title },
        )
    }

    @Test fun todayRows_championshipFinalsLeadDivisionsByName() {
        val live =
            listOf(
                today("Newton", EventType.CHAMPIONSHIP_DIVISION),
                today("archimedes", EventType.CHAMPIONSHIP_DIVISION),
                today("Einstein", EventType.CHAMPIONSHIP_FINALS),
            ) + (1..5).map { today("reg$it") }
        val cmp = EventFeed.todayRows(live).first()
        assertEquals(listOf("Einstein", "archimedes", "Newton"), cmp.eventKeys())
    }

    @Test fun todayRows_districtsOrderedByName_dcmpFirstWithinRow() {
        val zeta = District("zz", "Zeta")
        val alpha = District("aa", "Alpha")
        val live =
            listOf(
                today("z1", EventType.DISTRICT, zeta),
                today("z2", EventType.DISTRICT, zeta),
                today("a1", EventType.DISTRICT, alpha),
                today("a2", EventType.DISTRICT, alpha),
                today("Mi Lansing", EventType.DISTRICT, fim),
                today("Mi Division", EventType.DISTRICT_CHAMPIONSHIP_DIVISION, fim),
                today("Mi Alpena", EventType.DISTRICT, fim),
                today("Mi State", EventType.DISTRICT_CHAMPIONSHIP, fim),
            )
        val rows = EventFeed.todayRows(live)
        // Alphabetical by district name, regardless of how many events each has today: rows stay in
        // a predictable place. FIM has the most events but sorts by its name.
        assertEquals(
            listOf("live-district-aa", "live-district-fim", "live-district-zz"),
            rows.keys(),
        )
        assertEquals(
            listOf("Mi State", "Mi Division", "Mi Alpena", "Mi Lansing"),
            rows.first { it.key == "live-district-fim" }.eventKeys(),
        )
    }

    @Test fun todayRows_leftoversGoToMoreEventsNotRegionals() {
        val live =
            listOf(
                today("mi1", EventType.DISTRICT, fim),
                today("mi2", EventType.DISTRICT, fim),
                today("granite", EventType.DISTRICT, ne),
                today("utah"),
                today("arizona"),
                today("remote", EventType.REMOTE),
                today("foc", EventType.FESTIVAL_OF_CHAMPIONS),
                today("mystery", EventType.UNLABELED),
            )
        val rows = EventFeed.todayRows(live)
        assertEquals(listOf("live-district-fim", "live-regionals", "live-more"), rows.keys())
        // Regionals holds only true regionals; a single-event district gets no row of its own.
        assertEquals(listOf("arizona", "utah"), rows[1].eventKeys())
        assertEquals(listOf("foc", "granite", "mystery", "remote"), rows[2].eventKeys())
    }

    @Test fun feedRows_marksOnlyFirstRowAfterTodayBlock() {
        val feed =
            EventFeed.from(
                listOf(
                    today("live"),
                    event("up", today.plusDays(3)),
                    event("past", today.minusDays(3)),
                ),
                today,
            )
        assertEquals(listOf(false, true, false), feed.rows().map { it.startsAfterToday })
        // With no Upcoming, Recent is the first row past today and takes the boundary.
        val noUpcoming =
            EventFeed.from(listOf(today("live"), event("past", today.minusDays(3))), today)
        assertEquals(listOf(false, true), noUpcoming.rows().map { it.startsAfterToday })
        // Nothing today: no block to close off, so no extra gap at the top of the feed.
        val nothingToday = EventFeed.from(listOf(event("up", today.plusDays(3))), today)
        assertEquals(listOf(false), nothingToday.rows().map { it.startsAfterToday })
    }

    @Test fun feedRows_appendUpcomingAndRecentAfterToday() {
        val feed =
            EventFeed.from(
                listOf(
                    today("live"),
                    event("up", today.plusDays(3)),
                    event("past", today.minusDays(3)),
                ),
                today,
            )
        assertEquals(listOf("live", "upcoming", "recent"), feed.rows().keys())
        assertEquals(
            listOf(RowTitle.HappeningNow, RowTitle.Upcoming, RowTitle.Recent),
            feed.rows().map { it.title },
        )
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

    @Test fun streamPlatforms_distinctInEnumOrderWithoutOther() {
        val e =
            event(
                "k",
                today,
                webcasts =
                    listOf(
                        Webcast(WebcastType.TWITCH, "a"),
                        Webcast(WebcastType.OTHER, "b"),
                        Webcast(WebcastType.YOUTUBE, "c"),
                        Webcast(WebcastType.TWITCH, "d"),
                    ),
            )
        assertEquals(listOf(WebcastType.YOUTUBE, WebcastType.TWITCH), e.streamPlatforms)
        val otherOnly = event("k", today, webcasts = listOf(Webcast(WebcastType.OTHER, "x")))
        assertTrue(otherOnly.streamPlatforms.isEmpty())
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
