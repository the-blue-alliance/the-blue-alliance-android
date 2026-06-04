package com.thebluealliance.android.tv.data.repository

import android.content.Context
import com.thebluealliance.android.tv.data.api.EventDto
import com.thebluealliance.android.tv.data.api.TbaApi
import com.thebluealliance.android.tv.data.api.TbaJson
import com.thebluealliance.android.tv.data.api.toDomainOrNull
import com.thebluealliance.android.tv.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import java.time.LocalDate
import java.time.temporal.ChronoUnit

interface EventRepository {
    /** All events for [year] we could place on a timeline. Throws on network/parse failure. */
    suspend fun getEvents(year: Int): List<Event>
}

/** Hits the live TBA API (prod or a local dev server). */
class NetworkEventRepository(
    private val api: TbaApi,
) : EventRepository {
    override suspend fun getEvents(year: Int): List<Event> =
        api.getEvents(year).mapNotNull { it.toDomainOrNull() }
}

/** The day the bundled fixture's "live" cluster is authored around; see [anchoredToToday]. */
val FIXTURE_ANCHOR: LocalDate = LocalDate.of(2026, 5, 30)

/**
 * Slides every bundled event by (today − anchor) whole days so the sample feed keeps the same
 * Live/Upcoming/Recent shape no matter what day someone opens the app. Without this, the bundled
 * dates would age out and the Live hero — the whole point of the app — would vanish a day later.
 * Network data is real and never shifted; this only touches the bundled demo fixture.
 */
fun List<Event>.anchoredToToday(
    today: LocalDate,
    anchor: LocalDate = FIXTURE_ANCHOR,
): List<Event> {
    val delta = ChronoUnit.DAYS.between(anchor, today)
    if (delta == 0L) return this
    return map {
        it.copy(
            startDate = it.startDate.plusDays(delta),
            endDate = it.endDate.plusDays(delta),
            webcasts = it.webcasts.map { w -> w.copy(date = w.date?.plusDays(delta)) },
        )
    }
}

/** Reads a bundled fixture from assets — used when no API key is configured. */
class AssetEventRepository(
    context: Context,
    private val assetName: String = "events_fixture.json",
    private val today: LocalDate = LocalDate.now(),
) : EventRepository {
    private val appContext = context.applicationContext

    override suspend fun getEvents(year: Int): List<Event> =
        withContext(Dispatchers.IO) {
            val json =
                appContext.assets
                    .open(assetName)
                    .bufferedReader()
                    .use { it.readText() }
            TbaJson
                .decodeFromString(ListSerializer(EventDto.serializer()), json)
                .mapNotNull { it.toDomainOrNull() }
                .anchoredToToday(today)
        }
}
