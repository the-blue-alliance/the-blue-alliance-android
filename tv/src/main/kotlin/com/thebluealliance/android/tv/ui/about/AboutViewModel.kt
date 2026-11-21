package com.thebluealliance.android.tv.ui.about

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thebluealliance.android.tv.R
import com.thebluealliance.android.tv.TbaTvApplication
import com.thebluealliance.android.tv.data.repository.Contributor
import com.thebluealliance.android.tv.data.repository.ContributorRepository
import com.thebluealliance.android.tv.data.repository.Sponsor
import com.thebluealliance.android.tv.data.repository.SponsorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed interface ThanksUiState {
    data object Loading : ThanksUiState

    data class Success(
        val contributors: List<Contributor>,
    ) : ThanksUiState

    data class Error(
        @param:StringRes val messageRes: Int,
    ) : ThanksUiState
}

class AboutViewModel(
    private val repository: ContributorRepository,
    private val sponsorRepository: SponsorRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ThanksUiState>(ThanksUiState.Loading)
    val uiState: StateFlow<ThanksUiState> = _uiState.asStateFlow()

    private val _sponsors = MutableStateFlow<List<Sponsor>>(emptyList())

    /** Platinum sponsors to credit; stays empty while loading or if the fetch fails. */
    val sponsors: StateFlow<List<Sponsor>> = _sponsors.asStateFlow()

    init {
        load()
        loadSponsors()
    }

    fun retry() = load()

    private fun load() {
        _uiState.value = ThanksUiState.Loading
        viewModelScope.launch {
            _uiState.value =
                try {
                    ThanksUiState.Success(repository.getContributors())
                } catch (e: Exception) {
                    ThanksUiState.Error(friendlyError(e))
                }
        }
    }

    // The credit is a nicety, not the screen's job: the repository serves its cache and swallows a
    // failed refresh, so there's no error row here — no sponsors just means no section.
    private fun loadSponsors() {
        viewModelScope.launch { sponsorRepository.sponsors().collect { _sponsors.value = it } }
    }

    /** Turn network/parse exceptions into a calm, 10-foot-readable line. */
    @StringRes
    private fun friendlyError(e: Throwable): Int =
        when (e) {
            is UnknownHostException -> R.string.error_contributors_offline
            is SocketTimeoutException -> R.string.error_contributors_timeout
            is IOException -> R.string.error_contributors_network
            else -> R.string.error_contributors_generic
        }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as TbaTvApplication
                    AboutViewModel(
                        repository = app.container.contributorRepository,
                        sponsorRepository = app.container.sponsorRepository,
                    )
                }
            }
    }
}
