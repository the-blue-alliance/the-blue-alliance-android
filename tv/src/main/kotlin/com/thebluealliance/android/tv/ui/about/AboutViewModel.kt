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
) : ViewModel() {
    private val _uiState = MutableStateFlow<ThanksUiState>(ThanksUiState.Loading)
    val uiState: StateFlow<ThanksUiState> = _uiState.asStateFlow()

    init {
        load()
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
                    AboutViewModel(repository = app.container.contributorRepository)
                }
            }
    }
}
