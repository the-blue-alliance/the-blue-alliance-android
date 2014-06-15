package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;

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
        EVENT_AWARDS;
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

    public static APIResponse<String> getResponseFromURLOrThrow(Context c, final String URL, boolean cacheInDatabase) throws DataManager.NoDataException {
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
                // Check if the local copy is up-to-date; if it is, return it.
                // Otherwise, requery the API, cache the new data, and return the data.
                // TODO: once we support the If-Modified-Since header, use that to check if our local copy is up-to-date.
                // For now, we just load the new data every time.
                boolean dataRequiresUpdate = false;
                if (dataRequiresUpdate) {
                    // Load team data, cache it in the database, return it to caller
                    String response = HTTP.GET(URL);
                    if (cacheInDatabase) {
                        db.storeResponse(URL, response, -1);
                    }
                    Log.d("datamanager", "Online; updated from internet");
                    return new APIResponse<String>(response, APIResponse.CODE.UPDATED);
                } else {
                    Log.d("datamanager", "Online; no update required, loaded from database");
                    return new APIResponse<String>(db.getResponse(URL), APIResponse.CODE.CACHED304);
                }
            } else {
                Log.d("datamanager", "Offline; loaded from database");
                return new APIResponse<String>(db.getResponse(URL), APIResponse.CODE.OFFLINECACHE);
            }
        } else {
            if (connectedToInternet) {
                // Load team data, cache it in the database, return it to caller
                String response = HTTP.GET(URL);
                if (cacheInDatabase) {
                    db.storeResponse(URL, response, -1);
                }
                Log.d("datamanager", "Online; loaded from internet");
                return new APIResponse<String>(response, APIResponse.CODE.WEBLOAD);
            } else {
                // There is no locally stored data and we are not connected to the internet.
                Log.d("datamanager", "Offline; no data!");
                throw new DataManager.NoDataException("There is no internet connection and the response is not cached!");
            }
        }
    }
}
