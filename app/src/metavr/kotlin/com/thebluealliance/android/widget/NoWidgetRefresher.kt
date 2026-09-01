package com.thebluealliance.android.widget

/** Horizon OS has no home screen to host app widgets, so there is nothing to refresh. */
class NoWidgetRefresher : WidgetRefresher {
    override suspend fun refresh() = Unit
}
