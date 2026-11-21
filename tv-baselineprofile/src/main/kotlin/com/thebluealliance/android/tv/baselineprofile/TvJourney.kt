package com.thebluealliance.android.tv.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

/** The release-type variants the plugin profiles keep :tv's release applicationId. */
const val TV_PACKAGE = "com.thebluealliance.androidclient"

private const val CONTENT_TIMEOUT_MS = 15_000L
private const val STEP_TIMEOUT_MS = 5_000L

// The bundled sample feed (the profiled variants use USE_MOCK_DATA) is re-anchored to today, so these
// rows and events exist on every run. Card content descriptions read "<name>, <place>, <status>, on …".
private const val MAX_ROWS = 12
private const val MAX_CARDS_PER_ROW = 6

// An event in the sample feed with two webcasts, so selecting it opens the picker instead of handing
// off to YouTube/Twitch.
private const val MULTI_WEBCAST_EVENT = "FIRST Championship — Houston"

private fun aboutButton(): BySelector = By.desc("About")

// Any event card: every one has a "…, on <platform>" content description.
private fun eventCard(): BySelector = By.descContains(", on ")

/** Cold-starts the app and waits until the feed has actually rendered event cards. */
fun MacrobenchmarkScope.startAndWaitForFeed() {
    pressHome()
    startActivityAndWait()
    device.waitForFeed()
}

fun UiDevice.waitForFeed() {
    check(wait(Until.hasObject(eventCard()), CONTENT_TIMEOUT_MS)) {
        "Feed never rendered an event card"
    }
    check(hasObject(aboutButton())) { "Feed header has no About button" }
}

/**
 * The profiled journey, D-pad only (TV has no touch): move through a few rows and cards, open and
 * dismiss a webcast picker, then open About, scroll it, and come back to the feed.
 */
fun MacrobenchmarkScope.browseFeedAndAbout() {
    device.run {
        // Focus starts on the first card. Wander across and down the rows so the row/card focus,
        // pivot-scroll and thumbnail paths all run.
        repeat(2) { pressAndSettle { pressDPadRight() } }
        repeat(2) { pressAndSettle { pressDPadDown() } }
        pressAndSettle { pressDPadRight() }

        // Back to the top, then hunt for a multi-webcast card and open its picker. Never pick a
        // webcast: that would launch another app.
        repeat(MAX_ROWS) { pressAndSettle { pressDPadUp() } }
        check(focusCard(MULTI_WEBCAST_EVENT)) { "Couldn't focus \"$MULTI_WEBCAST_EVENT\"" }
        pressAndSettle { pressDPadCenter() }
        check(wait(Until.hasObject(By.text("Choose a webcast")), STEP_TIMEOUT_MS)) {
            "Webcast picker didn't open"
        }
        pressAndSettle { pressBack() }
        check(wait(Until.gone(By.text("Choose a webcast")), STEP_TIMEOUT_MS)) {
            "Webcast picker didn't close"
        }

        // Up to the header's ⓘ and into About.
        check(focusAbout()) { "Couldn't focus the About button" }
        pressAndSettle { pressDPadCenter() }
        check(wait(Until.hasObject(By.text("Thanks")), STEP_TIMEOUT_MS)) { "About didn't open" }
        repeat(6) { pressAndSettle { pressDPadDown() } }
        pressAndSettle { pressBack() }
        waitForFeed()
    }
}

private inline fun UiDevice.pressAndSettle(press: UiDevice.() -> Boolean) {
    press()
    waitForIdle()
}

/**
 * True when the node matching [selector] is focused, or sits inside the focused node (a card's
 * focusable container wraps the art that carries the content description). [selector] must build a
 * fresh selector on each call: BySelector is mutable.
 */
private fun UiDevice.isFocused(selector: () -> BySelector): Boolean =
    hasObject(selector().focused(true)) || hasObject(By.focused(true).hasDescendant(selector()))

private fun UiDevice.focusCard(eventName: String): Boolean {
    val target = { By.descStartsWith(eventName) }
    repeat(MAX_ROWS) {
        if (isFocused(target)) return true
        if (hasObject(target())) {
            // On screen, but maybe in a different row: sweep this row, then return to its start.
            repeat(MAX_CARDS_PER_ROW) {
                pressAndSettle { pressDPadRight() }
                if (isFocused(target)) return true
            }
            repeat(MAX_CARDS_PER_ROW) { pressAndSettle { pressDPadLeft() } }
        }
        pressAndSettle { pressDPadDown() }
    }
    return isFocused(target)
}

private fun UiDevice.focusAbout(): Boolean {
    repeat(MAX_ROWS) {
        if (isFocused(::aboutButton)) return true
        pressAndSettle { pressDPadUp() }
    }
    return isFocused(::aboutButton)
}
