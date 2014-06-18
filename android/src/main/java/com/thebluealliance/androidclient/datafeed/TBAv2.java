package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.HashMap;


public class TBAv2 {

    public static enum QUERY {
        CSV_TEAMS,
        TEAM, //TODO modify appropriately whenever teams get their own endpoint
        TEAM_LIST,
        TEAM_MEDIA,
        EVENT_LIST,
        EVENT_INFO,
        EVENT_TEAMS,
        EVENT_MATCHES,
        EVENT_STATS,
        EVENT_RANKS,
        EVENT_AWARDS
    }

    public static final HashMap<QUERY, String> API_URL;

    static {
        API_URL = new HashMap<>();
        API_URL.put(QUERY.CSV_TEAMS, "http://www.thebluealliance.com/api/csv/teams/all?X-TBA-App-Id=" + Constants.getApiHeader());
        API_URL.put(QUERY.TEAM, "http://thebluealliance.com/api/v2/team/%s");
        API_URL.put(QUERY.TEAM_LIST, "http://thebluealliance.com/api/v2/teams/%s");
        API_URL.put(QUERY.EVENT_INFO, "http://thebluealliance.com/api/v2/event/%s");
        API_URL.put(QUERY.EVENT_TEAMS, "http://thebluealliance.com/api/v2/event/%s/teams");
        API_URL.put(QUERY.EVENT_RANKS, "http://thebluealliance.com/api/v2/event/%s/rankings");
        API_URL.put(QUERY.EVENT_MATCHES, "http://thebluealliance.com/api/v2/event/%s/matches");
        API_URL.put(QUERY.EVENT_STATS, "http://thebluealliance.com/api/v2/event/%s/stats");
        API_URL.put(QUERY.EVENT_AWARDS, "http://thebluealliance.com/api/v2/event/%s/awards");
        API_URL.put(QUERY.EVENT_LIST, "http://thebluealliance.com/api/v2/events/%d");
        API_URL.put(QUERY.TEAM_MEDIA, "http://thebluealliance.com/api/v2/team/%s/%d/media");
    }

    public static ArrayList<SimpleEvent> getEventList(String json) {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        JsonArray data = JSONManager.getasJsonArray(json);
        for (JsonElement aData : data) {
            events.add(JSONManager.getGson().fromJson(aData, SimpleEvent.class));
        }
        return events;
    }

    public static ArrayList<SimpleTeam> getTeamList(String json) {
        ArrayList<SimpleTeam> teams = new ArrayList<>();
        JsonArray data = JSONManager.getasJsonArray(json);
        for (JsonElement aData : data) {
            teams.add(JSONManager.getGson().fromJson(aData, SimpleTeam.class));
        }
        return teams;
    }

    /**
     * This is the main datafeed method - you speciy a URL and this will either return it from our cache or fetch and store it.
     * @param c App context
     * @param URL URL to fetch
     * @param cacheInDatabase boolean - do we want to store the response locally if we need to load it from the web?
     * @param forceFromCache (optional, defaults to FALSE). If set, the data exists locally, we won't query the web ever - just return what we have.
     * @return An APIRespnse containing the data we fetched from the internet
     * @throws DataManager.NoDataException
     */
    public static APIResponse<String> getResponseFromURLOrThrow(Context c, final String URL, boolean cacheInDatabase, boolean forceFromCache) throws DataManager.NoDataException {
        if (c == null) {
            Log.d("datamanager", "Error: null context");
            throw new DataManager.NoDataException("Unexpected problem retrieving data");
        }
        Log.d("datamanager", "Loading URL: " + URL);
        Database db = Database.getInstance(c);
        boolean existsInDb = db.responseExists(URL);
        boolean connectedToInternet = ConnectionDetector.isConnectedToInternet(c);
        if (existsInDb) {
            if (connectedToInternet) {
                // We are connected to the internet and have a record in the database.
                //query the API - if it returns 304-Not-Modified, then query the database
                //and return our local content

                APIResponse<String> cachedData = db.getResponse(URL);

                //we want whatever's in the cache. Forget everything else...
                if(forceFromCache){
                    Log.d("datamanager", "Online; force reading from cache");
                    return cachedData;
                }

                HttpResponse cachedResponse = HTTP.getResponse(URL,cachedData.getLastUpdate());
                //if we get a 200-OK back, then we need to cache that new data
                //otherwise, it's a 304-Not-Modified
                boolean dataRequiresUpdate = (cachedResponse != null) && (cachedResponse.getStatusLine().getStatusCode() == 200);

                if (dataRequiresUpdate) {
                    // Load team data, cache it in the database, return it to caller
                    String response = HTTP.dataFromResponse(cachedResponse),
                            lastUpdate = "";
                    Header lastModified = cachedResponse.getFirstHeader("Last-Modified");
                    if(lastModified != null){
                        lastUpdate = lastModified.getValue();
                    }
                    if (cacheInDatabase) {
                        db.updateResponse(URL, response, lastUpdate);
                    }
                    Log.d("datamanager", "Online; updated from internet");
                    return new APIResponse<>(response, APIResponse.CODE.UPDATED);
                } else {
                    Log.d("datamanager", "Online; no update required, loaded from database");
                    return cachedData.updateCode(APIResponse.CODE.CACHED304);
                }
            } else {
                Log.d("datamanager", "Offline; loaded from database");
                return db.getResponse(URL).updateCode(APIResponse.CODE.OFFLINECACHE);
            }
        } else {
            if (connectedToInternet) {
                // Load team data, cache it in the database, return it to caller
                HttpResponse cachedResponse = HTTP.getResponse(URL);
                String response = HTTP.dataFromResponse(cachedResponse),
                        lastUpdate = "";
                Header lastModified = cachedResponse.getFirstHeader("Last-Modified");
                if(lastModified != null){
                    lastUpdate = lastModified.getValue();
                }

                if (cacheInDatabase) {
                    db.storeResponse(URL, response, lastUpdate);
                }
                Log.d("datamanager", "Online; loaded from internet");
                return new APIResponse<>(response, APIResponse.CODE.WEBLOAD);
            } else {
                // There is no locally stored data and we are not connected to the internet.
                Log.d("datamanager", "Offline; no data!");
                throw new DataManager.NoDataException("There is no internet connection and the response is not cached!");
            }
        }
    }
}
