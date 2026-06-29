package com.thebluealliance.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.config.ThemePreferences
import com.thebluealliance.android.data.local.dao.SubscriptionDao
import com.thebluealliance.android.data.local.entity.SubscriptionEntity
import com.thebluealliance.android.domain.model.ModelType
import com.thebluealliance.android.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val themePreferences: ThemePreferences,
        private val subscriptionDao: SubscriptionDao,
    ) : ViewModel() {
        val themeMode: StateFlow<ThemeMode> =
            themePreferences.themeModeFlow
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.AUTO)

        fun setThemeMode(mode: ThemeMode) {
            viewModelScope.launch {
                themePreferences.setThemeMode(mode)
            }
        }

        // Debug-only: seed an orphaned event_team (model_type 3) subscription — the kind the new
        // app can't create — to verify #1460 renders + removes it. Called only from the DEBUG
        // section of SettingsScreen.
        fun seedDebugEventTeamSubscription() {
            viewModelScope.launch {
                subscriptionDao.insertAll(
                    listOf(
                        SubscriptionEntity(
                            modelKey = "2024micmp4_frc2471",
                            modelType = ModelType.EVENT_TEAM,
                            notifications = "upcoming_match,match_score",
                        ),
                    ),
                )
            }
        }
    }
