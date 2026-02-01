package com.thebluealliance.android.ui.mytba

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyTBAViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val myTBARepository: MyTBARepository,
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val uiState: StateFlow<MyTBAUiState> = combine(
        authRepository.currentUser,
        myTBARepository.observeFavorites(),
        myTBARepository.observeSubscriptions(),
    ) { user, favorites, subscriptions ->
        MyTBAUiState(
            isSignedIn = user != null,
            userName = user?.displayName,
            userEmail = user?.email,
            favorites = favorites,
            subscriptions = subscriptions,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyTBAUiState())

    init {
        // Auto-refresh when user signs in
        viewModelScope.launch {
            authRepository.currentUser
                .map { it != null }
                .distinctUntilChanged()
                .collect { signedIn ->
                    if (signedIn) refresh()
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            if (!authRepository.isSignedIn()) return@launch
            _isRefreshing.value = true
            try {
                launch { try { myTBARepository.refreshFavorites() } catch (_: Exception) {} }
                launch { try { myTBARepository.refreshSubscriptions() } catch (_: Exception) {} }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            myTBARepository.clearLocal()
        }
    }
}
