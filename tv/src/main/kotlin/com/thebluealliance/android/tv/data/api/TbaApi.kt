package com.thebluealliance.android.tv.data.api

import com.thebluealliance.android.data.remote.dto.EventDto
import com.thebluealliance.android.data.remote.dto.SponsorsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TbaApi {
    @GET("api/v3/events/{year}")
    suspend fun getEvents(
        @Path("year") year: Int,
    ): List<EventDto>

    // Platinum sponsors credited on the About screen
    @GET("api/v3/sponsors")
    suspend fun getSponsors(): SponsorsResponseDto
}
