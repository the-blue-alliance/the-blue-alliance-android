package com.thebluealliance.android.widget

/**
 * The states the Team Tracking widget renders (see [TeamTrackingWidget] subtitle logic),
 * persisted by [TeamTrackingWorker] as the analytics `widget_state` dimension so usage can be
 * sliced by what the widget was actually showing.
 */
enum class WidgetState(
    val value: String,
) {
    /** "No upcoming events" — nothing tracked coming up. */
    NO_UPCOMING("no_upcoming"),

    /** "Upcoming events" — a future event is scheduled but not happening now. */
    UPCOMING("upcoming"),

    /** The tracked team is at — or just finished (within ~a day) — a live event (EVENT_KEY set). */
    CURRENT_EVENT("current_event"),

    /** Nothing has ever loaded for this widget and the latest refresh failed. */
    LOAD_FAILED("load_failed"),
}
