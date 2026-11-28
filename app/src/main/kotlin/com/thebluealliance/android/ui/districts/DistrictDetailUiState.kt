package com.thebluealliance.android.ui.districts

import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.domain.model.DistrictRanking
import com.thebluealliance.android.ui.events.EventSection

data class DistrictDetailUiState(
    val district: District? = null,
    val eventSections: List<EventSection>? = null,
    val rankings: List<DistrictRanking>? = null,
) {
    /** Whether [tab]'s fetch failed with nothing cached to show instead. Not tracked yet. */
    @Suppress("UNUSED_PARAMETER", "FunctionOnlyReturningConstant")
    fun loadFailed(tab: DistrictDetailTab): Boolean = false
}
