package com.thebluealliance.android.ui.regional

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.SeasonSelection
import com.thebluealliance.android.ui.common.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

@HiltViewModel(assistedFactory = RegionalAdvancementViewModel.Factory::class)
class RegionalAdvancementViewModel
    @AssistedInject
    constructor(
        @Assisted navKey: Screen.RegionalAdvancement,
        private val repository: RegionalAdvancementRepository,
        private val seasons: SeasonRepository,
    ) : RefreshableViewModel() {
        private val fallbackYear = Calendar.getInstance().get(Calendar.YEAR)

        private val _maxYear = MutableStateFlow(fallbackYear)
        val maxYear: StateFlow<Int> = _maxYear.asStateFlow()

        private val selection =
            SeasonSelection(fallbackYear).apply {
                // A year in the link counts as the user's pick, so later defaults leave it alone.
                navKey.year?.let(::pick)
            }
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

        private var rankingsJob: Job? = null

        fun refreshRankings() {
            val year = selection.year.value
            // A load for a year no longer selected is abandoned, so it can't hold the spinner.
            rankingsJob?.cancel()
            rankingsJob =
                refreshing({
                    val state =
                        try {
                            val rankings = repository.getRegionalRankings(year)
                            if (rankings.isEmpty()) {
                                UiState.Empty
                            } else {
                                UiState.Success(rankings)
                            }
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            UiState.Error(
                                e.message ?: "Unable to load regional advancement rankings",
                            )
                        }
                    // A slower load for a year no longer selected must not replace the current one.
                    if (selection.year.value == year) _uiState.value = state
                })
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.RegionalAdvancement): RegionalAdvancementViewModel
        }
    }
