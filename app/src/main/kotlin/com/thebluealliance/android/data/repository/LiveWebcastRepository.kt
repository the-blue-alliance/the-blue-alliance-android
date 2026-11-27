package com.thebluealliance.android.data.repository

import com.thebluealliance.android.domain.model.Webcast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Which events' webcasts are online right now, from the `live_events` node of TBA's Firebase
 * Realtime Database: the feed GameDay subscribes to. The server's live-events cron writes it every
 * minute with each live event's current webcasts and their provider status.
 *
 * It's read once over the database's public REST endpoint and cached for [ttlMillis], instead of
 * holding a realtime listener open, so a screen that asks again within the window gets the cached
 * answer and the app makes at most one request per window.
 */
class LiveWebcastRepository(
    private val fetchBody: suspend () -> String,
    private val json: Json,
    private val now: () -> Long = System::currentTimeMillis,
    val ttlMillis: Long = DEFAULT_TTL_MILLIS,
) {
    private val mutex = Mutex()
    private var cached: Map<String, List<Webcast>> = emptyMap()
    private var fetchedAt: Long? = null

    /** Online webcasts by event key; empty while nothing is live or the fetch fails. */
    suspend fun onlineWebcasts(): Map<String, List<Webcast>> =
        mutex.withLock {
            val last = fetchedAt
            if (last != null && now() - last < ttlMillis) return@withLock cached
            cached =
                try {
                    parseOnlineWebcasts(json, fetchBody())
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    emptyMap()
                }
            // A failure also waits out the window, so a dead network isn't retried every recomposition.
            fetchedAt = now()
            cached
        }

    companion object {
        const val DEFAULT_TTL_MILLIS = 3 * 60 * 1000L
    }
}

@Serializable
internal data class LiveEventDto(
    val webcasts: List<LiveWebcastDto?>? = null,
)

@Serializable
internal data class LiveWebcastDto(
    val type: String? = null,
    val channel: String? = null,
    val file: String? = null,
    val date: String? = null,
    val status: String? = null,
)

/**
 * Parses a `live_events.json` body into each event's online webcasts. The node is `null` when
 * nothing is live. Status is `online`, `offline` or `unknown`; only `online` counts.
 */
internal fun parseOnlineWebcasts(
    json: Json,
    body: String,
): Map<String, List<Webcast>> {
    val element = json.parseToJsonElement(body)
    if (element is JsonNull) return emptyMap()
    val events = json.decodeFromJsonElement<Map<String, LiveEventDto?>>(element)
    return events
        .mapValues { (_, event) ->
            event
                ?.webcasts
                .orEmpty()
                .filterNotNull()
                .filter {
                    it.status == "online" &&
                        !it.type.isNullOrBlank() &&
                        !it.channel.isNullOrBlank()
                }.map {
                    Webcast(
                        type = it.type!!,
                        channel = it.channel!!,
                        file = it.file,
                        date = it.date,
                    )
                }
        }.filterValues { it.isNotEmpty() }
}
