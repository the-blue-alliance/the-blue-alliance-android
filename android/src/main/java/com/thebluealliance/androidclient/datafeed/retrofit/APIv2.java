package com.thebluealliance.androidclient.datafeed.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.thebluealliance.androidclient.api.ApiV2Constants;
import com.thebluealliance.androidclient.models.ApiStatus;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Interface for TBA API spec to be used with Retrofit
 */
public interface APIv2 {

    @GET("/api/v2/teams/{pageNum}")
    Observable<Response<List<Team>>> fetchTeamPage(
            @Path("pageNum") int pageNum,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    /**
     * Use to execute synchronous request in {@link com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData}
     */
    @GET("/api/v2/teams/{pageNum}")
    Call<List<Team>> fetchTeamPageCall(
            @Path("pageNum") int pageNum,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/team/{teamKey}")
    Observable<Response<Team>> fetchTeam(
            @Path("teamKey") String teamKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/team/{teamKey}/{year}/events")
    Observable<Response<List<Event>>> fetchTeamEvents(
            @Path("teamKey") String teamKey,
            @Path("year") int year,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/team/{teamKey}/event/{eventKey}/awards")
    Observable<Response<List<Award>>> fetchTeamAtEventAwards(
            @Path("teamKey") String teamKey,
            @Path("eventKey") String eventKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/team/{teamKey}/event/{eventKey}/matches")
    Observable<Response<List<Match>>> fetchTeamAtEventMatches(
            @Path("teamKey") String teamKey,
            @Path("eventKey") String eventKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/team/{teamKey}/years_participated")
    Observable<Response<JsonArray>> fetchTeamYearsParticipated(
            @Path("teamKey") String teamKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/team/{teamKey}/{year}/media")
    Observable<Response<List<Media>>> fetchTeamMediaInYear(
            @Path("teamKey") String teamKey,
            @Path("year") int year,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/team/{teamKey}/history/events")
    Observable<Response<List<Event>>> fetchTeamEventHistory(
            @Path("teamKey") String teamKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/team/{teamKey}/history/awards")
    Observable<Response<List<Award>>> fetchTeamEventAwards(
            @Path("teamKey") String teamKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/events/{year}")
    Observable<Response<List<Event>>> fetchEventsInYear(
            @Path("year") int year,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    /**
     * Use to execute synchronous request in {@link com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData}
     */
    @GET("/api/v2/events/{year}")
    Call<List<Event>> fetchEventsInYearCall(
            @Path("year") int year,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/event/{eventKey}")
    Observable<Response<Event>> fetchEvent(
            @Path("eventKey") String eventKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/event/{eventKey}/teams")
    Observable<Response<List<Team>>> fetchEventTeams(
            @Path("eventKey") String eventKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/event/{eventKey}/rankings")
    Observable<Response<JsonElement>> fetchEventRankings(
            @Path("eventKey") String eventKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/event/{eventKey}/matches")
    Observable<Response<List<Match>>> fetchEventMatches(
            @Path("eventKey") String eventKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/event/{eventKey}/stats")
    Observable<Response<JsonElement>> fetchEventStats(
            @Path("eventKey") String eventKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/event/{eventKey}/awards")
    Observable<Response<List<Award>>> fetchEventAwards(
            @Path("eventKey") String eventKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/event/{eventKey}/district_points")
    Observable<Response<JsonElement>> fetchEventDistrictPoints(
            @Path("eventKey") String eventKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/districts/{year}")
    Observable<Response<List<District>>> fetchDistrictList(
            @Path("year") int year,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    /**
     * Use to execute synchronous request in {@link com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData}
     */
    @GET("/api/v2/districts/{year}")
    Call<List<District>> fetchDistrictListCall(
            @Path("year") int year,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/district/{districtShort}/{year}/events")
    Observable<Response<List<Event>>> fetchDistrictEvents(
            @Path("districtShort") String districtShort,
            @Path("year") int year,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/district/{districtShort}/{year}/rankings")
    Observable<Response<List<DistrictTeam>>> fetchDistrictRankings(
            @Path("districtShort") String districtShort,
            @Path("year") int year,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/match/{matchKey}")
    Observable<Response<Match>> fetchMatch(
            @Path("matchKey") String matchKey,
            @Header(ApiV2Constants.TBA_CACHE_HEADER) String cacheHeader);

    @GET("/api/v2/status")
    Observable<Response<ApiStatus>> status();

    /**
     * Use to execute synchronous request in {@link com.thebluealliance.androidclient.background.firstlaunch.LoadTBAData}
     */
    @GET("/api/v2/status")
    Call<ApiStatus> statusCall();
}
