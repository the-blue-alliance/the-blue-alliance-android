package com.thebluealliance.android.wear.data

import com.thebluealliance.android.wear.data.dto.EventDto
import com.thebluealliance.android.wear.data.dto.MatchDto
import com.thebluealliance.android.wear.data.dto.MediaDto
import com.thebluealliance.android.wear.data.dto.TeamDto
import retrofit2.http.GET
import retrofit2.http.Path

interface WearTbaApi {

    @GET("api/v3/team/{team_key}/events/{year}")
    suspend fun getTeamEvents(
        @Path("team_key") teamKey: String,
        @Path("year") year: Int,
    ): List<EventDto>

    @GET("api/v3/event/{event_key}/matches")
    suspend fun getEventMatches(
        @Path("event_key") eventKey: String,
    ): List<MatchDto>

    @GET("api/v3/team/{team_key}/media/{year}")
    suspend fun getTeamMedia(
        @Path("team_key") teamKey: String,
        @Path("year") year: Int,
    ): List<MediaDto>

    @GET("api/v3/team/{team_key}")
    suspend fun getTeam(
        @Path("team_key") teamKey: String,
    ): TeamDto
}
