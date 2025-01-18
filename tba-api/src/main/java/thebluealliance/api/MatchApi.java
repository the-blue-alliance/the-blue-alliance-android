package thebluealliance.api;

import thebluealliance.api.client.CollectionFormats.*;

import retrofit2.Call;
import retrofit2.http.*;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.MultipartBody;

import thebluealliance.api.model.GetStatus401Response;
import thebluealliance.api.model.Match;
import thebluealliance.api.model.MatchSimple;
import thebluealliance.api.model.Zebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MatchApi {
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
   * Gets a &#x60;Match&#x60; object for the given match key.
   * @param matchKey TBA Match Key, eg &#x60;2016nytr_qm1&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;Match&gt;
   */
  @GET("match/{match_key}")
  Call<Match> getMatch(
    @retrofit2.http.Path("match_key") String matchKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets a short-form &#x60;Match&#x60; object for the given match key.
   * @param matchKey TBA Match Key, eg &#x60;2016nytr_qm1&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;MatchSimple&gt;
   */
  @GET("match/{match_key}/simple")
  Call<MatchSimple> getMatchSimple(
    @retrofit2.http.Path("match_key") String matchKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets an array of game-specific Match Timeseries objects for the given match key or an empty array if not available. *WARNING:* This is *not* official data, and is subject to a significant possibility of error, or missing data. Do not rely on this data for any purpose. In fact, pretend we made it up. *WARNING:* This endpoint and corresponding data models are under *active development* and may change at any time, including in breaking ways.
   * @param matchKey TBA Match Key, eg &#x60;2016nytr_qm1&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;List&lt;Object&gt;&gt;
   */
  @GET("match/{match_key}/timeseries")
  Call<List<Object>> getMatchTimeseries(
    @retrofit2.http.Path("match_key") String matchKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
  );

  /**
   * 
   * Gets Zebra MotionWorks data for a Match for the given match key.
   * @param matchKey TBA Match Key, eg &#x60;2016nytr_qm1&#x60; (required)
   * @param ifNoneMatch Value of the &#x60;ETag&#x60; header in the most recently cached response by the client. (optional)
   * @return Call&lt;Zebra&gt;
   */
  @GET("match/{match_key}/zebra_motionworks")
  Call<Zebra> getMatchZebra(
    @retrofit2.http.Path("match_key") String matchKey, @retrofit2.http.Header("If-None-Match") String ifNoneMatch
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

}
