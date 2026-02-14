package com.thebluealliance.android.ui.districts

import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.domain.model.Event
import com.thebluealliance.android.domain.model.RegionalRanking

data class DistrictDetailUiState(
    val district: District? = null,
    val events: List<Event>? = null,
    val rankings: List<DistrictRanking>? = null,
    val regionalRankings: List<RegionalRanking>? = null,
)
