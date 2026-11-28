package com.thebluealliance.android.ui.regional

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.RegionalAdvancementRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.domain.model.RegionalRanking
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.SeasonSelection
import com.thebluealliance.android.ui.common.UiState
import com.thebluealliance.android.ui.common.UserMessageBus
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
        private val userMessages: UserMessageBus,
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

        // The year the shown rankings belong to, so a failed refresh can tell them apart.
        private var loadedYear: Int? = null

        fun refreshRankings() {
            val year = selection.year.value
            // A load for a year no longer selected is abandoned, so it can't hold the spinner.
            rankingsJob?.cancel()
            // Another year's rankings never stand in for this one's.
            if (loadedYear != year) _uiState.value = UiState.Loading
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
                            Log.w(TAG, "regional advancement load failed", e)
                            null
                        }
                    // A slower load for a year no longer selected must not replace the current one.
                    if (selection.year.value != year) return@refreshing
                    when {
                        state != null -> {
                            _uiState.value = state
                            loadedYear = year
                        }
                        // A failed refresh keeps the rankings on screen and says so.
                        _uiState.value is UiState.Success && loadedYear == year ->
                            userMessages.showRetry(
                                "Couldn't refresh regional advancement",
                                ::refreshRankings,
                            )
                        else -> _uiState.value = UiState.Error("Couldn't load regional advancement")
                    }
                })
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.RegionalAdvancement): RegionalAdvancementViewModel
        }

        private companion object {
            const val TAG = "RegionalAdvancementVM"
        }
    }
