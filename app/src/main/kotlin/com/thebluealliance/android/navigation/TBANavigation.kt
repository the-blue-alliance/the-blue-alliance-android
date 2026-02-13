package com.thebluealliance.android.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Events : Route
    @Serializable data object Teams : Route
    @Serializable data object Districts : Route
    @Serializable data object RegionalAdvancement : Route
    @Serializable data object More : Route
}

sealed interface Screen {
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
