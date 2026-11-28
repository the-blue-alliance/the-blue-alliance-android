package com.thebluealliance.android.ui.events.detail.tabs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventInfoTabLogicTest {
    @Test
    fun `venue repeated as the first address line is shown once`() {
        // 2005ga, as served by /api/v3/event/2005ga.
        val address = "Gwinnett Civic Center\r\nDuluth, GA 30095\r\nUSA"
        assertEquals(
            EventVenueLines(venue = null, address = address),
            eventVenueLines("Gwinnett Civic Center", address),
        )
    }

    @Test
    fun `venue repeated in a different case is shown once`() {
        // 2016ohsc: "HIgh" in the venue, "High" in the address.
        val ohsc = "Worthington Kilbourne High School\r\n1499 Hard Road\r\nColumbus, OH 43235"
        assertEquals(
            EventVenueLines(venue = null, address = ohsc),
            eventVenueLines("Worthington Kilbourne HIgh School", ohsc),
        )
        // 2017mems: lower case in the address.
        val mems =
            "Messalonskee high school\r\n131 Messalonskee High Drive\r\n" +
                "Oakland, Maine, United States"
        assertEquals(
            EventVenueLines(venue = null, address = mems),
            eventVenueLines("Messalonskee High School", mems),
        )
    }

    @Test
    fun `venue repeated with surrounding spaces is shown once`() {
        // 2014txrr: trailing space on the address's first line.
        val txrr =
            "Austin Convention Center Exhibit Hall 1 \r\n500 E Cesar Chavez St.\r\n" +
                "Austin, TX 78701\r\nUSA"
        assertEquals(
            EventVenueLines(venue = null, address = txrr),
            eventVenueLines("Austin Convention Center Exhibit Hall 1", txrr),
        )
        // 2015glbbb: leading space on the venue only.
        val glbbb = "Herbert Henry Dow High School\r\n3901 N Saginaw Rd\r\nMidland, MI 48640"
        assertEquals(
            EventVenueLines(venue = null, address = glbbb),
            eventVenueLines(" Herbert Henry Dow High School", glbbb),
        )
        // 2018wvrox: trailing space on the venue only.
        val wvrox = "West Virginia University\r\nMorgantown, WV, USA"
        assertEquals(
            EventVenueLines(venue = null, address = wvrox),
            eventVenueLines("West Virginia University ", wvrox),
        )
    }

    @Test
    fun `venue repeated without its apostrophe is shown once`() {
        // 2003az: curly apostrophe in the venue, none in the address.
        val address = "Arizona Veterans Coliseum\r\nPhoenix, AZ 85001\r\nUSA"
        assertEquals(
            EventVenueLines(venue = null, address = address),
            eventVenueLines("Arizona Veteran’s Coliseum", address),
        )
    }

    @Test
    fun `venue repeated in a newline-separated address is shown once`() {
        // 2019chcmp uses bare \n where most events use \r\n.
        val address = "George Mason University\n4400 University Drive\nFairfax, VA 22030\nUSA"
        assertEquals(
            EventVenueLines(venue = null, address = address),
            eventVenueLines("George Mason University", address),
        )
    }

    @Test
    fun `address that repeats the venue on its second line too is shown unchanged`() {
        // 2010md: the address itself lists the venue twice; only the separate venue line goes.
        val address =
            "Baltimore Convention Center\r\nBaltimore Convention Center\r\n" +
                "Baltimore, MD 21201\r\nUSA"
        assertEquals(
            EventVenueLines(venue = null, address = address),
            eventVenueLines("Baltimore Convention Center", address),
        )
    }

    @Test
    fun `single-line address equal to the venue is shown once`() {
        // The address line stays because it is the tappable Maps link.
        assertEquals(
            EventVenueLines(venue = null, address = "Gwinnett Civic Center"),
            eventVenueLines("Gwinnett Civic Center", "Gwinnett Civic Center"),
        )
    }

    @Test
    fun `modern venue and street address are both shown`() {
        // 2026necmp.
        assertEquals(
            EventVenueLines(venue = "Eastern States Exposition", address = "1305 Memorial Ave"),
            eventVenueLines("Eastern States Exposition", "1305 Memorial Ave"),
        )
    }

    @Test
    fun `venue that only starts a different first address line is kept`() {
        // 2001ct: "Hartford" is not the line "Hartford, CT".
        val ct = "Hartford, CT\r\nUSA"
        assertEquals(
            EventVenueLines(venue = "Hartford", address = ct),
            eventVenueLines("Hartford", ct),
        )
        // 2026mndu: venue and the street line share no line.
        val mndu = "Duluth Entertainment Convention Center 350 Harbor Drive"
        assertEquals(
            EventVenueLines(venue = "DECC Arena/South Pioneer Hall", address = mndu),
            eventVenueLines("DECC Arena/South Pioneer Hall", mndu),
        )
    }

    @Test
    fun `venue that only appears later in the address is kept`() {
        // 2009cmp: "Georgia Dome" is inside the street name, not the first line.
        val address = "1 Georgia Dome Dr, Atlanta, GA 30313, USA"
        assertEquals(
            EventVenueLines(venue = "Georgia Dome", address = address),
            eventVenueLines("Georgia Dome", address),
        )
    }

    @Test
    fun `blank venue is not shown`() {
        // 2008ga has location_name "".
        assertEquals(
            EventVenueLines(venue = null, address = "Duluth, GA, USA"),
            eventVenueLines("", "Duluth, GA, USA"),
        )
    }

    @Test
    fun `missing venue is not shown`() {
        // No location_name in the payload.
        assertEquals(
            EventVenueLines(venue = null, address = "Duluth, GA, USA"),
            eventVenueLines(null, "Duluth, GA, USA"),
        )
    }

    @Test
    fun `blank address is not shown`() {
        // 2008ios has location_name "" and address "".
        assertEquals(EventVenueLines(venue = null, address = null), eventVenueLines("", ""))
    }

    @Test
    fun `venue is kept when there is no address`() {
        // No address in the payload.
        assertEquals(
            EventVenueLines(venue = "Gwinnett Civic Center", address = null),
            eventVenueLines("Gwinnett Civic Center", null),
        )
    }
}
