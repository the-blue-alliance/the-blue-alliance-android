package com.thebluealliance.android.widget

/**
 * Process-start maintenance for the home-screen widgets. Horizon OS has no widget host,
 * so that distribution ships no widgets and no implementation to run.
 */
interface WidgetRefresher {
    /** Refreshes bound widgets and republishes picker previews after an app update. */
    suspend fun refresh()
}
