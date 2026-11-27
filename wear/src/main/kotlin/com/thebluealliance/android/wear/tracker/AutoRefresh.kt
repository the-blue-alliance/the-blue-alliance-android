package com.thebluealliance.android.wear.tracker

/** Whether opening the app or starting its process should schedule refreshes on its own. */
internal object AutoRefresh {
    /**
     * No team means nothing to fetch, and a team the API said doesn't exist would only 404 again.
     * Picking a team in the config screen schedules the refresh in both cases.
     */
    fun allowed(
        teamNumber: String,
        teamNotFound: Boolean,
    ): Boolean = teamNumber.isNotBlank() && !teamNotFound
}
