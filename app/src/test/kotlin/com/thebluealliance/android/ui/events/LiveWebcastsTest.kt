package com.thebluealliance.android.ui.events

import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.PlayoffType
import com.thebluealliance.android.domain.model.Webcast
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class LiveWebcastsTest {
    private fun event(timezone: String? = "America/Chicago") =
        Event(
            key = "2026txhou",
            name = "FIT District Houston Event",
            eventCode = "txhou",
            year = 2026,
            type = null,
            district = null,
            city = null,
            state = null,
            country = null,
            startDate = null,
            endDate = null,
            week = null,
            shortName = "Houston",
            website = null,
            timezone = timezone,
            locationName = null,
            address = null,
            gmapsUrl = null,
            webcasts = emptyList(),
            playoffType = PlayoffType.BRACKET_8_TEAM,
        )

    private val twitch = Webcast("twitch", "firstintexas", null, null)
    private val saturday = Webcast("youtube", "sat", null, "2026-03-14")
    private val sunday = Webcast("youtube", "sun", null, "2026-03-15")
    private val online = mapOf("2026txhou" to listOf(twitch, saturday, sunday, twitch))

    // Sat 2026-03-14 23:30 in Houston, already Sunday in UTC.
    private val saturdayNight = Instant.parse("2026-03-15T04:30:00Z")

    private fun live(
        event: Event = event(),
        now: Instant = saturdayNight,
        canOpen: (Webcast) -> Boolean = { true },
    ) = liveWebcastsFor(event, online, now, canOpen)

    @Test
    fun `a dated stream counts only on its day in the event's time zone`() {
        assertEquals(listOf(twitch, saturday), live())
    }

    @Test
    fun `the next local day switches to that day's stream`() {
        assertEquals(listOf(twitch, sunday), live(now = Instant.parse("2026-03-15T15:00:00Z")))
    }

    @Test
    fun `an unknown time zone falls back to the device's without failing`() {
        assertEquals(twitch, live(event(timezone = "Not/AZone")).first())
    }

    @Test
    fun `webcasts the app can't open are dropped`() {
        assertEquals(listOf(saturday), live(canOpen = { it.type == "youtube" }))
    }

    @Test
    fun `an event that isn't live has none`() {
        assertEquals(
            emptyList<Webcast>(),
            liveWebcastsFor(event(), emptyMap(), saturdayNight) { true },
        )
    }

    @Test
    fun `the picker names streams by channel and platform`() {
        assertEquals("firstintexas (Twitch)", webcastChannelLabel(twitch))
        assertEquals("sat (YouTube)", webcastChannelLabel(saturday))
    }
}
