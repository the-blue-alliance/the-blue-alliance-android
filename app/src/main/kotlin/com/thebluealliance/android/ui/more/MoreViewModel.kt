package com.thebluealliance.android.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.SponsorRepository
import com.thebluealliance.android.domain.model.PlatinumSponsor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel
    @Inject
    constructor(
        private val sponsorRepository: SponsorRepository,
    ) : ViewModel() {
        val sponsors: StateFlow<List<PlatinumSponsor>> =
            sponsorRepository.sponsors.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

        init {
            viewModelScope.launch { sponsorRepository.refreshIfStale() }
        }
    }
