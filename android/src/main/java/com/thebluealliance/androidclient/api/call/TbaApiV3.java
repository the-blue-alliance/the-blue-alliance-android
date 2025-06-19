package com.thebluealliance.androidclient.api.call;


import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import thebluealliance.api.model.APIStatus;

public interface TbaApiV3 {
  /**
   * API Status Request
   * Get various metadata about the TBA API
   * @return Call&lt;ApiStatus&gt;
   */
  
  @GET("api/v3/status")
  Call<APIStatus> fetchApiStatus();

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

}
