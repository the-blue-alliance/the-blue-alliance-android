package com.thebluealliance.android.ui.regional

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.SeasonSelection
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
        private val seasons: SeasonRepository,
    ) : RefreshableViewModel() {
        private val fallbackYear = Calendar.getInstance().get(Calendar.YEAR)

        private val _maxYear = MutableStateFlow(fallbackYear)
        val maxYear: StateFlow<Int> = _maxYear.asStateFlow()

        private val selection = SeasonSelection(fallbackYear)
        val selectedYear: StateFlow<Int> = selection.year

        private val _uiState = MutableStateFlow<UiState<List<RegionalRanking>>>(UiState.Loading)
        val uiState: StateFlow<UiState<List<RegionalRanking>>> = _uiState.asStateFlow()

        init {
            fetchMaxYear()
            refreshRankings()
        }

        private fun fetchMaxYear() {
            viewModelScope.launch {
                seasons.maxSeason()?.let { _maxYear.value = maxOf(it, selection.year.value) }
                if (selection.offerDefault(seasons.defaultSeason())) refreshRankings()
            }
        }

        fun selectYear(year: Int) {
            if (selection.year.value == year) return
            selection.pick(year)
            refreshRankings()
        }

        fun refreshRankings() {
            val year = selection.year.value
            refreshing({
                val state =
                    try {
                        val rankings = repository.getRegionalRankings(year)
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
                // A slower load for a year no longer selected must not replace the current one.
                if (selection.year.value == year) _uiState.value = state
            })
        }
    }
