package com.thebluealliance.api.rx;

import rx.Observable;
import retrofit2.Response;

import retrofit2.http.*;

import okhttp3.RequestBody;

import com.thebluealliance.api.model.ApiStatus;
import com.thebluealliance.api.model.Event;
import com.thebluealliance.api.model.Team;
import com.thebluealliance.api.model.Award;
import com.thebluealliance.api.model.Match;
import com.thebluealliance.api.model.Media;
import com.thebluealliance.api.model.Robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TbaApiV2 {
  /**
   * API Status Request
   * Get various metadata about the TBA API
   * @return Call&lt;ApiStatus&gt;
   */
  
  @GET("api/v2/status")
  Observable<Response<ApiStatus>> fetchApiStatus();
    

  /**
   * District Events Request
   * Fetch a list of events within a given district
   * @param districtShort Short string identifying a district (e.g. &#39;ne&#39;) (required)
   * @param year A specific year to request data for. (required)
   * @return Call&lt;List<Event>&gt;
   */
  
  @GET("district/{district_short}/{year}/events")
  Observable<Response<List<Event>>> fetchDistrictEvents(
    @Path("district_short") String districtShort, @Path("year") String year
  );

  /**
   * District List Request
   * Fetch a list of active districts in the given year
   * @param year A specific year to request data for. (required)
   * @return Call&lt;String&gt;
   */
  
  @GET("districts/{year}")
  Observable<Response<String>> fetchDistrictList(
    @Path("year") String year
  );

  /**
   * District Rankings Reques
   * Fetch district rankings
   * @param districtShort Short string identifying a district (e.g. &#39;ne&#39;) (required)
   * @param year A specific year to request data for. (required)
   * @return Call&lt;String&gt;
   */
  
  @GET("district/{district_short}/{year}/rankings")
  Observable<Response<String>> fetchDistrictRankings(
    @Path("district_short") String districtShort, @Path("year") String year
  );

  /**
   * District Teams Request
   * Fetch a list of teams within a given district
   * @param districtShort Short string identifying a district (e.g. &#39;ne&#39;) (required)
   * @param year A specific year to request data for. (required)
   * @return Call&lt;List<Team>&gt;
   */
  
  @GET("district/{district_short}/{year}/teams")
  Observable<Response<List<Team>>> fetchDistrictTeamsInYear(
    @Path("district_short") String districtShort, @Path("year") String year
  );

  /**
   * Event Info Request
   * Fetch details for one event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @return Call&lt;Event&gt;
   */
  
  @GET("event/{event_key}")
  Observable<Response<Event>> fetchEvent(
    @Path("event_key") String eventKey
  );

  /**
   * Event Awards Request
   * Fetch awards for the given event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @return Call&lt;List<Award>&gt;
   */
  
  @GET("events/{event_key}/awards")
  Observable<Response<List<Award>>> fetchEventAwards(
    @Path("event_key") String eventKey
  );

  /**
   * Event District Points Request
   * Fetch district points for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @return Call&lt;String&gt;
   */
  
  @GET("event/{event_key}/district_points")
  Observable<Response<String>> fetchEventDistrictPoints(
    @Path("event_key") String eventKey
  );

  /**
   * Event Matches Request
   * Fetch matches for the given event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @return Call&lt;List<Match>&gt;
   */
  
  @GET("events/{event_key}/matches")
  Observable<Response<List<Match>>> fetchEventMatches(
    @Path("event_key") String eventKey
  );

  /**
   * Event Rankings Request
   * Fetch ranking details for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @return Call&lt;String&gt;
   */
  
  @GET("event/{event_key}/rankings")
  Observable<Response<String>> fetchEventRankings(
    @Path("event_key") String eventKey
  );

  /**
   * Event Stats Request
   * Fetch stats details for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @return Call&lt;String&gt;
   */
  
  @GET("event/{event_key}/stats")
  Observable<Response<String>> fetchEventStats(
    @Path("event_key") String eventKey
  );

  /**
   * Event Teams Request
   * Fetch teams attending the given event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @return Call&lt;List<Team>&gt;
   */
  
  @GET("events/{event_key}/teams")
  Observable<Response<List<Team>>> fetchEventTeams(
    @Path("event_key") String eventKey
  );

  /**
   * Event List Request
   * Fetch all events in a year
   * @param year A specific year to request data for. (required)
   * @return Call&lt;List<Event>&gt;
   */
  
  @GET("events/{year}")
  Observable<Response<List<Event>>> fetchEventsInYear(
    @Path("year") String year
  );

  /**
   * Match Request
   * Fetch details about a single match
   * @param matchKey Key identifying a single match, has format [event key]_[match id] (required)
   * @return Call&lt;Match&gt;
   */
  
  @GET("match/{match_key}")
  Observable<Response<Match>> fetchMatch(
    @Path("match_key") String matchKey
  );

  /**
   * Single Team Request
   * This endpoit returns information about a single team
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @return Call&lt;Team&gt;
   */
  
  @GET("team/{team_key}")
  Observable<Response<Team>> fetchTeam(
    @Path("team_key") String teamKey
  );

  /**
   * Team Event Awards Request
   * Fetch all awards won by a single team at an event
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @return Call&lt;List<Award>&gt;
   */
  
  @GET("team/{team_key}/event/{event_key}/awards")
  Observable<Response<List<Award>>> fetchTeamAtEventAwards(
    @Path("team_key") String teamKey, @Path("event_key") String eventKey
  );

  /**
   * Team Event Matches Request
   * Fetch all matches for a single team at an event
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @return Call&lt;List<Match>&gt;
   */
  
  @GET("team/{team_key}/event/{event_key}/matches")
  Observable<Response<List<Match>>> fetchTeamAtEventMatches(
    @Path("team_key") String teamKey, @Path("event_key") String eventKey
  );

  /**
   * Team History Awards Request
   * Fetch all awards a team has won
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @return Call&lt;List<Award>&gt;
   */
  
  @GET("team/{team_key}/history/awards")
  Observable<Response<List<Award>>> fetchTeamAwardHistory(
    @Path("team_key") String teamKey
  );

  /**
   * Team History District Request
   * Fetch all district keys that a team has competed in
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @return Call&lt;List<String>&gt;
   */
  
  @GET("team/{team_key}/history/districts")
  Observable<Response<List<String>>> fetchTeamDistrictHistory(
    @Path("team_key") String teamKey
  );

  /**
   * Team History Events Request
   * Fetch all events a team has event registered for
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @return Call&lt;List<Event>&gt;
   */
  
  @GET("team/{team_key}/history/events")
  Observable<Response<List<Event>>> fetchTeamEventHistory(
    @Path("team_key") String teamKey
  );

  /**
   * Team Events Request
   * Fetch all events for a given team in a given year
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param year A specific year to request data for. (required)
   * @return Call&lt;List<Event>&gt;
   */
  
  @GET("team/{team_key}/{year}/events")
  Observable<Response<List<Event>>> fetchTeamEvents(
    @Path("team_key") String teamKey, @Path("year") String year
  );

  /**
   * Team Media Request
   * Fetch media associated with a team in a given year
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param year A specific year to request data for. (required)
   * @return Call&lt;List<Media>&gt;
   */
  
  @GET("team/{team_key}/{year}/media")
  Observable<Response<List<Media>>> fetchTeamMediaInYear(
    @Path("team_key") String teamKey, @Path("year") String year
  );

  /**
   * Team List Request
   * Returns a page containing 500 teams
   * @param page A page of teams, zero-indexed. Each page consists of teams whose numbers start at start &#x3D; 500 * page_num and end at end &#x3D; start + 499, inclusive. (required)
   * @return Call&lt;List<Team>&gt;
   */
  
  @GET("teams/{page}")
  Observable<Response<List<Team>>> fetchTeamPage(
    @Path("page") String page
  );

  /**
   * Team History Robots Request
   * Fetch all robots a team has made since 2015. Robot names are scraped from TIMS.
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @return Call&lt;List<Robot>&gt;
   */
  
  @GET("team/{team_key}/history/robots")
  Observable<Response<List<Robot>>> fetchTeamRobotHistory(
    @Path("team_key") String teamKey
  );

  /**
   * Team Years Participated Request
   * Fetch the years for which the team was registered for an event
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @return Call&lt;List<Integer>&gt;
   */
  
  @GET("team/{team_key}/years_participated")
  Observable<Response<List<Integer>>> fetchTeamYearsParticipated(
    @Path("team_key") String teamKey
  );

}
