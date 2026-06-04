package com.thebluealliance.android.tv.ui.events

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thebluealliance.android.tv.R
import com.thebluealliance.android.tv.TbaTvApplication
import com.thebluealliance.android.tv.data.model.EventFeed
import com.thebluealliance.android.tv.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDate

class EventsViewModel(
    private val repository: EventRepository,
    private val usingMockData: Boolean,
    private val today: LocalDate = LocalDate.now(),
) : ViewModel() {
    private val _uiState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = EventsUiState.Loading
        viewModelScope.launch {
            _uiState.value =
                try {
                    val events = repository.getEvents(today.year)
                    EventsUiState.Success(EventFeed.from(events, today), usingMockData)
                } catch (e: Exception) {
                    EventsUiState.Error(friendlyError(e))
                }
        }
    }

    /** Turns network/parse exceptions into a calm, 10-foot-readable line instead of a raw stack message. */
    @StringRes
    private fun friendlyError(e: Throwable): Int =
        when (e) {
            is UnknownHostException -> R.string.error_events_offline
            is SocketTimeoutException -> R.string.error_events_timeout
            is IOException -> R.string.error_events_network
            else -> R.string.error_events_generic
        }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as TbaTvApplication
                    EventsViewModel(
                        repository = app.container.eventRepository,
                        usingMockData = app.container.usingMockData,
                    )
                }
            }
    }
}
