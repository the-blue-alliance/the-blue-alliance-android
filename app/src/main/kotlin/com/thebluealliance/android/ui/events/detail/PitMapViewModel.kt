package com.thebluealliance.android.ui.events.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.navigation.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class PitMapUiState(
    /** Screen title, formatted as "2026 EventShort" once loaded. Empty until event loads. */
    val eventTitle: String = "",
    /** Pre-formatted Nexus event code, e.g. "2026MIKET". Empty string until event loads. */
    val nexusEventCode: String = "",
    val isLoaded: Boolean = false,
)

@HiltViewModel(assistedFactory = PitMapViewModel.Factory::class)
class PitMapViewModel
    @AssistedInject
    constructor(
        @Assisted val navKey: Screen.PitMap,
        private val eventRepository: EventRepository,
    ) : ViewModel() {
        val eventKey: String = navKey.eventKey
        val highlightedTeamKeys: List<String> = navKey.highlightedTeamKeys

        val uiState: StateFlow<PitMapUiState> =
            eventRepository
                .observeEvent(eventKey)
                .map { event ->
                    if (event == null) {
                        PitMapUiState()
                    } else {
                        val nexusCode =
                            if (!event.firstEventCode.isNullOrBlank()) {
                                "${event.year}${event.firstEventCode.uppercase()}"
                            } else {
                                "${event.year}${eventKey.drop(4).uppercase()}"
                            }
                        PitMapUiState(
                            eventTitle = "${event.year} ${event.shortName ?: event.name}",
                            nexusEventCode = nexusCode,
                            isLoaded = true,
                        )
                    }
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = PitMapUiState(),
                )

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.PitMap): PitMapViewModel
        }
    }

