package com.thebluealliance.android.tv.data.api

import com.thebluealliance.android.data.remote.dto.EventDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TbaApi {
    @GET("api/v3/events/{year}")
    suspend fun getEvents(
        @Path("year") year: Int,
    ): List<EventDto>
}
