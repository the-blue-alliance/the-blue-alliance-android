package com.thebluealliance.android.data.remote

import com.thebluealliance.android.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path

interface TbaApi {

    // Teams
    @GET("api/v3/teams/{page_num}")
    suspend fun getTeams(@Path("page_num") page: Int): List<TeamDto>

    @GET("api/v3/team/{team_key}")
    suspend fun getTeam(@Path("team_key") teamKey: String): TeamDto

    @GET("api/v3/event/{event_key}/teams")
    suspend fun getEventTeams(@Path("event_key") eventKey: String): List<TeamDto>

    @GET("api/v3/district/{district_key}/teams")
    suspend fun getDistrictTeams(@Path("district_key") districtKey: String): List<TeamDto>

    // Events
    @GET("api/v3/events/{year}")
    suspend fun getEventsForYear(@Path("year") year: Int): List<EventDto>

    @GET("api/v3/event/{event_key}")
    suspend fun getEvent(@Path("event_key") eventKey: String): EventDto

    @GET("api/v3/team/{team_key}/events/{year}")
    suspend fun getTeamEvents(
        @Path("team_key") teamKey: String,
        @Path("year") year: Int,
    ): List<EventDto>

    @GET("api/v3/district/{district_key}/events")
    suspend fun getDistrictEvents(@Path("district_key") districtKey: String): List<EventDto>

    // Matches
    @GET("api/v3/event/{event_key}/matches")
    suspend fun getEventMatches(@Path("event_key") eventKey: String): List<MatchDto>

    @GET("api/v3/team/{team_key}/event/{event_key}/matches")
    suspend fun getTeamEventMatches(
        @Path("team_key") teamKey: String,
        @Path("event_key") eventKey: String,
    ): List<MatchDto>

    @GET("api/v3/match/{match_key}")
    suspend fun getMatch(@Path("match_key") matchKey: String): MatchDto

    // Awards
    @GET("api/v3/event/{event_key}/awards")
    suspend fun getEventAwards(@Path("event_key") eventKey: String): List<AwardDto>

    @GET("api/v3/team/{team_key}/event/{event_key}/awards")
    suspend fun getTeamEventAwards(
        @Path("team_key") teamKey: String,
        @Path("event_key") eventKey: String,
    ): List<AwardDto>

    // Rankings
    @GET("api/v3/event/{event_key}/rankings")
    suspend fun getEventRankings(@Path("event_key") eventKey: String): RankingResponseDto

    // Alliances
    @GET("api/v3/event/{event_key}/alliances")
    suspend fun getEventAlliances(@Path("event_key") eventKey: String): List<EventAllianceDto>

    // Districts
    @GET("api/v3/districts/{year}")
    suspend fun getDistrictsForYear(@Path("year") year: Int): List<DistrictDto>

    // District Rankings
    @GET("api/v3/district/{district_key}/rankings")
    suspend fun getDistrictRankings(@Path("district_key") districtKey: String): List<DistrictRankingDto>?

    // Media
    @GET("api/v3/team/{team_key}/media/{year}")
    suspend fun getTeamMedia(
        @Path("team_key") teamKey: String,
        @Path("year") year: Int,
    ): List<MediaDto>
}
