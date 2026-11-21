package com.thebluealliance.android.tv.data.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.thebluealliance.android.data.remote.dto.SponsorDto
import com.thebluealliance.android.data.remote.dto.SponsorsResponseDto
import com.thebluealliance.android.tv.data.api.TbaApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

/**
 * A platinum sponsor credited on the About screen. The TV theme is always dark, so [logoUrl] is the
 * on-dark logo when the API has one, else the full-color logo, else null (draw the name instead).
 */
data class Sponsor(
    val name: String,
    val logoUrl: String?,
    val logoStyle: SponsorLogoStyle,
)

/** A wordmark already spells the name; a square mark is drawn with the name beside it. */
enum class SponsorLogoStyle { Wordmark, Mark }

fun SponsorDto.toDomain() =
    Sponsor(
        name = name,
        logoUrl = httpsOrNull(logoUrlDark) ?: httpsOrNull(logoUrl),
        logoStyle = if (logoStyle == "mark") SponsorLogoStyle.Mark else SponsorLogoStyle.Wordmark,
    )

/**
 * Logo URLs come from the server (and a cache that could be tampered with). Coil's default
 * fetchers also read file://, content://, android.resource:// and data: URIs and bare paths, so
 * anything that isn't an https URL is dropped and the sponsor's name is drawn instead.
 */
private fun httpsOrNull(url: String?): String? =
    url?.takeIf { it.startsWith("https://", ignoreCase = true) }

/** A row of logos, not a list: more than a handful wouldn't fit, and a huge response shouldn't
 * start a load per entry. */
const val MAX_SPONSORS = 6

interface SponsorRepository {
    /** Sponsors in display order: the cached list first (if any), then a fresher one if fetched. */
    fun sponsors(): Flow<List<Sponsor>>
}

/** The last good `/sponsors` response and when it was fetched. */
interface SponsorCache {
    fun read(): Pair<SponsorsResponseDto, Long>?

    fun write(
        response: SponsorsResponseDto,
        fetchedAtMillis: Long,
    )
}

/**
 * An optimistic read-through cache. The cached sponsors show at once, even when stale; a cache older
 * than [CACHE_TTL_MILLIS] (or none) triggers one fetch, whose result replaces it. A failed fetch
 * (offline, or a server without the endpoint) keeps whatever was cached. Sponsors change a few
 * times a year, so showing a stale list once is fine.
 */
class NetworkSponsorRepository(
    private val api: TbaApi,
    private val cache: SponsorCache,
    private val now: () -> Long = System::currentTimeMillis,
    private val onRefreshFailed: (Exception) -> Unit = {
        Log.w(TAG, "Couldn't refresh sponsors; keeping the cached list", it)
    },
) : SponsorRepository {
    override fun sponsors(): Flow<List<Sponsor>> =
        flow {
            val cached = cache.read()
            cached?.let { (response, _) -> emit(response.toDomain()) }
            val age = cached?.let { (_, fetchedAt) -> now() - fetchedAt }
            if (age != null && age in 0 until CACHE_TTL_MILLIS) return@flow
            val fresh =
                try {
                    api.getSponsors()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    onRefreshFailed(e)
                    return@flow
                }
            cache.write(fresh, now())
            if (fresh != cached?.first) emit(fresh.toDomain())
        }

    private fun SponsorsResponseDto.toDomain() = sponsors.take(MAX_SPONSORS).map { it.toDomain() }

    companion object {
        const val CACHE_TTL_MILLIS = 24L * 60 * 60 * 1000
        private const val TAG = "SponsorRepository"
    }
}

/** Persists the response as JSON so the credit survives restarts and offline launches. */
class PrefsSponsorCache(
    private val prefs: SharedPreferences,
    private val json: Json = Json { ignoreUnknownKeys = true },
) : SponsorCache {
    override fun read(): Pair<SponsorsResponseDto, Long>? {
        val raw = prefs.getString(KEY_JSON, null) ?: return null
        val response =
            try {
                json.decodeFromString(SponsorsResponseDto.serializer(), raw)
            } catch (e: SerializationException) {
                return null
            }
        return response to prefs.getLong(KEY_FETCHED_AT, 0L)
    }

    override fun write(
        response: SponsorsResponseDto,
        fetchedAtMillis: Long,
    ) {
        prefs.edit {
            putString(KEY_JSON, json.encodeToString(SponsorsResponseDto.serializer(), response))
            putLong(KEY_FETCHED_AT, fetchedAtMillis)
        }
    }

    private companion object {
        const val KEY_JSON = "platinum_sponsors_json"
        const val KEY_FETCHED_AT = "platinum_sponsors_fetched_at"
    }
}

/** The bundled-sample build has no TBA key to fetch with, so it credits no one. */
object NoSponsorRepository : SponsorRepository {
    override fun sponsors(): Flow<List<Sponsor>> = emptyFlow()
}
