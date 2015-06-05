package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.squareup.okhttp.Response;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class LegacyAPIHelper {

    private static final String TBA_HOST_PREF = "tba_host";
    private static final String tbaHostDefault = "http://www.thebluealliance.com";

    public enum QUERY {
        CSV_TEAMS,
        TEAM_LIST,
        TEAM,
        TEAM_YEAR,
        TEAM_EVENTS,
        TEAM_EVENT_AWARDS,
        TEAM_EVENT_MATCHES,
        TEAM_YEARS_PARTICIPATED,
        TEAM_MEDIA,
        EVENT_LIST,
        EVENT_INFO,
        EVENT_TEAMS,
        EVENT_MATCHES,
        EVENT_MATCHES_FOR_TEAM,
        EVENT_STATS,
        EVENT_RANKS,
        EVENT_AWARDS,
        EVENT_DISTRICT_POINTS,
        DISTRICT_LIST,
        DISTRICT_EVENTS,
        DISTRICT_RANKINGS
    }

    private static final HashMap<QUERY, String> API_URL;

    static {
        API_URL = new HashMap<>();
        API_URL.put(QUERY.CSV_TEAMS, "/api/csv/teams/all?X-TBA-App-Id=" + Constants.getApiHeader());
        API_URL.put(QUERY.TEAM_LIST, "/api/v2/teams/%s");

        API_URL.put(QUERY.TEAM, "/api/v2/team/%s");
        API_URL.put(QUERY.TEAM_YEAR, "/api/v2/team/%s/%d");
        API_URL.put(QUERY.TEAM_EVENTS, "/api/v2/team/%s/%d/events");
        API_URL.put(QUERY.TEAM_EVENT_AWARDS, "/api/v2/team/%s/event/%s/awards");
        API_URL.put(QUERY.TEAM_EVENT_MATCHES, "/api/v2/team/%s/event/%s/matches");
        API_URL.put(QUERY.TEAM_YEARS_PARTICIPATED, "/api/v2/team/%s/years_participated");
        API_URL.put(QUERY.TEAM_MEDIA, "/api/v2/team/%s/%d/media");

        API_URL.put(QUERY.EVENT_INFO, "/api/v2/event/%s");
        API_URL.put(QUERY.EVENT_TEAMS, "/api/v2/event/%s/teams");
        API_URL.put(QUERY.EVENT_RANKS, "/api/v2/event/%s/rankings");
        API_URL.put(QUERY.EVENT_MATCHES, "/api/v2/event/%s/matches");
        API_URL.put(QUERY.EVENT_STATS, "/api/v2/event/%s/stats");
        API_URL.put(QUERY.EVENT_AWARDS, "/api/v2/event/%s/awards");
        API_URL.put(QUERY.EVENT_LIST, "/api/v2/events/%d");
        API_URL.put(QUERY.EVENT_DISTRICT_POINTS, "/api/v2/event/%s/district_points");

        API_URL.put(QUERY.DISTRICT_LIST, "/api/v2/districts/%d");
        API_URL.put(QUERY.DISTRICT_EVENTS, "/api/v2/district/%s/%d/events");
        API_URL.put(QUERY.DISTRICT_RANKINGS, "/api/v2/district/%s/%d/rankings");
    }

    public static String getTBAApiUrl(Context c, QUERY query) {
        String host = tbaHostDefault;
        if (c != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            if (prefs != null) {
                host = prefs.getString(TBA_HOST_PREF, tbaHostDefault);
                if (!Utilities.isDebuggable() || host.isEmpty()) {
                    host = tbaHostDefault;
                }
            }
        }
        return host + API_URL.get(query);
    }

    public static ArrayList<Event> getEventList(String json) {
        ArrayList<Event> events = new ArrayList<>();
        JsonArray data = JSONManager.getasJsonArray(json);
        for (JsonElement aData : data) {
            events.add(JSONManager.getGson().fromJson(aData, Event.class));
        }
        return events;
    }

    public static ArrayList<Team> getTeamList(String json) {
        ArrayList<Team> teams = new ArrayList<>();
        JsonArray data = JSONManager.getasJsonArray(json);
        for (JsonElement aData : data) {
            teams.add(JSONManager.getGson().fromJson(aData, Team.class));
        }
        return teams;
    }

    public static ArrayList<District> getDistrictList(String json, String url, int version) {
        JsonArray data = JSONManager.getasJsonArray(json);
        return DistrictHelper.buildVersionedDistrictList(data, url, version);
    }

    /**
     * This is the method that, when given an API url, checks the database to see when it was last
     * updated (if event), and tells its calling query what action to take. If the url has not yet
     * been queried or is in need of update, then download (or update) the data from the internet
     * and record the Last-Modified header for future use. This method also implements a rate limit
     * on how often you can hit a single API endpoint, which is defined in
     * Constants.API_HIT_TIMEOUT
     * <p>
     * About the return values. The 'data' field of the resulting APIResponse is only going to have
     * actual data if we needed to load something from the internet (thus, the accompanying CODE
     * will either be Code.WEBLOAD or Code.UPDATED). If the CODE is anything else, then the 'data'
     * field will be null - so check the code before assuming it's set.
     *
     * @param c      Calling context - used to query the database for the Last-Update time for a
     *               URL
     * @param URL    API URL to check and see if an update is required
     * @param params Parameters associated with this request cacheLocally: Option to save the fact
     *               that we hit this URL in the database. Setting this parameter to TRUE allows us
     *               to use If-Modified-Since headers, reducing overhead forceFromCache: When this
     *               parameter is true, we won't make any web requests and just return Code.LOCAL,
     *               telling the caller to use whatever it has cached locally forceFromWeb: When
     *               this parameter is true, the API timeout is greatly reduced so we can force (not
     *               exactly, but close enough) a web refresh. The timeout still exists for the poor
     *               server's sake
     * @return APIResponse containing the data we fetched (if necessary) and the response code for
     * how we obtained that data.
     * @throws DataManager.NoDataException
     */
    public static APIResponse<String> getResponseFromURLOrThrow(Context c, final String URL, RequestParams params) throws DataManager.NoDataException {
        if (c == null) {
            Log.d(Constants.DATAMANAGER_LOG, "Error: null context");
            throw new DataManager.NoDataException("Unexpected problem retrieving data");
        }

        /* Check if we have hit (and recorded) this URL in the past and if we are connected to the intertubes */
        boolean existsInDb = Database.getInstance(c).getResponseTable().responseExists(URL);
        boolean connectedToInternet = ConnectionDetector.isConnectedToInternet(c);

        if (existsInDb) {
            if (connectedToInternet) {
                /* We are connected to the internet and have a record in the database.
                 * Now, query the API - if it returns 304-Not-Modified, tell the caller
                 * to return local content from the database, as the remote content is unchanged
                 */

                APIResponse<String> cachedData;
                cachedData = Database.getInstance(c).getResponseTable().getResponseIfExists(URL);   /* this will always return an empty string and Code.LOCAL for the data.
                                                                                             * we just care about the updated times for the query.
                                                                                             * getLastUpdate() will return the Last-Modified header from the
                                                                                             * last time we queried this endpoint (which will subsequently be passed
                                                                                             * as If-Modified-Since for this request) and lastHit will be the system
                                                                                             * time when we last queried this endpoint.
                                                                                             *
                                                                                             * The Last-Modified time is set by the TBA server and used for all web requests
                                                                                             * The local times are used for determining if we're within the limit for how often we can query one endpoint
                                                                                             */

                /* First, check if we're within the API timeout. If so, just tell the caller to return what data we have */
                Date now = new Date();
                long timeout = params.forceFromWeb ? Constants.API_HIT_TIMEOUT_LONG : Constants.API_HIT_TIMEOUT_SHORT;
                Date futureTime = new Date(cachedData.lastHit.getTime() + timeout);
                if (now.before(futureTime) && !params.forceFromWeb) {
                    //if isn't hasn't been longer than the timeout (1 minute now)
                    //just return what we have in cache
                    return cachedData.updateCode(APIResponse.CODE.CACHED304); /* Send Code.CACHED304 to tell the caller
                                                                               * that remote content is unchanged.
                                                                               * The 'data' field is null
                                                                               */
                }

                /* If we don't want to query the API at all, then tell the caller to return from cache */
                if (params.forceFromCache) {
                    return cachedData; /* This will have Code.LOCAL and null data */
                }

                /* Now, we can make a web request. Query the API, passing the previous Last-Modified as our current If-Modified-Since */
                Response cachedResponse = HTTP.getRequest(URL, params.forceFromWeb ? null : cachedData.getLastUpdate());

                if (cachedResponse != null) {

                    int responseStatus = cachedResponse.code();

                    /**
                     * If we get 4xx Client Error or 5xx Server back as a code, return a response with an empty string as data
                     * and with the response contents (e.g. "404 Not Found") as the error message.
                     * This will have the code APIResponse.ERROR
                     */
                    if (responseStatus / 100 == 4 || responseStatus / 100 == 5) {
                        String responseData = HTTP.dataFromResponse(cachedResponse);
                        Log.e(Constants.DATAMANAGER_LOG, "Error: HTTP " + responseStatus + "\n " + responseData + " from updating " + URL);
                        return new APIResponse<>("", responseData);
                    }

                    /* If we get a 200-OK back from the server, then we need to return that new data
                     * Otherwise, we are going to assume the code is 304-Not-Modified
                     * There is a possibility of other codes, but we can add those in (along with proper error handling) here later
                     */
                    boolean dataRequiresUpdate = (responseStatus == 200);

                    if (dataRequiresUpdate) {
                    /* If we get 200-OK back, read the data from the request
                     * Also, if the server gives a Last-Modified time back, record it and add it to the database for future use
                     */
                        String response = HTTP.dataFromResponse(cachedResponse),
                                lastUpdate = cachedResponse.header("Last-Modified", "");

                        int apiVersion;
                        String versionHeader = cachedResponse.header("X-TBA-Version", "");
                        if (!versionHeader.isEmpty()) {
                            apiVersion = Integer.parseInt(versionHeader);
                        } else {
                            apiVersion = 0;
                        }

                        Database.getInstance(c).getResponseTable().updateResponse(URL, lastUpdate);

                        Log.d(Constants.DATAMANAGER_LOG, "Online; data updated from internet v" + apiVersion + ": " + URL);
                        return new APIResponse<>(response, APIResponse.CODE.UPDATED, apiVersion); /* This response will contain the data that we fetched */
                    } else {
                    /* The data does not require an update (we got a 304-Not-Modified back), so simply
                     * Update the lastHit time in the database to make sure the timeout stays active
                     */
                        Database.getInstance(c).getResponseTable().touchResponse(URL);

                        return cachedData.updateCode(APIResponse.CODE.CACHED304);
                    }
                } else {
                    Log.e(Constants.DATAMANAGER_LOG, "Unable to update data from the web");
                    return new APIResponse<>(null, APIResponse.CODE.NODATA);
                }
            } else {
                Log.d(Constants.DATAMANAGER_LOG, "Offline; can't check API. " + URL);
                return Database.getInstance(c).getResponseTable().getResponseIfExists(URL).updateCode(APIResponse.CODE.OFFLINECACHE);
            }
        } else {
            if (connectedToInternet) {
                /* We haven't hit this response before - it doesn't exist in the database
                 * But we do have the ability to fetch it from the web.
                 */
                Response webResponse = HTTP.getRequest(URL);
                if (webResponse != null) {

                    int responseStatus = webResponse.code();

                    /**
                     * If we get 4xx Client Error or 5xx Server back as a code, return a response with an empty string as data
                     * and with the response contents (e.g. "404 Not Found") as the error message.
                     * This will have the code APIResponse.ERROR
                     */
                    if (responseStatus / 100 == 4 || responseStatus / 100 == 5) {
                        String responseData = HTTP.dataFromResponse(webResponse);
                        Log.e(Constants.DATAMANAGER_LOG, "Error: HTTP " + responseStatus + "\n " + responseData + " from fetching " + URL);
                        return new APIResponse<>("", responseData);
                    }

                    String response = HTTP.dataFromResponse(webResponse),
                            lastUpdate = webResponse.header("Last-Modified", "");

                    if (params.cacheLocally) {
                        Database.getInstance(c).getResponseTable().storeResponse(URL, lastUpdate);
                    }

                    int apiVersion;
                    String versionHeader = webResponse.header("X-TBA-Version", "");
                    if (!versionHeader.isEmpty()) {
                        apiVersion = Integer.parseInt(versionHeader);
                    } else {
                        apiVersion = 0;
                    }

                    Log.d(Constants.DATAMANAGER_LOG, "Online; data loaded from internet v" + apiVersion + ": " + URL);
                    return new APIResponse<>(response, APIResponse.CODE.WEBLOAD, apiVersion); /* This response will contain the loaded data */
                } else {
                    Log.e(Constants.DATAMANAGER_LOG, "Unable to load data from the web");
                    return new APIResponse<>(null, APIResponse.CODE.NODATA);
                }
            } else {
                /* There is no locally stored data and we are not connected to the internet.
                 * Can't do anything...
                 */
                Log.d(Constants.DATAMANAGER_LOG, "Offline; can't load from internet and nothing exists locally: " + URL);
                throw new DataManager.NoDataException("There is no internet connection and the response is not cached!");
            }
        }
    }
}
