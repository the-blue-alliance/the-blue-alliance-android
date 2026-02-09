package com.thebluealliance.android.ui.events

import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventDistrictPoints
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.Team

data class EventDetailUiState(
    val event: Event? = null,
    val teams: List<Team>? = null,
    val matches: List<Match>? = null,
    val rankings: List<Ranking>? = null,
    val alliances: List<Alliance>? = null,
    val awards: List<Award>? = null,
    val districtPoints: List<EventDistrictPoints>? = null,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)
