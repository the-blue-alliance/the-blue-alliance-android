package com.thebluealliance.android.ui.matches

import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.PlayoffType

data class MatchVideo(
    val type: String,
    val key: String,
)

data class MatchDetailUiState(
    val match: Match? = null,
    val scoreBreakdown: Map<String, Map<String, String>>? = null,
    val eventName: String? = null,
    val eventKey: String? = null,
    val playoffType: PlayoffType = PlayoffType.OTHER,
    val formattedTime: String? = null,
    val videos: List<MatchVideo> = emptyList(),
    val year: Int = 0,
    /**
     * True once a refresh has finished and there's still no match to show, for any reason but a
     * 404 (offline, a server error). Retry may get it next time.
     */
    val loadFailed: Boolean = false,
    /** TBA's last answer for the match was a 404. */
    val matchMissing: Boolean = false,
) {
    /** TBA has no match with this key, and none is cached. */
    val notFound: Boolean get() = matchMissing && match == null
}
