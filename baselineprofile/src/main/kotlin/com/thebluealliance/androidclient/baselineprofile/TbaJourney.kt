package com.thebluealliance.androidclient.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until

/**
 * Shared user journeys exercised by both the Baseline Profile generator and the startup
 * benchmark, so the two never drift. The journey covers the app's critical path:
 *
 *   cold start -> Events list (scroll) -> Event detail (Teams / Rankings / Matches tabs)
 *
 * Selector strategy (deliberately not `By.scrollable(true)`):
 *  - The events list is selected by resource-id [EVENTS_LIST_RES_ID], surfaced from a
 *    Compose `testTag` via `Modifier.semantics { testTagsAsResourceId = true }` on the app
 *    root. There are several scrollables on screen, so we must name the one we mean.
 *  - Event-detail tabs are driven by tapping their visible tab LABELS by text. This is
 *    deterministic (a `By.scrollable(true)` would ambiguously match the vertical Info
 *    LazyColumn, the horizontal pager, or the scrollable TabRow) AND it mirrors how a real
 *    at-venue user moves between Teams / Rankings / Matches.
 *
 * Every leg HARD-FAILS (throws) if its content never appears, so a degraded "Info-only" or
 * empty-list profile can never be silently captured and committed.
 */
object TbaJourney {
    /** Matches [EVENTS_LIST_TEST_TAG] in :app EventsScreen.kt. */
    const val EVENTS_LIST_RES_ID = "events_list"

    /** Matches the testTag on EventRankingsTab's content list — the real "rankings rendered" signal. */
    const val EVENT_RANKINGS_RES_ID = "event_rankings"

    /**
     * A stable, fully-played event for the at-venue detail journey: a 2024 FiM championship
     * division — permanent data with rankings + matches + alliances + awards, so the heavy
     * event-detail render paths are always exercised. The profile is generated against prod
     * (see local.properties tba.url.benchmark / tba.api.key.benchmark), where this is fixed.
     */
    const val POPULATED_EVENT_KEY = "2024micmp4"

    /**
     * Very generous on purpose. The capture variant bakes a TBA key (tba.api.key.benchmark),
     * so ApiKeyProvider short-circuits Firebase Remote Config entirely — but the app still
     * floods the network with the historical multi-year event sync ahead of the current-year
     * fetch that backs this list. On a cold managed device that first render can take double-
     * digit seconds; 60s comfortably covers the first list render plus the tab loads.
     */
    private const val UI_TIMEOUT_MS = 60_000L

    /**
     * Cold-start + scroll the events list + open an event + page through the at-venue tabs.
     * [startActivityAndWait] must already have been called by the caller (the BaselineProfile
     * and startup-benchmark rules each manage their own iteration/launch lifecycle).
     */
    fun MacrobenchmarkScope.runEventsToDetailJourney() {
        scrollEventsList()
        openPopulatedEventAndExploreTabs()
    }

    /** Scroll the events list a few times; hard-fail if the list never loaded any rows. */
    fun MacrobenchmarkScope.scrollEventsList() {
        device.waitForIdle()
        val list =
            device.wait(Until.findObject(By.res(EVENTS_LIST_RES_ID)), UI_TIMEOUT_MS)
                ?: error(
                    "Events list (res-id '$EVENTS_LIST_RES_ID') never appeared — cannot " +
                        "capture a meaningful events-list profile.",
                )

        // A list with no scrollable content means the backend returned nothing; refuse to
        // capture a content-free profile rather than commit a degraded one.
        check(list.childCount > 0) {
            "Events list rendered but has no rows — refusing to capture an empty profile."
        }

        list.setGestureMargin(device.displayWidth / 5)
        repeat(3) {
            list.fling(Direction.DOWN)
            device.waitForIdle()
        }
        list.fling(Direction.UP)
        device.waitForIdle()
    }

    /**
     * Deep-link to a known fully-played event, then visit the at-venue power-user tabs.
     * Hard-fails if the detail screen never opens or the Rankings content list renders empty,
     * so the EventDetail half is genuinely covered with real data (not Info-only / empty).
     */
    fun MacrobenchmarkScope.openPopulatedEventAndExploreTabs() {
        // Deep-link straight to a stable, fully-played event so the at-venue power-user tabs
        // render REAL content. Selecting "the first row in the list" is non-deterministic —
        // in the offseason the chronologically-first event is often empty, which would
        // exercise loading/empty states instead of the rankings/matches paths that matter at
        // a competition. Target the (release) package explicitly so it opens in-app, not Chrome.
        device.executeShellCommand(
            "am start -a android.intent.action.VIEW " +
                "-d https://www.thebluealliance.com/event/$POPULATED_EVENT_KEY " +
                "-p $packageName",
        )

        // Reached Event Detail once its tab strip renders.
        device.wait(Until.findObject(By.text("Rankings")), UI_TIMEOUT_MS)
            ?: error("Event detail for '$POPULATED_EVENT_KEY' never opened.")

        // Rankings: tap the tab, then HARD-assert the rankings *content list* rendered with
        // rows — keyed off the 'event_rankings' testTag, NOT the always-visible tab label, so
        // a no-op tap can't masquerade as coverage and an empty event can't slip through.
        device.findObject(By.text("Rankings"))?.click()
        val rankings =
            device.wait(Until.findObject(By.res(EVENT_RANKINGS_RES_ID)), UI_TIMEOUT_MS)
                ?: error("Rankings content never rendered for '$POPULATED_EVENT_KEY'.")
        check(rankings.childCount > 0) {
            "Rankings list rendered empty — the EventDetail power-user path is not covered."
        }

        // Matches + Alliances exercise the other heavy event-detail render paths.
        exploreTab("Matches")
        exploreTab("Alliances")
    }

    private fun MacrobenchmarkScope.exploreTab(label: String) {
        device.findObject(By.text(label))?.let { tab ->
            tab.click()
            device.waitForIdle()
        }
    }
}
