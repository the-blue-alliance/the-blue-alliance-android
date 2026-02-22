package com.thebluealliance.android.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable data object Events : Screen
    @Serializable data object Teams : Screen
    @Serializable data object Districts : Screen
    @Serializable data object More : Screen
    @Serializable data class EventDetail(val eventKey: String) : Screen
    @Serializable data class TeamDetail(val teamKey: String) : Screen
    @Serializable data class MatchDetail(val matchKey: String) : Screen
    @Serializable data class TeamEventDetail(val teamKey: String, val eventKey: String) : Screen
    @Serializable data class DistrictDetail(val districtKey: String) : Screen
    @Serializable data object MyTBA : Screen
    @Serializable data object Search : Screen
    @Serializable data object Settings : Screen
    @Serializable data object About : Screen
    @Serializable data object Thanks : Screen
}

