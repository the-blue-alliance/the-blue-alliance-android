package com.thebluealliance.android.ui.mytba

import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.AuthRepository
import com.thebluealliance.android.data.repository.MyTBARepository
import com.thebluealliance.android.domain.model.Favorite
import com.thebluealliance.android.messaging.DeviceRegistrationManager
import com.thebluealliance.android.shortcuts.TBAShortcutManager
import com.thebluealliance.android.ui.common.RefreshableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyTBAViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val myTBARepository: MyTBARepository,
        private val deviceRegistrationManager: DeviceRegistrationManager,
        private val shortcutManager: TBAShortcutManager,
    ) : RefreshableViewModel() {
        val uiState: StateFlow<MyTBAUiState> =
            combine(
                authRepository.currentUser,
                myTBARepository.observeFavorites(),
                myTBARepository.observeSubscriptions(),
            ) { user, favorites, subscriptions ->
                MyTBAUiState(
                    isSignedIn = user != null,
                    userName = user?.displayName,
                    userEmail = user?.email,
                    userPhotoUrl = user?.photoUrl?.toString(),
                    favorites = favorites,
                    subscriptions = subscriptions,
                    canPinShortcuts = shortcutManager.canPinShortcuts(),
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyTBAUiState())

        init {
            // Auto-refresh when user signs in
            viewModelScope.launch {
                authRepository.currentUser
                    .map { it != null }
                    .distinctUntilChanged()
                    .collect { signedIn ->
                        if (signedIn) {
                            launch {
                                try {
                                    deviceRegistrationManager.onSignIn()
                                } catch (
                                    _: Exception,
                                ) {
                                }
                            }
                            refresh()
                        }
                    }
            }
        }

        fun requestPinShortcut(favorite: Favorite) {
            viewModelScope.launch {
                shortcutManager.requestPinShortcut(favorite)
            }
        }

        fun removeFavorite(favorite: Favorite) {
            viewModelScope.launch {
                try {
                    myTBARepository.removeFavorite(favorite.modelKey, favorite.modelType)
                } catch (e: Exception) {
                    android.util.Log.w("MyTBAViewModel", "Failed to remove favorite", e)
                }
            }
        }

        fun removeSubscription(
            modelKey: String,
            modelType: Int,
        ) {
            viewModelScope.launch {
                try {
                    val isFavorited =
                        uiState.value.favorites.any {
                            it.modelKey == modelKey && it.modelType == modelType
                        }
                    myTBARepository.updatePreferences(
                        modelKey = modelKey,
                        modelType = modelType,
                        favorite = isFavorited,
                        notifications = emptyList(),
                    )
                } catch (e: Exception) {
                    android.util.Log.w("MyTBAViewModel", "Failed to remove subscription", e)
                }
            }
        }

        fun refresh() {
            viewModelScope.launch {
                if (!authRepository.isSignedIn()) return@launch
                refreshing(
                    { myTBARepository.refreshFavorites() },
                    { myTBARepository.refreshSubscriptions() },
                )
            }
        }

        fun refreshTab(tab: Int) {
            viewModelScope.launch {
                if (!authRepository.isSignedIn()) return@launch
                when (tab) {
                    0 -> refreshing({ myTBARepository.refreshFavorites() })
                    1 -> refreshing({ myTBARepository.refreshSubscriptions() })
                }
            }
        }

        fun signOut() {
            viewModelScope.launch {
                try {
                    deviceRegistrationManager.onSignOut()
                } catch (_: Exception) {
                }
                authRepository.signOut()
                myTBARepository.clearLocal()
            }
        }
    }
