package com.thebluealliance.android.domain.model

/** Describes how (or whether) a team has qualified for the Championship. */
sealed class CmpAdvancement {
    /** Team qualified by winning / placing highly at a specific event. */
    data class EventQualified(
        val eventKey: String,
        /** Resolved short name of the qualifying event (may fall back to the event key). */
        val eventShortName: String?,
    ) : CmpAdvancement()

    /** Team qualified via the regional advancement pool for a given week. */
    data class PoolQualified(
        val week: Int,
    ) : CmpAdvancement()

    /** Team has qualified but no further detail is available. */
    object Qualified : CmpAdvancement()
}
