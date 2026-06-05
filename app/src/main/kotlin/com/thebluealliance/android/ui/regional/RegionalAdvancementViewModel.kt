package com.thebluealliance.android.ui.regional

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RegionalAdvancementViewModel
    @Inject
    constructor(
        private val repository: RegionalAdvancementRepository,
        private val tbaApi: TbaApi,
    ) : RefreshableViewModel() {
        private val fallbackYear = Calendar.getInstance().get(Calendar.YEAR)

        private val _maxYear = MutableStateFlow(fallbackYear)
        val maxYear: StateFlow<Int> = _maxYear.asStateFlow()

        private val _selectedYear = MutableStateFlow(fallbackYear)
        val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

        private val _uiState = MutableStateFlow<UiState<List<RegionalRanking>>>(UiState.Loading)
        val uiState: StateFlow<UiState<List<RegionalRanking>>> = _uiState.asStateFlow()

        init {
            fetchMaxYear()
            refreshRankings()
        }

        private fun fetchMaxYear() {
            viewModelScope.launch {
                try {
                    val status = tbaApi.getStatus()
                    _maxYear.value = status.maxSeason
                    if (_selectedYear.value < status.maxSeason) {
                        _selectedYear.value = status.maxSeason
                    }
                } catch (_: Exception) {
                    // Keep calendar year fallback if status request fails.
                }
            }
        }

        fun selectYear(year: Int) {
            if (_selectedYear.value == year) return
            _selectedYear.value = year
            refreshRankings()
        }

        fun refreshRankings() {
            refreshing({
                _uiState.value =
                    try {
                        val rankings = repository.getRegionalRankings(_selectedYear.value)
                        if (rankings.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(rankings)
                        }
                    } catch (e: Exception) {
                        UiState.Error(
                            e.message ?: "Unable to load regional advancement rankings",
                        )
                    }
            })
        }
    }
