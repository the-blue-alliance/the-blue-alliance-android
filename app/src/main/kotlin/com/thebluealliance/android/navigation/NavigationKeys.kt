package com.thebluealliance.android.navigation

import androidx.navigation3.runtime.NavKey
import com.thebluealliance.android.ui.events.detail.EventDetailTab
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable data object Events : Screen
    @Serializable data object Teams : Screen
    @Serializable data object Districts : Screen
    @Serializable data object RegionalAdvancement : Screen
    @Serializable data object More : Screen
    @Serializable data class EventDetail(val eventKey: String, val initialTab: EventDetailTab = EventDetailTab.INFO) : Screen
    @Serializable data class TeamDetail(val teamKey: String, val initialTab: Int = TAB_INFO) : Screen {
        companion object {
            const val TAB_INFO = 0
            const val TAB_EVENTS = 1
            const val TAB_MEDIA = 2
        }
    }
    @Serializable data class MatchDetail(val matchKey: String) : Screen
    @Serializable data class TeamEventDetail(val teamKey: String, val eventKey: String) : Screen
    @Serializable data class DistrictDetail(val districtKey: String) : Screen
    @Serializable data object MyTBA : Screen
    @Serializable data object Search : Screen
    @Serializable data object Settings : Screen
    @Serializable data object About : Screen
    @Serializable data object Thanks : Screen
}
