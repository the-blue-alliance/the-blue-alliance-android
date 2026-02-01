package com.thebluealliance.android.ui.matches

import com.thebluealliance.android.domain.model.Match

data class MatchDetailUiState(
    val match: Match? = null,
    val scoreBreakdown: Map<String, Map<String, String>>? = null,
)
