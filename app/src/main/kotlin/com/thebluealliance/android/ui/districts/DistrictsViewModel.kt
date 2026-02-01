package com.thebluealliance.android.ui.districts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

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
        refreshDistricts()
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
