package com.thebluealliance.android.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.remote.GitHubApi
import com.thebluealliance.android.data.remote.dto.GitHubContributorDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThanksViewModel @Inject constructor(
    private val gitHubApi: GitHubApi,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ThanksUiState>(ThanksUiState.Loading)
    val uiState: StateFlow<ThanksUiState> = _uiState

    init {
        loadContributors()
    }

    fun retry() {
        loadContributors()
    }

    private fun loadContributors() {
        _uiState.value = ThanksUiState.Loading
        viewModelScope.launch {
            try {
                val contributors = gitHubApi.getContributors(
                    owner = "the-blue-alliance",
                    repo = "the-blue-alliance-android",
                )
                _uiState.value = ThanksUiState.Success(contributors)
            } catch (e: Exception) {
                _uiState.value = ThanksUiState.Error(e.message ?: "Failed to load contributors")
            }
        }
    }
}

sealed interface ThanksUiState {
    data object Loading : ThanksUiState
    data class Success(val contributors: List<GitHubContributorDto>) : ThanksUiState
    data class Error(val message: String) : ThanksUiState
}
