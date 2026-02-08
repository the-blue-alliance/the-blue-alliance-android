package com.thebluealliance.android.ui.matches

import com.thebluealliance.android.domain.model.Match

data class MatchVideo(
    val type: String,
    val key: String,
)

data class MatchDetailUiState(
    val match: Match? = null,
    val scoreBreakdown: Map<String, Map<String, String>>? = null,
    val eventName: String? = null,
    val eventKey: String? = null,
    val formattedTime: String? = null,
    val videos: List<MatchVideo> = emptyList(),
)
