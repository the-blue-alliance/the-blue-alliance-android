package com.thebluealliance.android.ui.teamevent

import com.thebluealliance.android.domain.model.Alliance
import com.thebluealliance.android.domain.model.Award
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.EventOPRs
import com.thebluealliance.android.domain.model.Match
import com.thebluealliance.android.domain.model.Ranking
import com.thebluealliance.android.domain.model.Team

data class TeamEventDetailUiState(
    val team: Team? = null,
    val event: Event? = null,
    val ranking: Ranking? = null,
    val matches: List<Match>? = null,
    val awards: List<Award>? = null,
    val oprs: EventOPRs? = null,
    val alliances: List<Alliance>? = null,
)
