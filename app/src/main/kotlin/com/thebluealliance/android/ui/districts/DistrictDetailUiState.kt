package com.thebluealliance.android.ui.districts

import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.events.EventSection

data class DistrictDetailUiState(
    val district: District? = null,
    /** Null until there are events to show or a fetch has said there are none. */
    val eventSections: List<EventSection>? = null,
    /** Null until there are rankings to show or a fetch has said there are none. */
    val rankings: List<DistrictRanking>? = null,
    val loadOutcomes: Map<DistrictDetailTab, RefreshOutcome> = emptyMap(),
) {
    /** Whether [tab] has anything to show, cached or fetched. */
    fun hasData(tab: DistrictDetailTab): Boolean =
        when (tab) {
            DistrictDetailTab.EVENTS -> !eventSections.isNullOrEmpty()
            DistrictDetailTab.RANKINGS -> !rankings.isNullOrEmpty()
        }

    /** Whether [tab]'s fetch failed with nothing cached to show instead. */
    fun loadFailed(tab: DistrictDetailTab): Boolean =
        loadOutcomes[tab] == RefreshOutcome.FAILED && !hasData(tab)
}
