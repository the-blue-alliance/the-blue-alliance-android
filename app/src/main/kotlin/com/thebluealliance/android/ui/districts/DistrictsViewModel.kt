package com.thebluealliance.android.ui.districts

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.data.repository.SeasonRepository
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.SeasonSelection
import com.thebluealliance.android.ui.common.UiState
import com.thebluealliance.android.ui.common.UserMessageBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class DistrictsViewModel
    @Inject
    constructor(
        private val districtRepository: DistrictRepository,
        private val seasons: SeasonRepository,
        private val userMessages: UserMessageBus,
    ) : RefreshableViewModel() {
        private val _maxYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
        val maxYear: StateFlow<Int> = _maxYear.asStateFlow()

        private val selection = SeasonSelection(Calendar.getInstance().get(Calendar.YEAR))
        val selectedYear: StateFlow<Int> = selection.year

        private val refreshOutcome = MutableStateFlow(RefreshOutcome.PENDING)

        val uiState: StateFlow<UiState<List<District>>> =
            selection.year
                .flatMapLatest { year ->
                    combine(
                        districtRepository.observeDistrictsForYear(year),
                        refreshOutcome,
                    ) { districts, outcome ->
                        val state: UiState<List<District>> =
                            when {
                                districts.isNotEmpty() -> UiState.Success(districts)
                                outcome == RefreshOutcome.PENDING -> UiState.Loading
                                outcome == RefreshOutcome.FAILED ->
                                    UiState.Error("Couldn't load districts")
                                else -> UiState.Empty
                            }
                        state
                    }
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading,
                )

        init {
            fetchMaxYear()
            refreshDistricts()
        }

        private fun fetchMaxYear() {
            viewModelScope.launch {
                seasons.maxSeason()?.let { _maxYear.value = maxOf(it, selection.year.value) }
                // The picker reaches maxSeason, but the list opens on the season in play.
                val year = seasons.defaultSeason()
                if (selection.wouldTakeDefault(year)) {
                    refreshOutcome.value = RefreshOutcome.PENDING
                    selection.offerDefault(year)
                    refreshDistricts()
                }
            }
        }

        fun selectYear(year: Int) {
            refreshOutcome.value = RefreshOutcome.PENDING
            selection.pick(year)
            refreshDistricts()
        }

        fun refreshDistricts() {
            val year = selection.year.value
            refreshingTracked(
                refreshOutcome,
                { reportFailedRefresh(year) },
                { districtRepository.refreshDistrictsForYear(year) },
            )
        }

        // With nothing cached, the error screen says it instead.
        private suspend fun reportFailedRefresh(year: Int) {
            if (year != selection.year.value) return
            if (districtRepository.observeDistrictsForYear(year).first().isNotEmpty()) {
                userMessages.showRetry("Couldn't refresh districts", ::refreshDistricts)
            }
        }
    }
