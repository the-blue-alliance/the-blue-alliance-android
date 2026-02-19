package com.thebluealliance.android.ui.regionaladvancement

import com.thebluealliance.android.domain.model.RegionalRanking

sealed interface RegionalAdvancementUiState {
    data object Loading : RegionalAdvancementUiState
    data class Success(val rankings: List<RegionalRanking>) : RegionalAdvancementUiState
    data class Error(val message: String) : RegionalAdvancementUiState
}
