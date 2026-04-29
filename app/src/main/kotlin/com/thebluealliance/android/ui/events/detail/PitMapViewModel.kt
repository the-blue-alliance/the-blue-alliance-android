package com.thebluealliance.android.ui.events.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebluealliance.android.data.repository.EventRepository
import com.thebluealliance.android.navigation.Screen
import com.thebluealliance.android.util.buildNexusEventCode
import com.thebluealliance.android.util.buildTbaPitMapUrl
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

data class PitMapUiState(
    /** Screen title, formatted as "2026 EventShort" once the event loads. Empty until then. */
    val eventTitle: String = "",
    /** Pre-formatted Nexus event code, e.g. "2026MIKET". Empty until event loads. */
    val nexusEventCode: String = "",
    val isLoaded: Boolean = false,
)

@HiltViewModel(assistedFactory = PitMapViewModel.Factory::class)
class PitMapViewModel
    @AssistedInject
    constructor(
        @Assisted private val navKey: Screen.PitMap,
        private val eventRepository: EventRepository,
    ) : ViewModel() {
        val eventKey: String = navKey.eventKey
        val highlightedTeamKeys: List<String> = navKey.highlightedTeamKeys

        private val _tbaMapAvailable = MutableStateFlow<Boolean?>(null)

        /**
         * Whether the TBA pit map exists for this event.
         * - `null`  = HEAD check in progress
         * - `true`  = TBA map available; load it in the WebView
         * - `false` = Not found; fall back to Nexus
         */
        val tbaMapAvailable: StateFlow<Boolean?> = _tbaMapAvailable.asStateFlow()

        val uiState: StateFlow<PitMapUiState> =
            eventRepository
                .observeEvent(eventKey)
                .map { event ->
                    if (event == null) {
                        PitMapUiState()
                    } else {
                        PitMapUiState(
                            eventTitle = "${event.year} ${event.shortName ?: event.name}",
                            nexusEventCode =
                                buildNexusEventCode(
                                    eventKey,
                                    event.year,
                                    event.firstEventCode,
                                ),
                            isLoaded = true,
                        )
                    }
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = PitMapUiState(),
                )

        init {
            // Run the availability HEAD check on IO so the UI layer stays free of network calls.
            viewModelScope.launch(Dispatchers.IO) {
                _tbaMapAvailable.value =
                    checkTbaPitMapAvailable(
                        buildTbaPitMapUrl(eventKey, highlightedTeamKeys),
                    )
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(navKey: Screen.PitMap): PitMapViewModel
        }
    }

/**
 * Returns `true` if the TBA pit map endpoint responds with anything other than HTTP 404.
 * On any network failure we return `true` optimistically and let the WebView handle errors
 * rather than leaving the UI stuck on a spinner indefinitely.
 */
private fun checkTbaPitMapAvailable(url: String): Boolean =
    try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        connection.instanceFollowRedirects = true
        connection.connectTimeout = 8_000
        connection.readTimeout = 8_000
        connection.connect()
        val responseCode = connection.responseCode
        connection.disconnect()
        responseCode != HttpURLConnection.HTTP_NOT_FOUND
    } catch (_: Exception) {
        // No connectivity or unexpected error — optimistically try TBA; WebView will surface any
        // further errors natively.
        true
    }
