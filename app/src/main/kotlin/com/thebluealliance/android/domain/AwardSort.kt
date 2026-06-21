package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.AwardType
import com.thebluealliance.android.util.teamNumber

// TBA web's AWARD_SORT_ORDER (consts/award_type.py). The web sorts everything
// else after these in API order; we use the award-type code as a deterministic
// tiebreak instead.
private val displayOrder =
    listOf(
        AwardType.CHAIRMANS,
        AwardType.FOUNDERS,
        AwardType.ENGINEERING_INSPIRATION,
        AwardType.ROOKIE_ALL_STAR,
        AwardType.WOODIE_FLOWERS,
        AwardType.VOLUNTEER,
        AwardType.DEANS_LIST,
        AwardType.WINNER,
        AwardType.FINALIST,
    )

private val priorityByCode: Map<Int, Int> =
    displayOrder.withIndex().associate { (index, type) -> type.code to index }

private val Award.displayPriority: Int
    get() = priorityByCode[awardType] ?: (displayOrder.size + awardType)

fun List<Award>.sortedForDisplay(): List<Award> =
    sortedWith(
        compareBy(
            { it.displayPriority },
            { it.teamKey.teamNumber.toIntOrNull() ?: Int.MAX_VALUE },
            { it.awardee.orEmpty() },
        ),
    )
