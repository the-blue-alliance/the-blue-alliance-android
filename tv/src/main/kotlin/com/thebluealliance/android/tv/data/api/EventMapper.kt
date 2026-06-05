package com.thebluealliance.android.tv.data.api

import com.thebluealliance.android.data.remote.dto.EventDto
import com.thebluealliance.android.tv.data.model.Event
import com.thebluealliance.android.tv.data.model.WebcastResolver
import java.time.LocalDate

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
                .orEmpty()
                .filter { it.channel.isNotBlank() }
                .map { WebcastResolver.resolve(it.type, it.channel, it.file, it.date) },
    )
}
