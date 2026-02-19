package com.thebluealliance.android.domain.model

import android.R.attr.type
import com.google.common.io.Files.map

data class Event(
    val key: String,
    val name: String,
    val eventCode: String,
    val year: Int,
    val type: Int?,
    val district: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val startDate: String?,
    val endDate: String?,
    val week: Int?,
    val shortName: String?,
    val website: String?,
    val timezone: String?,
    val locationName: String?,
    val address: String?,
    val gmapsUrl: String?,
    val webcasts: List<Webcast>,
    val playoffType: PlayoffType,
)

data class Webcast(
    val type: String,
    val channel: String,
    val file: String?,
)

enum class PlayoffType(
    val typeInt: Int,
) {
    BRACKET_16_TEAM(typeInt = 1),
    BRACKET_8_TEAM(typeInt = 0),
    BRACKET_4_TEAM(typeInt = 2),
    BRACKET_2_TEAM(typeInt = 9),

    // Used only in 2015
    AVERAGE_SCORE_8_TEAM(typeInt = 3),

    ROUND_ROBIN_6_TEAM(typeInt = 4),

    // Pre-2022 Double Elim bracket (unofficial events)
    LEGACY_DOUBLE_ELIM_8_TEAM(typeInt = 5),

    // 2022+ Double Elimination bracket
    DOUBLE_ELIM_8_TEAM(typeInt = 10),

    // Districts with 4 divisions
    DOUBLE_ELIM_4_TEAM(typeInt = 11),

    BEST_OF_5(typeInt = 6),
    BEST_OF_3(typeInt = 7),

    OTHER(typeInt = 8);

    companion object {
        private val typeIntMap = entries.associateBy(PlayoffType::typeInt)
        fun fromInt(typeInt: Int): PlayoffType {
            return typeIntMap[typeInt] ?: OTHER
        }
    }
}