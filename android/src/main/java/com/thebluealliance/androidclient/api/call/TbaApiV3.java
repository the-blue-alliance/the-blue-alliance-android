package com.thebluealliance.androidclient.api.call;



import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;

import com.thebluealliance.androidclient.models.ApiStatus;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.RankingResponseObject;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TbaApiV3 {
  /**
   * API Status Request
   * Get various metadata about the TBA API
   * @return Call&lt;ApiStatus&gt;
   */
  
  @GET("api/v3/status")
  Call<ApiStatus> fetchApiStatus();
    

  /**
   * District Events Request
   * Fetch a list of events within a given district
   * @param districtKey Key identifying a district (e.g. &#39;2016ne&#39;) (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Event>&gt;
   */
  
  @GET("api/v3/district/{district_key}/events")
  Call<List<Event>> fetchDistrictEvents(
    @Path("district_key") String districtKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * District List Request
   * Fetch a list of active districts in the given year
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<District>&gt;
   */
  
  @GET("api/v3/districts/{year}")
  Call<List<District>> fetchDistrictList(
    @Path("year") Integer year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * District Rankings Reques
   * Fetch district rankings
   * @param districtKey Key identifying a district (e.g. &#39;2016ne&#39;) (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<DistrictRanking>&gt;
   */
  
  @GET("api/v3/district/{district_key}/rankings")
  Call<List<DistrictRanking>> fetchDistrictRankings(
    @Path("district_key") String districtKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * District Teams Request
   * Fetch a list of teams within a given district
   * @param districtKey Key identifying a district (e.g. &#39;2016ne&#39;) (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Team>&gt;
   */
  
  @GET("api/v3/district/{district_key}/teams")
  Call<List<Team>> fetchDistrictTeamsInYear(
    @Path("district_key") String districtKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Event Info Request
   * Fetch details for one event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;Event&gt;
   */
  
  @GET("api/v3/event/{event_key}")
  Call<Event> fetchEvent(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Event Alliances Request
   * Fetch alliance information for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<EventAlliance>&gt;
   */
  
  @GET("api/v3/event/{event_key}/alliances")
  Call<List<EventAlliance>> fetchEventAlliances(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Event Awards Request
   * Fetch awards for the given event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Award>&gt;
   */
  
  @GET("api/v3/event/{event_key}/awards")
  Call<List<Award>> fetchEventAwards(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Event District Points Request
   * Fetch district points for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;String&gt;
   */
  
  @GET("api/v3/event/{event_key}/district_points")
  Call<String> fetchEventDistrictPoints(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Event Matches Request
   * Fetch matches for the given event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Match>&gt;
   */
  
  @GET("api/v3/event/{event_key}/matches")
  Call<List<Match>> fetchEventMatches(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Event OPR Request
   * Fetch OPR details for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;String&gt;
   */
  
  @GET("api/v3/event/{event_key}/oprs")
  Call<String> fetchEventOPR(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Event Rankings Request
   * Fetch ranking details for one event.
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;RankingResponseObject&gt;
   */
  
  @GET("api/v3/event/{event_key}/rankings")
  Call<RankingResponseObject> fetchEventRankings(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Event Teams Request
   * Fetch teams attending the given event
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Team>&gt;
   */
  
  @GET("api/v3/event/{event_key}/teams")
  Call<List<Team>> fetchEventTeams(
    @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Event List Request
   * Fetch all events in a year
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Event>&gt;
   */
  
  @GET("api/v3/events/{year}")
  Call<List<Event>> fetchEventsInYear(
    @Path("year") Integer year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Match Request
   * Fetch details about a single match
   * @param matchKey Key identifying a single match, has format [event key]_[match id] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;Match&gt;
   */
  
  @GET("api/v3/match/{match_key}")
  Call<Match> fetchMatch(
    @Path("match_key") String matchKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Single Team Request
   * This endpoit returns information about a single team
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;Team&gt;
   */
  
  @GET("api/v3/team/{team_key}")
  Call<Team> fetchTeam(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Team Event Awards Request
   * Fetch all awards won by a single team at an event
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Award>&gt;
   */
  
  @GET("api/v3/team/{team_key}/event/{event_key}/awards")
  Call<List<Award>> fetchTeamAtEventAwards(
    @Path("team_key") String teamKey, @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Team Event Matches Request
   * Fetch all matches for a single team at an event
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param eventKey Key identifying a single event, has format [year][event code] (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Match>&gt;
   */
  
  @GET("api/v3/team/{team_key}/event/{event_key}/matches")
  Call<List<Match>> fetchTeamAtEventMatches(
    @Path("team_key") String teamKey, @Path("event_key") String eventKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Team Districts Request
   * Fetch all district keys that a team has competed in
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<String>&gt;
   */
  
  @GET("api/v3/team/{team_key}/districts")
  Call<List<String>> fetchTeamDistricts(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Team Events Request
   * Fetch all events for a given team in a given year
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Event>&gt;
   */
  
  @GET("api/v3/team/{team_key}/{year}/events")
  Call<List<Event>> fetchTeamEvents(
    @Path("team_key") String teamKey, @Path("year") Integer year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Team Media Request
   * Fetch media associated with a team in a given year
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param year A specific year to request data for. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Media>&gt;
   */
  
  @GET("api/v3/team/{team_key}/media/{year}")
  Call<List<Media>> fetchTeamMediaInYear(
    @Path("team_key") String teamKey, @Path("year") Integer year, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Team List Request
   * Returns a page containing 500 teams
   * @param page A page of teams, zero-indexed. Each page consists of teams whose numbers start at start &#x3D; 500 * page_num and end at end &#x3D; start + 499, inclusive. (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Team>&gt;
   */
  
  @GET("api/v3/teams/{page}")
  Call<List<Team>> fetchTeamPage(
    @Path("page") Integer page, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Team Robots Request
   * Fetch all robots a team has made since 2015. Robot names are scraped from TIMS.
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Robot>&gt;
   */
  
  @GET("api/v3/team/{team_key}/robots")
  Call<List<Robot>> fetchTeamRobots(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Team Social Media Request
   * Fetch social media profiles for a team
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Media>&gt;
   */
  
  @GET("api/v3/team/{team_key}/social_media")
  Call<List<Media>> fetchTeamSocialMedia(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

  /**
   * Team Years Participated Request
   * Fetch the years for which the team was registered for an event
   * @param teamKey Key identifying a single team, has format frcXXXX, where XXXX is the team number (required)
   * @param xTBACache Special TBA App Internal Header to indicate caching strategy. (optional)
   * @return Call&lt;List<Integer>&gt;
   */
  
  @GET("api/v3/team/{team_key}/years_participated")
  Call<List<Integer>> fetchTeamYearsParticipated(
    @Path("team_key") String teamKey, @Header("X-TBA-Cache") String xTBACache
  );

}
