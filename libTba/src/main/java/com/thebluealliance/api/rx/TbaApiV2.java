package com.thebluealliance.api.rx;

import rx.Observable;
import retrofit2.Response;

import retrofit2.http.*;

import okhttp3.RequestBody;

import com.thebluealliance.api.model.IApiStatus;
import com.thebluealliance.api.model.IEvent;
import com.thebluealliance.api.model.ITeam;
import com.thebluealliance.api.model.IAward;
import com.thebluealliance.api.model.IMatch;
import com.thebluealliance.api.model.IMedia;
import com.thebluealliance.api.model.IRobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TbaApiV2 {
  /**
   * API Status Request
   * Get various metadata about the TBA API
   * @return Call&lt;IApiStatus&gt;
   */
  
  @GET("api/v2/status")
  Observable<Response<IApiStatus>> fetchIApiStatus();
    

  /**
   * District IEvents Request
   * Fetch a list of events within a given district
   * @param districtShort Short string identifying a district (e.g. &#39;ne&#39;) (required)
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IEvent>&gt;
   */
  
  @GET("district/{district_short}/{year}/events")
  Observable<Response<List<IEvent>>> fetchDistrictIEvents(
    @Path("district_short") String districtShort, @Path("year") String year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * District List Request
   * Fetch a list of active districts in the given year
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;String&gt;
   */
  
  @GET("districts/{year}")
  Observable<Response<String>> fetchDistrictList(
    @Path("year") String year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * District Rankings Reques
   * Fetch district rankings
   * @param districtShort Short string identifying a district (e.g. &#39;ne&#39;) (required)
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;String&gt;
   */
  
  @GET("district/{district_short}/{year}/rankings")
  Observable<Response<String>> fetchDistrictRankings(
    @Path("district_short") String districtShort, @Path("year") String year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * District ITeams Request
   * Fetch a list of teams within a given district
   * @param districtShort Short string identifying a district (e.g. &#39;ne&#39;) (required)
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<ITeam>&gt;
   */
  
  @GET("district/{district_short}/{year}/teams")
  Observable<Response<List<ITeam>>> fetchDistrictITeamsInYear(
    @Path("district_short") String districtShort, @Path("year") String year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * IEvent Info Request
   * Fetch details for one event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;IEvent&gt;
   */
  
  @GET("event/{event_key}")
  Observable<Response<IEvent>> fetchIEvent(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * IEvent IAwards Request
   * Fetch awards for the given event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IAward>&gt;
   */
  
  @GET("events/{event_key}/awards")
  Observable<Response<List<IAward>>> fetchIEventIAwards(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * IEvent District Points Request
   * Fetch district points for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;String&gt;
   */
  
  @GET("event/{event_key}/district_points")
  Observable<Response<String>> fetchIEventDistrictPoints(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * IEvent IMatches Request
   * Fetch matches for the given event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IMatch>&gt;
   */
  
  @GET("events/{event_key}/matches")
  Observable<Response<List<IMatch>>> fetchIEventIMatches(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * IEvent Rankings Request
   * Fetch ranking details for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;String&gt;
   */
  
  @GET("event/{event_key}/rankings")
  Observable<Response<String>> fetchIEventRankings(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * IEvent Stats Request
   * Fetch stats details for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;String&gt;
   */
  
  @GET("event/{event_key}/stats")
  Observable<Response<String>> fetchIEventStats(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * IEvent ITeams Request
   * Fetch teams attending the given event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<ITeam>&gt;
   */
  
  @GET("events/{event_key}/teams")
  Observable<Response<List<ITeam>>> fetchIEventITeams(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * IEvent List Request
   * Fetch all events in a year
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IEvent>&gt;
   */
  
  @GET("events/{year}")
  Observable<Response<List<IEvent>>> fetchIEventsInYear(
    @Path("year") String year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * IMatch Request
   * Fetch details about a single match
   * @param matchKey Key identifying a single match, has format [event key]_[match id] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;IMatch&gt;
   */
  
  @GET("match/{match_key}")
  Observable<Response<IMatch>> fetchIMatch(
    @Path("match_key") String matchKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Single ITeam Request
   * This endpoit returns information about a single team
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;ITeam&gt;
   */
  
  @GET("team/{team_key}")
  Observable<Response<ITeam>> fetchITeam(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam IEvent IAwards Request
   * Fetch all awards won by a single team at an event
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IAward>&gt;
   */
  
  @GET("team/{team_key}/event/{event_key}/awards")
  Observable<Response<List<IAward>>> fetchITeamAtIEventIAwards(
    @Path("team_key") String teamKey, @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam IEvent IMatches Request
   * Fetch all matches for a single team at an event
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IMatch>&gt;
   */
  
  @GET("team/{team_key}/event/{event_key}/matches")
  Observable<Response<List<IMatch>>> fetchITeamAtIEventIMatches(
    @Path("team_key") String teamKey, @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam History IAwards Request
   * Fetch all awards a team has won
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IAward>&gt;
   */
  
  @GET("team/{team_key}/history/awards")
  Observable<Response<List<IAward>>> fetchITeamIAwardHistory(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam History District Request
   * Fetch all district keys that a team has competed in
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<String>&gt;
   */
  
  @GET("team/{team_key}/history/districts")
  Observable<Response<List<String>>> fetchITeamDistrictHistory(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam History IEvents Request
   * Fetch all events a team has event registered for
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IEvent>&gt;
   */
  
  @GET("team/{team_key}/history/events")
  Observable<Response<List<IEvent>>> fetchITeamIEventHistory(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam IEvents Request
   * Fetch all events for a given team in a given year
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IEvent>&gt;
   */
  
  @GET("team/{team_key}/{year}/events")
  Observable<Response<List<IEvent>>> fetchITeamIEvents(
    @Path("team_key") String teamKey, @Path("year") String year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam IMedia Request
   * Fetch media associated with a team in a given year
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IMedia>&gt;
   */
  
  @GET("team/{team_key}/{year}/media")
  Observable<Response<List<IMedia>>> fetchITeamIMediaInYear(
    @Path("team_key") String teamKey, @Path("year") String year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam List Request
   * Returns a page containing 500 teams
   * @param page A page of teams, zero-indexed. Each page consists of teams whose numbers start at start &#x3D; 500 * page_num and end at end &#x3D; start + 499, inclusive. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<ITeam>&gt;
   */
  
  @GET("teams/{page}")
  Observable<Response<List<ITeam>>> fetchITeamPage(
    @Path("page") String page, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam History IRobots Request
   * Fetch all robots a team has made since 2015. IRobot names are scraped from TIMS.
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IRobot>&gt;
   */
  
  @GET("team/{team_key}/history/robots")
  Observable<Response<List<IRobot>>> fetchITeamIRobotHistory(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * ITeam Years Participated Request
   * Fetch the years for which the team was registered for an event
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Integer>&gt;
   */
  
  @GET("team/{team_key}/years_participated")
  Observable<Response<List<Integer>>> fetchITeamYearsParticipated(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

}
