package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class TBAv2 {

    public static enum QUERY {
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
        EVENT_AWARDS
    }

    public static final HashMap<QUERY, String> API_URL;

    static {
        API_URL = new HashMap<>();
        API_URL.put(QUERY.CSV_TEAMS, "http://www.thebluealliance.com/api/csv/teams/all?X-TBA-App-Id=" + Constants.getApiHeader());
        API_URL.put(QUERY.TEAM_LIST, "http://www.thebluealliance.com/api/v2/teams/%s");

        API_URL.put(QUERY.TEAM, "http://www.thebluealliance.com/api/v2/team/%s");
        API_URL.put(QUERY.TEAM_YEAR, "http://www.thebluealliance.com/api/v2/team/%s/%d");
        API_URL.put(QUERY.TEAM_EVENTS, "http://www.thebluealliance.com/api/v2/team/%s/%d/events");
        API_URL.put(QUERY.TEAM_EVENT_AWARDS, "http://www.thebluealliance.com/api/v2/team/%s/event/%s/awards");
        API_URL.put(QUERY.TEAM_EVENT_MATCHES, "http://www.thebluealliance.com/api/v2/team/%s/event/%s/matches");
        API_URL.put(QUERY.TEAM_YEARS_PARTICIPATED, "http://www.thebluealliance.com/api/v2/team/%s/years_participated");
        API_URL.put(QUERY.TEAM_MEDIA, "http://www.thebluealliance.com/api/v2/team/%s/%d/media");

        API_URL.put(QUERY.EVENT_INFO, "http://www.thebluealliance.com/api/v2/event/%s");
        API_URL.put(QUERY.EVENT_TEAMS, "http://www.thebluealliance.com/api/v2/event/%s/teams");
        API_URL.put(QUERY.EVENT_RANKS, "http://www.thebluealliance.com/api/v2/event/%s/rankings");
        API_URL.put(QUERY.EVENT_MATCHES, "http://www.thebluealliance.com/api/v2/event/%s/matches");
        API_URL.put(QUERY.EVENT_STATS, "http://www.thebluealliance.com/api/v2/event/%s/stats");
        API_URL.put(QUERY.EVENT_AWARDS, "http://www.thebluealliance.com/api/v2/event/%s/awards");
        API_URL.put(QUERY.EVENT_LIST, "http://www.thebluealliance.com/api/v2/events/%d");
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

    public static APIResponse<String> getResponseFromURLOrThrow(Context c, final String URL, boolean forceFromCache) throws DataManager.NoDataException {
        return getResponseFromURLOrThrow(c, URL, true, forceFromCache);
    }

    /**
     * This is the main datafeed method - you speciy a URL and this will either return it from our cache or fetch and store it.
     *
     * @param c               App context
     * @param URL             URL to fetch
     * @param forceFromCache  (optional, defaults to FALSE). If set, the data exists locally, we won't query the web ever - just return what we have.
     * @return An APIRespnse containing the data we fetched from the internet
     * @throws DataManager.NoDataException
     */
    public static APIResponse<String> getResponseFromURLOrThrow(Context c, final String URL, boolean cacheLocally, boolean forceFromCache) throws DataManager.NoDataException {
        if (c == null) {
            Log.d(Constants.DATAMANAGER_LOG, "Error: null context");
            throw new DataManager.NoDataException("Unexpected problem retrieving data");
        }
        boolean existsInDb;
        existsInDb = Database.getInstance(c).getResponseTable().responseExists(URL);

        boolean connectedToInternet = ConnectionDetector.isConnectedToInternet(c);
        if (existsInDb) {
            if (connectedToInternet) {
                // We are connected to the internet and have a record in the database.
                //query the API - if it returns 304-Not-Modified, then query the database
                //and return our local content

                APIResponse<String> cachedData;
                cachedData = Database.getInstance(c).getResponseTable().getResponse(URL);

                Date now = new Date();
                Date futureTime = new Date(cachedData.lastHit.getTime() + Constants.API_HIT_TIMEOUT);
                if (now.before(futureTime)) {
                    //if isn't hasn't been longer than the timeout (1 minute now)
                    //just return what we have in cache
                    return cachedData.updateCode(APIResponse.CODE.CACHED304);
                }

                //we want whatever's in the cache. Forget everything else...
                if (forceFromCache) {
                    return cachedData;
                }

                HttpResponse cachedResponse = HTTP.getResponse(URL, cachedData.getLastUpdate());
                //if we get a 200-OK back, then we need to cache that new data
                //otherwise, it's a 304-Not-Modified
                boolean dataRequiresUpdate = (cachedResponse != null) && (cachedResponse.getStatusLine().getStatusCode() == 200);

                if (dataRequiresUpdate) {
                    // Load team data, cache it in the database, return it to caller
                    String response = HTTP.dataFromResponse(cachedResponse),
                            lastUpdate = "";
                    Header lastModified = cachedResponse.getFirstHeader("Last-Modified");
                    if (lastModified != null) {
                        lastUpdate = lastModified.getValue();
                    }

                    Database.getInstance(c).getResponseTable().updateResponse(URL, lastUpdate);

                    Log.d(Constants.DATAMANAGER_LOG, "Online; data updated from internet: " + URL);
                    return new APIResponse<>(response, APIResponse.CODE.UPDATED);
                } else {
                    Database.getInstance(c).getResponseTable().touchResponse(URL);

                    return cachedData.updateCode(APIResponse.CODE.CACHED304);
                }
            } else {
                Log.d(Constants.DATAMANAGER_LOG, "Offline; can't check API. Reading data locally: " + URL);
                return Database.getInstance(c).getResponseTable().getResponse(URL).updateCode(APIResponse.CODE.OFFLINECACHE);
            }
        } else {
            if (connectedToInternet) {
                // Load team data, cache it in the database, return it to caller
                HttpResponse cachedResponse = HTTP.getResponse(URL);
                String response = HTTP.dataFromResponse(cachedResponse),
                        lastUpdate = "";
                Header lastModified = cachedResponse.getFirstHeader("Last-Modified");
                if (lastModified != null) {
                    lastUpdate = lastModified.getValue();
                }

                if(cacheLocally) {
                    Database.getInstance(c).getResponseTable().storeResponse(URL, lastUpdate);
                }

                Log.d(Constants.DATAMANAGER_LOG, "Online; data loaded from internet: " + URL);
                return new APIResponse<>(response, APIResponse.CODE.WEBLOAD);
            } else {
                // There is no locally stored data and we are not connected to the internet.
                Log.d(Constants.DATAMANAGER_LOG, "Offline; can't load from internet and nothing exists locally: " + URL);
                throw new DataManager.NoDataException("There is no internet connection and the response is not cached!");
            }
        }
    }
}
