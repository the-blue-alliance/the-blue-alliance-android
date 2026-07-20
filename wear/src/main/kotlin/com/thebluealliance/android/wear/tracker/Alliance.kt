package com.thebluealliance.android.wear.tracker

import com.thebluealliance.android.data.remote.dto.MatchDto

/**
 * Which alliance a team competes on in a match.
 *
 * TBA encodes this as the raw strings "red"/"blue" at the API and SharedPreferences edges;
 * [key] is that wire/storage form. Everything above those edges works with the enum so the
 * codebase never compares bare "red"/"blue" literals.
 */
enum class Alliance(
    val key: String,
) {
    RED("red"),
    BLUE("blue"),
    ;

    companion object {
        /** Parse a raw TBA/pref value; returns null for blank or anything unrecognized. */
        fun fromKey(raw: String?): Alliance? = entries.firstOrNull { it.key == raw }

        /** The alliance [teamKey] competes on in [match], or null if it is on neither. */
        fun of(
            match: MatchDto,
            teamKey: String,
        ): Alliance? =
            when (teamKey) {
                in (match.alliances?.red?.teamKeys ?: emptyList()) -> RED
                in (match.alliances?.blue?.teamKeys ?: emptyList()) -> BLUE
                else -> null
            }
    }
}
