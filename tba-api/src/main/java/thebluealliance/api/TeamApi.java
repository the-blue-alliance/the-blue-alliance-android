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
import thebluealliance.api.model.EventSimple;
import thebluealliance.api.model.GetStatus401Response;
import thebluealliance.api.model.GetTeamEventsStatusesByYear200ResponseValue;
import thebluealliance.api.model.History;
import thebluealliance.api.model.Match;
import thebluealliance.api.model.MatchSimple;
import thebluealliance.api.model.Media;
import thebluealliance.api.model.Team;
import thebluealliance.api.model.TeamEventStatus;
import thebluealliance.api.model.TeamRobot;
import thebluealliance.api.model.TeamSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TeamApi {
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
   * @return Call&lt;Map&lt;String, GetTeamEventsStatusesByYear200ResponseValue&gt;&gt;
   */
  @GET("event/{event_key}/teams/statuses")
  Call<Map<String, GetTeamEventsStatusesByYear200ResponseValue>> getEventTeamsStatuses(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a &#x60;Team&#x60; object for the team referenced by the given key.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;Team&gt;
   */
  @GET("team/{team_key}")
  Call<Team> getTeam(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of awards the given team has won.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Award&gt;&gt;
   */
  @GET("team/{team_key}/awards")
  Call<List<Award>> getTeamAwards(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of awards the given team has won in a given year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Award&gt;&gt;
   */
  @GET("team/{team_key}/awards/{year}")
  Call<List<Award>> getTeamAwardsByYear(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
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

  /**
   * 
   * Gets a list of awards the given team won at the given event.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Award&gt;&gt;
   */
  @GET("team/{team_key}/event/{event_key}/awards")
  Call<List<Award>> getTeamEventAwards(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of matches for the given team and event.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Match&gt;&gt;
   */
  @GET("team/{team_key}/event/{event_key}/matches")
  Call<List<Match>> getTeamEventMatches(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of match keys for matches for the given team and event.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("team/{team_key}/event/{event_key}/matches/keys")
  Call<List<String>> getTeamEventMatchesKeys(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form list of matches for the given team and event.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Match&gt;&gt;
   */
  @GET("team/{team_key}/event/{event_key}/matches/simple")
  Call<List<Match>> getTeamEventMatchesSimple(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets the competition rank and status of the team at the given event.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;TeamEventStatus&gt;
   */
  @GET("team/{team_key}/event/{event_key}/status")
  Call<TeamEventStatus> getTeamEventStatus(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of all events this team has competed at.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Event&gt;&gt;
   */
  @GET("team/{team_key}/events")
  Call<List<Event>> getTeamEvents(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of events this team has competed at in the given year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Event&gt;&gt;
   */
  @GET("team/{team_key}/events/{year}")
  Call<List<Event>> getTeamEventsByYear(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of the event keys for events this team has competed at in the given year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("team/{team_key}/events/{year}/keys")
  Call<List<String>> getTeamEventsByYearKeys(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form list of events this team has competed at in the given year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;EventSimple&gt;&gt;
   */
  @GET("team/{team_key}/events/{year}/simple")
  Call<List<EventSimple>> getTeamEventsByYearSimple(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of the event keys for all events this team has competed at.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("team/{team_key}/events/keys")
  Call<List<String>> getTeamEventsKeys(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form list of all events this team has competed at.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;EventSimple&gt;&gt;
   */
  @GET("team/{team_key}/events/simple")
  Call<List<EventSimple>> getTeamEventsSimple(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a key-value list of the event statuses for events this team has competed at in the given year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;Map&lt;String, GetTeamEventsStatusesByYear200ResponseValue&gt;&gt;
   */
  @GET("team/{team_key}/events/{year}/statuses")
  Call<Map<String, GetTeamEventsStatusesByYear200ResponseValue>> getTeamEventsStatusesByYear(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets the history for the team referenced by the given key, including their events and awards.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;History&gt;
   */
  @GET("team/{team_key}/history")
  Call<History> getTeamHistory(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of matches for the given team and year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Match&gt;&gt;
   */
  @GET("team/{team_key}/matches/{year}")
  Call<List<Match>> getTeamMatchesByYear(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of match keys for matches for the given team and year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("team/{team_key}/matches/{year}/keys")
  Call<List<String>> getTeamMatchesByYearKeys(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form list of matches for the given team and year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;MatchSimple&gt;&gt;
   */
  @GET("team/{team_key}/matches/{year}/simple")
  Call<List<MatchSimple>> getTeamMatchesByYearSimple(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of Media (videos / pictures) for the given team and tag.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param mediaTag Media Tag which describes the Media. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Media&gt;&gt;
   */
  @GET("team/{team_key}/media/tag/{media_tag}")
  Call<List<Media>> getTeamMediaByTag(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("media_tag") String mediaTag, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of Media (videos / pictures) for the given team, tag and year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param mediaTag Media Tag which describes the Media. (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Media&gt;&gt;
   */
  @GET("team/{team_key}/media/tag/{media_tag}/{year}")
  Call<List<Media>> getTeamMediaByTagYear(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("media_tag") String mediaTag, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of Media (videos / pictures) for the given team and year.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param year Competition Year (or Season). Must be 4 digits. (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Media&gt;&gt;
   */
  @GET("team/{team_key}/media/{year}")
  Call<List<Media>> getTeamMediaByYear(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of year and robot name pairs for each year that a robot name was provided. Will return an empty array if the team has never named a robot.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;TeamRobot&gt;&gt;
   */
  @GET("team/{team_key}/robots")
  Call<List<TeamRobot>> getTeamRobots(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a &#x60;Team_Simple&#x60; object for the team referenced by the given key.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;TeamSimple&gt;
   */
  @GET("team/{team_key}/simple")
  Call<TeamSimple> getTeamSimple(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of Media (social media) for the given team.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Media&gt;&gt;
   */
  @GET("team/{team_key}/social_media")
  Call<List<Media>> getTeamSocialMedia(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of years in which the team participated in at least one competition.
   * @param teamKey TBA Team Key, eg &#x60;frc254&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Integer&gt;&gt;
   */
  @GET("team/{team_key}/years_participated")
  Call<List<Integer>> getTeamYearsParticipated(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
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
