package com.thebluealliance.androidclient.datafeed;

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

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Interface for TBA API spec to be used with Retrofit
 */
public interface APIv2 {

    String TBA_URL = "http://www.thebluealliance.com/";

    @GET("/api/v2/teams/{pageNum}")
    Observable<List<Team>> fetchTeamPage(@Path("pageNum") int pageNum);

    @GET("/api/v2/team/{teamKey}")
    Observable<Team> fetchTeam(@Path("teamKey") String teamKey);

    @GET("/api/v2/team/{teamKey}/{year}/events")
    Observable<List<Event>> fetchTeamEvents(
      @Path("teamKey") String teamKey,
      @Path("year") int year);

    @GET("/api/v2/team/{teamKey}/event/{eventKey}/awards")
    Observable<List<Award>> fetchTeamAtEventAwards(
      @Path("teamKey") String teamKey,
      @Path("eventKey") String eventKey);

    @GET("/api/v2/team/{teamKey}/event/{eventKey}/matches")
    Observable<List<Match>> fetchTeamAtEventMatches(
      @Path("teamKey") String teamKey,
      @Path("eventKey") String eventKey);

    @GET("/api/v2/team/{teamKey}/years_participated")
    Observable<JsonArray> fetchTeamYearsParticipated(@Path("teamKey") String teamKey);

    @GET("/api/v2/team/{teamKey}/{year}/media")
    Observable<List<Media>> fetchTeamMediaInYear(
      @Path("teamKey") String teamKey,
      @Path("year") int year);

    @GET("/api/v2/team/{teamKey}/history/events")
    Observable<List<Event>> fetchTeamEventHistory(
      @Path("teamKey") String teamKey);

    @GET("/api/v2/team/{teamKey}/history/awards")
    Observable<List<Award>> fetchTeamEventAwards(
      @Path("teamKey") String teamKey);

    @GET("/api/v2/events/{year}")
    Observable<List<Event>> fetchEventsInYear(@Path("year") int year);

    @GET("/api/v2/event/{eventKey}")
    Observable<Event> fetchEvent(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/teams")
    Observable<List<Team>> fetchEventTeams(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/rankings")
    Observable<JsonArray> fetchEventRankings(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/matches")
    Observable<List<Match>> fetchEventMatches(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/stats")
    Observable<JsonObject> fetchEventStats(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/awards")
    Observable<List<Award>> fetchEventAwards(@Path("eventKey") String eventKey);

    @GET("/api/v2/event/{eventKey}/district_points")
    Observable<JsonObject> fetchEventDistrictPoints(@Path("eventKey") String eventKey);

    @GET("/api/v2/districts/{year}")
    Observable<List<District>> fetchDistrictList(@Path("year") int year);

    @GET("/api/v2/district/{districtShort}/{year}/events")
    Observable<List<Event>> fetchDistrictEvents(
      @Path("districtShort") String districtShort,
      @Path("year") int year);

    @GET("/api/v2/district/{districtShort}/{year}/rankings")
    Observable<List<DistrictTeam>> fetchDistrictRankings(
      @Path("districtShort") String districtShort,
      @Path("year") int year);

    @GET("/api/v2/match/{matchKey}")
    Observable<Match> fetchMatch(
      @Path("matchKey") String matchKey);
}
