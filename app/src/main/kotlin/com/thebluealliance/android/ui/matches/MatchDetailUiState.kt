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
     * True once a refresh has finished and there's still no match to show — a bad/stale deep
     * link (404) or an uncached match we couldn't fetch (offline). Either way: stop spinning.
     */
    val loadFailed: Boolean = false,
)
