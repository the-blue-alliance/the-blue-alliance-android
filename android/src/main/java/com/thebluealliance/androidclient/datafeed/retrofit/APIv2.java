package com.thebluealliance.androidclient.datafeed.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Interface for TBA API spec to be used with Retrofit
 */
public interface APIv2 {

    String TBA_URL = "http://www.thebluealliance.com/";

    @GET("/api/v2/teams/{pageNum}")
    Observable<Response<List<Team>>> fetchTeamPage(@Path("pageNum") int pageNum);

    @GET("/api/v2/team/{teamKey}")
    Observable<Response<Team>> fetchTeam(@Path("teamKey") String teamKey);

    @GET("/api/v2/team/{teamKey}/{year}/events")
    Observable<Response<List<Event>>> fetchTeamEvents(
      @Path("teamKey") String teamKey,
      @Path("year") int year);

    @GET("/api/v2/team/{teamKey}/event/{eventKey}/awards")
    Observable<Response<List<Award>>> fetchTeamAtEventAwards(
      @Path("teamKey") String teamKey,
      @Path("eventKey") String eventKey);

    @GET("/api/v2/team/{teamKey}/event/{eventKey}/matches")
    Observable<Response<List<Match>>> fetchTeamAtEventMatches(
      @Path("teamKey") String teamKey,
      @Path("eventKey") String eventKey);

    @GET("/api/v2/team/{teamKey}/years_participated")
    Observable<Response<JsonArray>> fetchTeamYearsParticipated(@Path("teamKey") String teamKey);

    @GET("/api/v2/team/{teamKey}/{year}/media")
    Observable<Response<List<Media>>> fetchTeamMediaInYear(
      @Path("teamKey") String teamKey,
      @Path("year") int year);

    @GET("/api/v2/team/{teamKey}/history/events")
    Observable<Response<List<Event>>> fetchTeamEventHistory(
      @Path("teamKey") String teamKey);

    @GET("/api/v2/team/{teamKey}/history/awards")
    Observable<Response<List<Award>>> fetchTeamEventAwards(
      @Path("teamKey") String teamKey);

    @GET("/api/v2/events/{year}")
    Observable<Response<List<Event>>> fetchEventsInYear(@Path("year") int year);

    @GET("/api/v2/event/{eventKey}")
    Observable<Response<Event>> fetchEvent(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/teams")
    Observable<Response<List<Team>>> fetchEventTeams(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/rankings")
    Observable<Response<JsonArray>> fetchEventRankings(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/matches")
    Observable<Response<List<Match>>> fetchEventMatches(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/stats")
    Observable<Response<JsonObject>> fetchEventStats(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/awards")
    Observable<Response<List<Award>>> fetchEventAwards(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/district_points")
    Observable<Response<JsonObject>> fetchEventDistrictPoints(@Path("eventKey") String eventKey);

    @GET("/api/v2/districts/{year}")
    Observable<Response<List<District>>> fetchDistrictList(@Path("year") int year);

    @GET("/api/v2/district/{districtShort}/{year}/events")
    Observable<Response<List<Event>>> fetchDistrictEvents(
      @Path("districtShort") String districtShort,
      @Path("year") int year);

    @GET("/api/v2/district/{districtShort}/{year}/rankings")
    Observable<Response<List<DistrictTeam>>> fetchDistrictRankings(
      @Path("districtShort") String districtShort,
      @Path("year") int year);

    @GET("/api/v2/match/{matchKey}")
    Observable<Response<Match>> fetchMatch(
      @Path("matchKey") String matchKey);
}
