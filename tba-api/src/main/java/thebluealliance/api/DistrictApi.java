package thebluealliance.api;

import thebluealliance.api.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import thebluealliance.api.model.Award;
import thebluealliance.api.model.DistrictList;
import thebluealliance.api.model.DistrictRanking;
import thebluealliance.api.model.Event;
import thebluealliance.api.model.EventDistrictPoints;
import thebluealliance.api.model.EventSimple;
import thebluealliance.api.model.GetStatus401Response;
import thebluealliance.api.model.Team;
import thebluealliance.api.model.TeamSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DistrictApi {
  /**
   * 
   * Gets a list of awards in the given district.
   * @param districtKey TBA District Key, eg &#x60;2016fim&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Award&gt;&gt;
   */
  @GET("district/{district_key}/awards")
  Call<List<Award>> getDistrictAwards(
    @retrofit2.http.Path("district_key") String districtKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of events in the given district.
   * @param districtKey TBA District Key, eg &#x60;2016fim&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Event&gt;&gt;
   */
  @GET("district/{district_key}/events")
  Call<List<Event>> getDistrictEvents(
    @retrofit2.http.Path("district_key") String districtKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of event keys for events in the given district.
   * @param districtKey TBA District Key, eg &#x60;2016fim&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("district/{district_key}/events/keys")
  Call<List<String>> getDistrictEventsKeys(
    @retrofit2.http.Path("district_key") String districtKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form list of events in the given district.
   * @param districtKey TBA District Key, eg &#x60;2016fim&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;EventSimple&gt;&gt;
   */
  @GET("district/{district_key}/events/simple")
  Call<List<EventSimple>> getDistrictEventsSimple(
    @retrofit2.http.Path("district_key") String districtKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of District objects with the given district abbreviation. This accounts for district abbreviation changes, such as MAR to FMA.
   * @param districtAbbreviation District abbreviation, eg &#x60;ne&#x60; or &#x60;fim&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;DistrictList&gt;&gt;
   */
  @GET("district/{district_abbreviation}/history")
  Call<List<DistrictList>> getDistrictHistory(
    @retrofit2.http.Path("district_abbreviation") String districtAbbreviation, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of team district rankings for the given district.
   * @param districtKey TBA District Key, eg &#x60;2016fim&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;DistrictRanking&gt;&gt;
   */
  @GET("district/{district_key}/rankings")
  Call<List<DistrictRanking>> getDistrictRankings(
    @retrofit2.http.Path("district_key") String districtKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of &#x60;Team&#x60; objects that competed in events in the given district.
   * @param districtKey TBA District Key, eg &#x60;2016fim&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Team&gt;&gt;
   */
  @GET("district/{district_key}/teams")
  Call<List<Team>> getDistrictTeams(
    @retrofit2.http.Path("district_key") String districtKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of &#x60;Team&#x60; objects that competed in events in the given district.
   * @param districtKey TBA District Key, eg &#x60;2016fim&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("district/{district_key}/teams/keys")
  Call<List<String>> getDistrictTeamsKeys(
    @retrofit2.http.Path("district_key") String districtKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form list of &#x60;Team&#x60; objects that competed in events in the given district.
   * @param districtKey TBA District Key, eg &#x60;2016fim&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;TeamSimple&gt;&gt;
   */
  @GET("district/{district_key}/teams/simple")
  Call<List<TeamSimple>> getDistrictTeamsSimple(
    @retrofit2.http.Path("district_key") String districtKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of districts and their corresponding district key, for the given year.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;DistrictList&gt;&gt;
   */
  @GET("districts/{year}")
  Call<List<DistrictList>> getDistrictsByYear(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of team rankings for the Event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;EventDistrictPoints&gt;
   */
  @GET("event/{event_key}/district_points")
  Call<EventDistrictPoints> getEventDistrictPoints(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets an array of districts representing each year the team was in a district. Will return an empty array if the team was never in a district.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;DistrictList&gt;&gt;
   */
  @GET("team/{team_key}/districts")
  Call<List<DistrictList>> getTeamDistricts(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

}
