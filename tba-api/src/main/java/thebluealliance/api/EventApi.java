package thebluealliance.api;

import thebluealliance.api.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import thebluealliance.api.model.Award;
import java.math.BigDecimal;
import thebluealliance.api.model.EliminationAlliance;
import thebluealliance.api.model.Event;
import thebluealliance.api.model.EventDistrictPoints;
import thebluealliance.api.model.EventInsights;
import thebluealliance.api.model.EventOPRs;
import thebluealliance.api.model.EventRanking;
import thebluealliance.api.model.EventSimple;
import thebluealliance.api.model.InlineObject;
import thebluealliance.api.model.Match;
import thebluealliance.api.model.MatchSimple;
import thebluealliance.api.model.Media;
import thebluealliance.api.model.Team;
import thebluealliance.api.model.TeamEventStatus;
import thebluealliance.api.model.TeamSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EventApi {
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
   * Gets an Event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;Event&gt;
   */
  @GET("event/{event_key}")
  Call<Event> getEvent(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Depending on the type of event (district/regional), this will return either district points or regional CMP points
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;EventDistrictPoints&gt;
   */
  @GET("event/{event_key}/advancement_points")
  Call<EventDistrictPoints> getEventAdvancementPoints(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of Elimination Alliances for the given Event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;EliminationAlliance&gt;&gt;
   */
  @GET("event/{event_key}/alliances")
  Call<List<EliminationAlliance>> getEventAlliances(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of awards from the given event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Award&gt;&gt;
   */
  @GET("event/{event_key}/awards")
  Call<List<Award>> getEventAwards(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a set of Event Component OPRs for the given Event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;Map&lt;String, Map&lt;String, BigDecimal&gt;&gt;&gt;
   */
  @GET("event/{event_key}/coprs")
  Call<Map<String, Map<String, BigDecimal>>> getEventCOPRs(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of district points for the Event. These are always calculated, regardless of event type, and may/may not be actually useful.
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
   * Gets a set of Event-specific insights for the given Event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;EventInsights&gt;
   */
  @GET("event/{event_key}/insights")
  Call<EventInsights> getEventInsights(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets an array of Match Keys for the given event key that have timeseries data. Returns an empty array if no matches have timeseries data. *WARNING:* This is *not* official data, and is subject to a significant possibility of error, or missing data. Do not rely on this data for any purpose. In fact, pretend we made it up. *WARNING:* This endpoint and corresponding data models are under *active development* and may change at any time, including in breaking ways.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("event/{event_key}/matches/timeseries")
  Call<List<String>> getEventMatchTimeseries(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of matches for the given event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Match&gt;&gt;
   */
  @GET("event/{event_key}/matches")
  Call<List<Match>> getEventMatches(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of match keys for the given event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;String&gt;&gt;
   */
  @GET("event/{event_key}/matches/keys")
  Call<List<String>> getEventMatchesKeys(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form list of matches for the given event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;MatchSimple&gt;&gt;
   */
  @GET("event/{event_key}/matches/simple")
  Call<List<MatchSimple>> getEventMatchesSimple(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a set of Event OPRs (including OPR, DPR, and CCWM) for the given Event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;EventOPRs&gt;
   */
  @GET("event/{event_key}/oprs")
  Call<EventOPRs> getEventOPRs(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets information on TBA-generated predictions for the given Event. Contains year-specific information. *WARNING* This endpoint is currently under development and may change at any time.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;Object&gt;
   */
  @GET("event/{event_key}/predictions")
  Call<Object> getEventPredictions(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of team rankings for the Event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;EventRanking&gt;
   */
  @GET("event/{event_key}/rankings")
  Call<EventRanking> getEventRankings(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form Event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;EventSimple&gt;
   */
  @GET("event/{event_key}/simple")
  Call<EventSimple> getEventSimple(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a list of media objects that correspond to teams at this event.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Media&gt;&gt;
   */
  @GET("event/{event_key}/team_media")
  Call<List<Media>> getEventTeamMedia(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
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
   * For 2025+ Regional events, this will return points towards the Championship qualification pool.
   * @param eventKey TBA Event Key, eg &#x60;2016nytr&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;EventDistrictPoints&gt;
   */
  @GET("event/{event_key}/regional_champs_pool_points")
  Call<EventDistrictPoints> getRegionalChampsPoolPoints(
    @retrofit2.http.Path("event_key") String eventKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
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
   * @return Call&lt;Map&lt;String, TeamEventStatus&gt;&gt;
   */
  @GET("team/{team_key}/events/{year}/statuses")
  Call<Map<String, TeamEventStatus>> getTeamEventsStatusesByYear(
    @retrofit2.http.Path("team_key") String teamKey, @retrofit2.http.Path("year") Integer year, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

}
