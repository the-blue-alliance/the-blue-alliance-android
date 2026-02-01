package com.thebluealliance.android.ui.districts

import com.thebluealliance.android.domain.model.District

sealed interface DistrictsUiState {
    data object Loading : DistrictsUiState
    data class Success(val districts: List<District>) : DistrictsUiState
    data class Error(val message: String) : DistrictsUiState
}
