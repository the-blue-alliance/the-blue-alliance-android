package thebluealliance.api;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import thebluealliance.api.model.Award;
import thebluealliance.api.model.DistrictRanking;
import thebluealliance.api.model.Event;
import thebluealliance.api.model.EventSimple;
import thebluealliance.api.model.LeaderboardInsight;
import thebluealliance.api.model.NotablesInsight;
import thebluealliance.api.model.RegionalRanking;
import thebluealliance.api.model.Team;
import thebluealliance.api.model.TeamEventStatus;
import thebluealliance.api.model.TeamSimple;

public interface ListApi {

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
   * Gets a list of &#x60;Team&#x60; objects that competed in the given event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Team&gt;&gt;
   */
  @GET("event/{event_key}/teams")
  Call<List<Team>> getEventTeams(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of &#x60;Team&#x60; keys that competed in the given event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("event/{event_key}/teams/keys")
  Call<List<String>> getEventTeamsKeys(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form list of &#x60;Team&#x60; objects that competed in the given event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;TeamSimple&gt;&gt;
   */
  @GET("event/{event_key}/teams/simple")
  Call<List<TeamSimple>> getEventTeamsSimple(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a key-value list of the event statuses for teams competing at the given event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;Map&lt;String, TeamEventStatus&gt;&gt;
   */
  @GET("event/{event_key}/teams/statuses")
  Call<Map<String, TeamEventStatus>> getEventTeamsStatuses(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of events in the given year.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Event&gt;&gt;
   */
  @GET("events/{year}")
  Call<List<Event>> getEventsByYear(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of event keys in the given year.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("events/{year}/keys")
  Call<List<String>> getEventsByYearKeys(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form list of events in the given year.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;EventSimple&gt;&gt;
   */
  @GET("events/{year}/simple")
  Call<List<EventSimple>> getEventsByYearSimple(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of &#x60;LeaderboardInsight&#x60; objects from a specific year. Use year&#x3D;0 for overall.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;LeaderboardInsight&gt;&gt;
   */
  @GET("insights/leaderboards/{year}")
  Call<List<LeaderboardInsight>> getInsightsLeaderboardsYear(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of &#x60;NotablesInsight&#x60; objects from a specific year. Use year&#x3D;0 for overall.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;NotablesInsight&gt;&gt;
   */
  @GET("insights/notables/{year}")
  Call<List<NotablesInsight>> getInsightsNotablesYear(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets the team rankings in the regional pool for a specific year.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;RegionalRanking&gt;&gt;
   */
  @GET("regional_advancement/{year}/rankings")
  Call<List<RegionalRanking>> getRegionalRankings(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a key-value list of the event statuses for events this team has competed at in the given year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;Map&lt;String, TeamEventStatus&gt;&gt;
   */
  @GET("team/{team_key}/events/{year}/statuses")
  Call<Map<String, TeamEventStatus>> getTeamEventsStatusesByYear(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of &#x60;Team&#x60; objects, paginated in groups of 500.
   * @param pageNum Page number of results to return, zero-indexed (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Team&gt;&gt;
   */
  @GET("teams/{page_num}")
  Call<List<Team>> getTeams(
    @retrofit2.http.Path("page_num") Integer pageNum, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of &#x60;Team&#x60; objects that competed in the given year, paginated in groups of 500.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param pageNum Page number of results to return, zero-indexed (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Team&gt;&gt;
   */
  @GET("teams/{year}/{page_num}")
  Call<List<Team>> getTeamsByYear(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Path("page_num") Integer pageNum, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list Team Keys that competed in the given year, paginated in groups of 500.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param pageNum Page number of results to return, zero-indexed (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("teams/{year}/{page_num}/keys")
  Call<List<String>> getTeamsByYearKeys(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Path("page_num") Integer pageNum, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of short form &#x60;Team_Simple&#x60; objects that competed in the given year, paginated in groups of 500.
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param pageNum Page number of results to return, zero-indexed (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;TeamSimple&gt;&gt;
   */
  @GET("teams/{year}/{page_num}/simple")
  Call<List<TeamSimple>> getTeamsByYearSimple(
    @retrofit2.http.Path("year") Integer year, @retrofit2.http.Path("page_num") Integer pageNum, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of Team keys, paginated in groups of 500. (Note, each page will not have 500 teams, but will include the teams within that range of 500.)
   * @param pageNum Page number of results to return, zero-indexed (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("teams/{page_num}/keys")
  Call<List<String>> getTeamsKeys(
    @retrofit2.http.Path("page_num") Integer pageNum, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of short form &#x60;Team_Simple&#x60; objects, paginated in groups of 500.
   * @param pageNum Page number of results to return, zero-indexed (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;TeamSimple&gt;&gt;
   */
  @GET("teams/{page_num}/simple")
  Call<List<TeamSimple>> getTeamsSimple(
    @retrofit2.http.Path("page_num") Integer pageNum, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

}
