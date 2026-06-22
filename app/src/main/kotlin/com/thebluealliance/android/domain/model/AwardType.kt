package com.thebluealliance.android.domain.model

/**
 * TBA award_type codes (web: consts/award_type.py) the app refers to by name.
 * [Award.awardType] keeps the raw code since the full upstream enum has ~100 entries;
 * codes without a named constant here are valid, just not individually significant.
 */
enum class AwardType(
    val code: Int,
) {
    CHAIRMANS(0),
    WINNER(1),
    FINALIST(2),
    WOODIE_FLOWERS(3),
    DEANS_LIST(4),
    VOLUNTEER(5),
    FOUNDERS(6),
    ENGINEERING_INSPIRATION(9),
    ROOKIE_ALL_STAR(10),
    ;

    companion object {
        fun fromCode(code: Int): AwardType? = entries.firstOrNull { it.code == code }
    }
}
