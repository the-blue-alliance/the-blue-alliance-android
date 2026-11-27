package com.thebluealliance.android.navigation

import android.net.Uri
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import com.thebluealliance.android.ui.events.detail.EventDetailTab
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URLDecoder
import java.time.Year

class TbaDeepLinksTest {
    private lateinit var deepLinks: TbaDeepLinks

    @BeforeEach
    fun setUp() {
        // android.net.Uri is a stub in JVM tests; Navigation 3's matchers parse both the patterns
        // and the requests through it.
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } answers { fakeUri(firstArg()) }
        every { Uri.decode(any()) } answers { decode(firstArg()) }
        deepLinks = TbaDeepLinks()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    private fun match(url: String) = deepLinks.match(DeepLinkRequest(url))

    @Test
    fun `event pitmap deeplink maps to pit map screen under its event`() {
        assertEquals(
            listOf(
                Screen.Events(),
                Screen.EventDetail("2026miket"),
                Screen.EventPitMap("2026miket", "frc254,frc1678"),
            ),
            match("https://www.thebluealliance.com/event/2026miket/pitmap?teams=frc254,frc1678"),
        )
    }

    @Test
    fun `event pitmap deeplink without teams has empty csv`() {
        assertEquals(
            Screen.EventPitMap("2026miket", ""),
            match("https://www.thebluealliance.com/event/2026miket/pitmap")?.last(),
        )
    }

    @Test
    fun `event detail deeplink sits on the events list`() {
        assertEquals(
            listOf(Screen.Events(), Screen.EventDetail("2026miket")),
            match("https://www.thebluealliance.com/event/2026miket"),
        )
    }

    @Test
    fun `event subpages and fragments open the event`() {
        assertEquals(
            Screen.EventDetail("2026miket"),
            match("https://www.thebluealliance.com/event/2026miket/teams")?.last(),
        )
    }

    @Test
    fun `event tab fragments open that tab`() {
        val expected =
            mapOf(
                "results" to EventDetailTab.MATCHES,
                "rankings" to EventDetailTab.RANKINGS,
                "awards" to EventDetailTab.AWARDS,
                "teams" to EventDetailTab.TEAMS,
                "event-insights" to EventDetailTab.INSIGHTS,
                "insights" to EventDetailTab.INSIGHTS,
                "district-points" to EventDetailTab.ADVANCEMENT_POINTS,
                "cmp-points" to EventDetailTab.ADVANCEMENT_POINTS,
                "champs-qual-points" to EventDetailTab.ADVANCEMENT_POINTS,
            )
        for ((fragment, tab) in expected) {
            assertEquals(
                listOf(Screen.Events(), Screen.EventDetail("2025hop", initialTab = tab)),
                match("https://www.thebluealliance.com/event/2025hop#$fragment"),
                fragment,
            )
        }
    }

    @Test
    fun `unknown or empty event fragments open the default tab`() {
        for (url in listOf("#media", "#scouting", "#practice", "#bogus", "#", "")) {
            assertEquals(
                listOf(Screen.Events(), Screen.EventDetail("2025hop")),
                match("https://www.thebluealliance.com/event/2025hop$url"),
                url,
            )
        }
    }

    @Test
    fun `event fragments only retarget the event itself`() {
        assertEquals(
            listOf(
                Screen.Events(),
                Screen.EventDetail("2026miket"),
                Screen.EventPitMap("2026miket", ""),
            ),
            match("https://www.thebluealliance.com/event/2026miket/pitmap#rankings"),
        )
    }

    @Test
    fun `bare host is accepted`() {
        assertEquals(
            listOf(Screen.Events(), Screen.EventDetail("2026miket")),
            match("https://thebluealliance.com/event/2026miket"),
        )
    }

    @Test
    fun `team deeplink sits on the teams list`() {
        assertEquals(
            listOf(Screen.Teams, Screen.TeamDetail("frc254")),
            match("https://www.thebluealliance.com/team/frc254"),
        )
    }

    @Test
    fun `team year deeplink opens that year`() {
        assertEquals(
            listOf(Screen.Teams, teamDetail("254", year = 2024)),
            match("https://www.thebluealliance.com/team/254/2024"),
        )
    }

    @Test
    fun `team subpages without a year open the default year`() {
        assertEquals(
            listOf(Screen.Teams, Screen.TeamDetail("254")),
            match("https://www.thebluealliance.com/team/254/history"),
        )
    }

    @Test
    fun `event insights deeplink opens the insights tab`() {
        assertEquals(
            listOf(
                Screen.Events(),
                Screen.EventDetail("2025hop", initialTab = EventDetailTab.INSIGHTS),
            ),
            match("https://www.thebluealliance.com/event/2025hop/insights"),
        )
    }

