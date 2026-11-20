package com.thebluealliance.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thebluealliance.android.data.mappers.toDomain
import com.thebluealliance.android.data.remote.TbaApi
import com.thebluealliance.android.data.remote.dto.SponsorsResponseDto
import com.thebluealliance.android.domain.model.PlatinumSponsor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

/**
 * Platinum sponsors for the More tab. The last good response is kept in DataStore so the credit
 * survives restarts and offline launches, and is refetched at most once a day.
 */
@Singleton
class SponsorRepository internal constructor(
    private val api: TbaApi,
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
    private val now: () -> Long,
) {
    @Inject
    constructor(
        api: TbaApi,
        dataStore: DataStore<Preferences>,
        json: Json,
    ) : this(api, dataStore, json, System::currentTimeMillis)

    private val refreshMutex = Mutex()

    /** The cached sponsors in display order; empty until a fetch has succeeded. */
    val sponsors: Flow<List<PlatinumSponsor>> =
        dataStore.data
            .map { prefs -> decode(prefs[SPONSORS_JSON_KEY]) }
            .distinctUntilChanged()

    /** Refetch if the cache is over a day old. A failed fetch (e.g. a 404) keeps the cached sponsors. */
    suspend fun refreshIfStale() {
        refreshMutex.withLock {
            val fetchedAt = dataStore.data.first()[FETCHED_AT_KEY]
            if (fetchedAt != null && now() - fetchedAt in 0 until CACHE_TTL_MILLIS) return
            val response =
                try {
                    api.getSponsors()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    return
                }
            dataStore.edit { prefs ->
                prefs[SPONSORS_JSON_KEY] =
                    json.encodeToString(SponsorsResponseDto.serializer(), response)
                prefs[FETCHED_AT_KEY] = now()
            }
        }
    }

    private fun decode(cached: String?): List<PlatinumSponsor> {
        if (cached == null) return emptyList()
        return try {
            json
                .decodeFromString(
                    SponsorsResponseDto.serializer(),
                    cached,
                ).sponsors
                .map { it.toDomain() }
        } catch (e: SerializationException) {
            emptyList()
        }
    }

    internal companion object {
        const val CACHE_TTL_MILLIS = 24L * 60 * 60 * 1000
        val SPONSORS_JSON_KEY = stringPreferencesKey("platinum_sponsors_json")
        val FETCHED_AT_KEY = longPreferencesKey("platinum_sponsors_fetched_at")
    }
}
