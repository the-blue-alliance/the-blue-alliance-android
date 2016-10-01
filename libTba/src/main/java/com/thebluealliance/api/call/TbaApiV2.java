package com.thebluealliance.api.call;



import com.thebluealliance.api.model.IApiStatus;
import com.thebluealliance.api.model.IAward;
import com.thebluealliance.api.model.IEvent;
import com.thebluealliance.api.model.IMatch;
import com.thebluealliance.api.model.IMedia;
import com.thebluealliance.api.model.IRobot;
import com.thebluealliance.api.model.ITeam;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface TbaApiV2 {
  /**
   * API Status Request
   * Get various metadata about the TBA API
   * @return Call&lt;IApiStatus&gt;
   */
  
  @GET("api/v2/status")
  Call<IApiStatus> fetchIApiStatus();
    

  /**
   * District IEvents Request
   * Fetch a list of events within a given district
   * @param districtShort Short string identifying a district (e.g. &#39;ne&#39;) (required)
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<IEvent>&gt;
   */
  
  @GET("district/{district_short}/{year}/events")
  Call<List<IEvent>> fetchDistrictIEvents(
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
  Call<String> fetchDistrictList(
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
  Call<String> fetchDistrictRankings(
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
  Call<List<ITeam>> fetchDistrictITeamsInYear(
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
  Call<IEvent> fetchIEvent(
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
  Call<List<IAward>> fetchIEventIAwards(
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
  Call<String> fetchIEventDistrictPoints(
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
  Call<List<IMatch>> fetchIEventIMatches(
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
  Call<String> fetchIEventRankings(
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
  Call<String> fetchIEventStats(
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
  Call<List<ITeam>> fetchIEventITeams(
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
  Call<List<IEvent>> fetchIEventsInYear(
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
  Call<IMatch> fetchIMatch(
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
  Call<ITeam> fetchITeam(
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
  Call<List<IAward>> fetchITeamAtIEventIAwards(
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
  Call<List<IMatch>> fetchITeamAtIEventIMatches(
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
  Call<List<IAward>> fetchITeamIAwardHistory(
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
  Call<List<String>> fetchITeamDistrictHistory(
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
  Call<List<IEvent>> fetchITeamIEventHistory(
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
  Call<List<IEvent>> fetchITeamIEvents(
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
  Call<List<IMedia>> fetchITeamIMediaInYear(
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
  Call<List<ITeam>> fetchITeamPage(
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
  Call<List<IRobot>> fetchITeamIRobotHistory(
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
  Call<List<Integer>> fetchITeamYearsParticipated(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

}
