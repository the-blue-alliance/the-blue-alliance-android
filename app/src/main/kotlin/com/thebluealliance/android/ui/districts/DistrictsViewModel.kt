package com.thebluealliance.android.ui.districts

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.DistrictRepository
import com.thebluealliance.android.domain.model.District
import com.thebluealliance.android.ui.common.RefreshOutcome
import com.thebluealliance.android.ui.common.RefreshableViewModel
import com.thebluealliance.android.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
        private val tbaApi: TbaApi,
    ) : RefreshableViewModel() {
        private val _maxYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
        val maxYear: StateFlow<Int> = _maxYear.asStateFlow()

        private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
        val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

        private val refreshOutcome = MutableStateFlow(RefreshOutcome.PENDING)

        private var hasUserSelectedYear = false

        val uiState: StateFlow<UiState<List<District>>> =
            _selectedYear
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
                try {
                    val status = tbaApi.getStatus()
                    _maxYear.value = maxOf(status.maxSeason, _selectedYear.value)
                    // The picker reaches maxSeason, but the list opens on the season in play.
                    if (!hasUserSelectedYear && _selectedYear.value != status.currentSeason) {
                        showYear(status.currentSeason)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // Fall back to calendar year (already set)
                }
            }
        }

        fun selectYear(year: Int) {
            hasUserSelectedYear = true
            showYear(year)
        }

        private fun showYear(year: Int) {
            refreshOutcome.value = RefreshOutcome.PENDING
            _selectedYear.value = year
            refreshDistricts()
        }

        fun refreshDistricts() {
            refreshingTracked(
                refreshOutcome,
                { districtRepository.refreshDistrictsForYear(_selectedYear.value) },
            )
        }
    }
