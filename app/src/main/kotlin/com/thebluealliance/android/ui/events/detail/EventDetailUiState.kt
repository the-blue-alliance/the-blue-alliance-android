package com.thebluealliance.android.ui.events.detail

import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventCOPRs
import com.thebluealliance.android.domain.model.EventAdvancementPoints
import com.thebluealliance.android.domain.model.EventInsights
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.RankingSortOrder
import com.thebluealliance.android.domain.model.Team

data class EventDetailUiState(
    val event: Event? = null,
    val teams: List<Team>? = null,
    val matches: List<Match>? = null,
    val rankings: List<Ranking>? = null,
    val rankingSortOrders: List<RankingSortOrder>? = null,
    val rankingExtraStatsInfo: List<RankingSortOrder>? = null,
    val alliances: List<Alliance>? = null,
    val awards: List<Award>? = null,
    val advancementPoints: List<EventAdvancementPoints>? = null,
    val oprs: EventOPRs? = null,
    val coprs: EventCOPRs? = null,
    val insights: EventInsights? = null,
    val districtDisplayName: String? = null,
    val pitLocations: Map<String, String> = emptyMap(),
)