    @Test
    fun `district events deeplink opens that district's year`() {
        assertEquals(
            listOf(Screen.Districts, Screen.DistrictDetail("2025fim")),
            match("https://www.thebluealliance.com/events/fim/2025"),
        )
    }

    @Test
    fun `district events deeplink without a year opens the current year`() {
        assertEquals(
            listOf(Screen.Districts, Screen.DistrictDetail("${Year.now().value}ne")),
            match("https://www.thebluealliance.com/events/ne"),
        )
    }

    @Test
    fun `regional events deeplinks are not districts`() {
        for (url in listOf("/events/regional", "/events/regional/2025")) {
            val screen = match("https://www.thebluealliance.com$url")?.last()
            assertFalse(screen is Screen.DistrictDetail, url)
        }
    }

    @Test
    fun `match deeplink sits on its event's matches tab`() {
        assertEquals(
            listOf(
                Screen.Events(),
                Screen.EventDetail("2024micmp4", initialTab = EventDetailTab.MATCHES),
                Screen.MatchDetail("2024micmp4_f1m1"),
            ),
            match("https://www.thebluealliance.com/match/2024micmp4_f1m1"),
        )
    }

    @Test
    fun `events deeplinks honor an optional year`() {
        assertEquals(listOf(Screen.Events()), match("https://www.thebluealliance.com/events"))
        assertEquals(
            listOf(Screen.Events(2024)),
            match("https://www.thebluealliance.com/events/2024"),
        )
        assertEquals(
            listOf(Screen.Events(2024)),
            match("https://www.thebluealliance.com/events/2024/district"),
        )
        assertEquals(listOf(Screen.Events()), match("https://www.thebluealliance.com/events/abc"))
    }

    @Test
    fun `teams deeplinks ignore the page`() {
        assertEquals(listOf(Screen.Teams), match("https://www.thebluealliance.com/teams"))
        assertEquals(listOf(Screen.Teams), match("https://www.thebluealliance.com/teams/2"))
    }

    @Test
    fun `regional advancement deeplinks sit under more`() {
        val expected = listOf(Screen.More, Screen.RegionalAdvancement)
        assertEquals(expected, match("https://www.thebluealliance.com/regional_advancement"))
        assertEquals(
            expected,
            match("https://www.thebluealliance.com/regional_advancement/2026/rankings"),
        )
    }

    @Test
    fun `unknown urls do not match`() {
        assertNull(match("https://www.thebluealliance.com"))
        assertNull(match("https://www.thebluealliance.com/districts"))
        assertNull(match("https://www.thebluealliance.com/event/"))
        assertNull(match("https://www.thebluealliance.com/team/"))
        assertNull(match("https://www.example.com/event/2026miket"))
    }

    /** A [Screen.TeamDetail] for [year], built by name so this compiles before the field exists. */
    private fun teamDetail(
        teamKey: String,
        year: Int,
    ): Screen.TeamDetail = Json.decodeFromString("""{"teamKey":"$teamKey","year":$year}""")

    private fun decode(s: String): String = URLDecoder.decode(s.replace("+", "%2B"), Charsets.UTF_8)

    /** A lenient `scheme://authority/path?query#fragment` parser mirroring android.net.Uri. */
    private fun fakeUri(s: String): Uri {
        val fragmentPart = if ('#' in s) s.substringAfter('#') else null
        val beforeFragment = s.substringBefore('#')
        val queryPart = if ('?' in beforeFragment) beforeFragment.substringAfter('?') else null
        val hierarchy = beforeFragment.substringBefore('?')
        val schemePart = hierarchy.substringBefore("://")
        val rest = hierarchy.substringAfter("://")
        val params =
            queryPart
                ?.split('&')
                ?.filter { it.isNotEmpty() }
                ?.map { decode(it.substringBefore('=')) to decode(it.substringAfter('=', "")) }
                .orEmpty()
        return mockk {
            every { this@mockk.toString() } returns s
            every { scheme } returns schemePart
            every { authority } returns rest.substringBefore('/')
            every { pathSegments } returns
                rest
                    .substringAfter('/', "")
                    .split('/')
                    .filter { it.isNotEmpty() }
                    .map(::decode)
            every { query } returns queryPart?.let(::decode)
            every { fragment } returns fragmentPart?.let(::decode)
            every { isHierarchical } returns true
            every { queryParameterNames } returns
                params.map { it.first }.toCollection(LinkedHashSet())
            every { getQueryParameters(any()) } answers {
                val key = firstArg<String>()
                params.filter { it.first == key }.map { it.second }
            }
        }
    }
}
