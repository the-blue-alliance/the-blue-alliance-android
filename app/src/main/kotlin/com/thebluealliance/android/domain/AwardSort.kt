package com.thebluealliance.android.domain

import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.util.teamNumber

// TBA web's AWARD_SORT_ORDER (consts/award_type.py): Chairman's/Impact, Founders,
// Engineering Inspiration, Rookie All Star, Woodie Flowers, Volunteer, Dean's List,
// Winner, Finalist. The web sorts everything else after these in API order; we use
// award type as a deterministic tiebreak instead.
private val awardTypePriority: Map<Int, Int> =
    listOf(0, 6, 9, 10, 3, 5, 4, 1, 2)
        .withIndex()
        .associate { (index, awardType) -> awardType to index }

private val Award.displayPriority: Int
    get() = awardTypePriority[awardType] ?: (awardTypePriority.size + awardType)

fun List<Award>.sortedForDisplay(): List<Award> =
    sortedWith(
        compareBy(
            { it.displayPriority },
            { it.teamKey.teamNumber.toIntOrNull() ?: Int.MAX_VALUE },
            { it.awardee.orEmpty() },
        ),
    )
