package com.thebluealliance.android.ui.districts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.repository.DistrictRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class DistrictsViewModel @Inject constructor(
    private val districtRepository: DistrictRepository,
    private val tbaApi: TbaApi,
) : ViewModel() {

    private val _maxYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val maxYear: StateFlow<Int> = _maxYear.asStateFlow()

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<DistrictsUiState> = _selectedYear
        .flatMapLatest { year ->
            districtRepository.observeDistrictsForYear(year).map { districts ->
                if (districts.isEmpty()) DistrictsUiState.Loading
                else DistrictsUiState.Success(districts)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DistrictsUiState.Loading)

    init {
        fetchMaxYear()
        refreshDistricts()
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
                // Fall back to calendar year (already set)
            }
        }
    }

    fun selectYear(year: Int) {
        _selectedYear.value = year
        refreshDistricts()
    }

    fun refreshDistricts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                districtRepository.refreshDistrictsForYear(_selectedYear.value)
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
