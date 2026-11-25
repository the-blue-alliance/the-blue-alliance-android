package com.thebluealliance.android.navigation

import androidx.navigation3.runtime.deeplink.BackStackMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.UriMatchResult
import androidx.navigation3.runtime.deeplink.withBackStack
import com.thebluealliance.android.ui.events.detail.EventDetailTab
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * Maps thebluealliance.com web URLs (the VIEW intent filter in AndroidManifest.xml) to [Screen]s,
 * using Navigation 3's deep link matchers.
 *
 * Each match carries the synthetic back stack a fresh task should start with — the deep-linked
 * screen preceded by its hierarchical parents, rooted at a top-level tab.
 */
class TbaDeepLinks {
    private val matchers: List<BackStackMatcher<Screen, Screen>> =
        buildList {
            // Listed before the event wildcard below: without a `teams` query the two rank equal,
            // and the first-listed match wins ties.
            link<Screen.EventPitMap>("event/{eventKey}/pitmap", query = "teams={teamsCsv}") {
                listOf(Screen.Events(), Screen.EventDetail(it.eventKey), it)
            }
            link<Screen.EventDetail>("event/{eventKey}") { listOf(Screen.Events(), it) }
            link<Screen.MatchDetail>("match/{matchKey}") {
                val eventKey = it.matchKey.substringBefore('_')
                listOf(
                    Screen.Events(),
                    Screen.EventDetail(eventKey, initialTab = EventDetailTab.MATCHES),
                    it,
                )
            }
            link<Screen.TeamDetail>("team/{teamKey}") { listOf(Screen.Teams, it) }
            // /events/{year} outranks the bare /events/.* fallback; a non-numeric year fails to
            // decode and falls through to it, landing on the current season.
            link<Screen.Events>("events/{year}") { listOf(it) }
            link<Screen.Events>("events") { listOf(it) }
            // /teams/{page} pages the web team list; the app shows every team on one screen.
            link<Screen.Teams>("teams") { listOf(it) }
            link<Screen.RegionalAdvancement>("regional_advancement") { listOf(Screen.More, it) }
        }

    /**
     * Returns the synthetic back stack for [request] — the deep-linked screen last — or null if no
     * pattern matches.
     */
    fun match(request: DeepLinkRequest): List<Screen>? =
        matchers
            .mapNotNull { it.match(request) }
            .maxOrNull()
            ?.backStack
            ?.withFragmentTab(request.uri?.fragment)
}

/**
 * Event page tab anchors: the Jinja page's (`event_details.html`) and the PWA's, which renamed two.
 * Media, Scouting and Practice have no app tab, so they open the default tab.
 */
private val EVENT_TAB_FRAGMENTS =
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

/** Opens the deep-linked event on the tab its URL fragment names, e.g. `/event/2025hop#rankings`. */
private fun List<Screen>.withFragmentTab(fragment: String?): List<Screen> {
    val target = lastOrNull() as? Screen.EventDetail ?: return this
    val tab = EVENT_TAB_FRAGMENTS[fragment] ?: return this
    return dropLast(1) + target.copy(initialTab = tab)
}

private val TBA_HOSTS = listOf("www.thebluealliance.com", "thebluealliance.com")

/**
 * Adds matchers for [path] on every TBA host, both exactly and with any trailing subpath (e.g.
 * `/team/frc254/2024`), since the web site nests views under each page.
 */
private inline fun <reified T : Screen> MutableList<BackStackMatcher<Screen, Screen>>.link(
    path: String,
    query: String? = null,
    noinline backStack: (T) -> List<Screen>,
) {
    val querySuffix = query?.let { "?$it" }.orEmpty()
    for (host in TBA_HOSTS) {
        for (pattern in listOf(path, "$path/.*")) {
            add(
                NonEmptyPathArgsMatcher(
                    DeepLinkUri("https://$host/$pattern$querySuffix"),
                    serializer<T>(),
                ).withBackStack { backStack(it.key) },
            )
        }
    }
}

/**
 * A [UriDeepLinkMatcher] that rejects empty path placeholders, so `/event/` doesn't open an event
 * with a blank key.
 */
private class NonEmptyPathArgsMatcher<T : Any>(
    uriPattern: DeepLinkUri,
    serializer: KSerializer<T>,
) : UriDeepLinkMatcher<T>(uriPattern, serializer) {
    override fun matchArguments(
        pathArgs: Map<String, List<String>>,
        queryArgs: Map<String, List<String>>,
        fragmentArgs: Map<String, List<String>>,
    ): UriMatchResult<T>? =
        if (pathArgs.values.any { values -> values.any(String::isEmpty) }) {
            null
        } else {
            super.matchArguments(pathArgs, queryArgs, fragmentArgs)
        }
}
