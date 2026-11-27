package com.thebluealliance.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.ApiStatusDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TBA's season bounds, fetched at most once per app session and shared by every year picker so
 * they all open on the same season. A failed fetch isn't cached; the next caller retries. Each
 * successful fetch is kept in DataStore, so an offline start opens on the last season TBA reported.
 */
@Singleton
class SeasonRepository
    @Inject
    constructor(
        private val api: TbaApi,
        private val dataStore: DataStore<Preferences>,
    ) {
        private val mutex = Mutex()
        private var status: ApiStatusDto? = null

        private suspend fun status(): ApiStatusDto? =
            mutex.withLock {
                status ?: try {
                    api.getStatus().also {
                        status = it
                        remember(it)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    lastKnown()
                }
            }

        private suspend fun remember(status: ApiStatusDto) {
            try {
                dataStore.edit { prefs ->
                    prefs[CURRENT_SEASON_KEY] = status.currentSeason
                    prefs[MAX_SEASON_KEY] = status.maxSeason
                }
            } catch (_: IOException) {
                // The fetched status still serves this session.
            }
        }

        private suspend fun lastKnown(): ApiStatusDto? {
            val prefs =
                try {
                    dataStore.data.first()
                } catch (_: IOException) {
                    return null
                }
            val current = prefs[CURRENT_SEASON_KEY] ?: return null
            val max = prefs[MAX_SEASON_KEY] ?: return null
            return ApiStatusDto(currentSeason = current, maxSeason = max)
        }

        /** The newest season a picker offers, or null when TBA's status has never been fetched. */
        suspend fun maxSeason(): Int? = status()?.maxSeason

        /** The season pickers open on: TBA's current season, or the calendar year if never fetched. */
        suspend fun defaultSeason(): Int = status()?.currentSeason ?: Year.now().value

        internal companion object {
            val CURRENT_SEASON_KEY = intPreferencesKey("tba_status_current_season")
            val MAX_SEASON_KEY = intPreferencesKey("tba_status_max_season")
        }
    }
