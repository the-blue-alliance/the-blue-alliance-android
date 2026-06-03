package com.thebluealliance.android.tv.data.api

import com.thebluealliance.android.tv.data.model.Event
import com.thebluealliance.android.tv.data.model.WebcastResolver
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

/** Mirrors the TBA API v3 Event model (only the fields we use). */
@Serializable
data class EventDto(
    val key: String,
    val name: String = "",
    @SerialName("short_name") val shortName: String? = null,
    val city: String? = null,
    @SerialName("state_prov") val stateProv: String? = null,
    val country: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    val webcasts: List<WebcastDto> = emptyList(),
)

@Serializable
data class WebcastDto(
    val type: String = "",
    val channel: String = "",
    val file: String? = null,
    val date: String? = null,
)

/** Maps a DTO to a domain Event, or null if it lacks the dates we need to place it on a timeline. */
fun EventDto.toDomainOrNull(): Event? {
    val start = startDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return null
    val end = endDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: start
    return Event(
        key = key,
        name = name,
        shortName = shortName,
        city = city,
        stateProv = stateProv,
        country = country,
        startDate = start,
        endDate = end,
        webcasts =
            webcasts
                .filter { it.channel.isNotBlank() }
                .map { WebcastResolver.resolve(it.type, it.channel, it.file, it.date) },
    )
}
