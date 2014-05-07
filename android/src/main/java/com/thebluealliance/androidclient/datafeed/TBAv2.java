package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Iterator;


public class TBAv2 {
    public static SimpleTeam getSimpleTeam(String key) {
        String data = HTTP.GET("http://thebluealliance.com/api/v2/team/" + key);
        return JSONManager.getGson().fromJson(data, SimpleTeam.class);
    }

    public static Team getTeam(String key) {
        String data = HTTP.GET("http://thebluealliance.com/api/v2/team/" + key);
        return JSONManager.getGson().fromJson(data, Team.class);
    }

    public static SimpleEvent getSimpleEvent(String key) {
        String data = HTTP.GET("http://thebluealliance.com/api/v2/event/" + key);
        return JSONManager.getGson().fromJson(data, SimpleEvent.class);
    }

    public static Event getEvent(String key) {
        Log.d(Constants.LOG_TAG,"Loading data for "+key);
        JsonObject data = JSONManager.getasJsonObject(HTTP.GET("http://thebluealliance.com/api/v2/event/" + key));


        //data.add("matches", JSONManager.getasJsonArray(HTTP.GET("http://thebluealliance.com/api/v2/event/" + key + "/matches")));
        //data.add("stats", JSONManager.getasJsonObject(HTTP.GET("http://thebluealliance.com/api/v2/event/" + key + "/stats")));
        /*
         * NOT YET IMPLEMENTED:
		 * data.add("rankings", JSONManager.getasJsonArray(HTTP.GET("http://thebluealliance.com/api/v2/event/"+key+"/rankings")));
		   data.add("webcasts", JSONManager.getasJsonObject(HTTP.GET("http://thebluealliance.com/api/v2/event/"+key+"/webcasts")));
		 */
        return JSONManager.getGson().fromJson(data, Event.class);
    }

    public static ArrayList<Team> getEventTeams(String eventKey){
        ArrayList<Team> teams = new ArrayList<>();
        JsonArray teamList = JSONManager.getasJsonArray(HTTP.GET("http://thebluealliance.com/api/v2/event/" + eventKey + "/teams"));
        Iterator iterator = teamList.iterator();
        while(iterator.hasNext()){
            teams.add(JSONManager.getGson().fromJson((JsonObject)iterator.next(),Team.class));
        }
        return teams;
    }

    public static ArrayList<SimpleEvent> getEventList(int year){
        ArrayList<SimpleEvent> events = new ArrayList<>();
        JsonArray data = JSONManager.getasJsonArray(HTTP.GET("http://thebluealliance.com/api/v2/events/" +year));
        Iterator iterator = data.iterator();
        while(iterator.hasNext()){
            events.add(JSONManager.getGson().fromJson((JsonObject)(iterator.next()),SimpleEvent.class));
        }
        return events;
    }

    public static String getResponseFromURLOrThrow(Context c, final String URL, boolean cacheInDatabase) throws DataManager.NoDataException {
        Database db = Database.getInstance(c);
        boolean existsInDb = db.exists(URL);
        boolean connectedToInternet = ConnectionDetector.isConnectedToInternet(c);
        if (existsInDb) {
            if (connectedToInternet) {
                // We are connected to the internet and have a record in the database.
                // Check if the local copy is up-to-date; if it is, return it.
                // Otherwise, requery the API, cache the new data, and return the data.
                // TODO: once we support the If-Modified-Since header, use that to check if our local copy is up-to-date.
                // For now, we just load the new data every time.

                Log.d("datamanager", "Online; loaded from database");
                return db.getResponse(URL);
            } else {
                Log.d("datamanager", "Offline; loaded from database");
                return db.getResponse(URL);
            }
        } else {
            if (connectedToInternet) {
                // Load team data, cache it in the database, return it to caller
                String response = HTTP.GET(URL);
                if (cacheInDatabase) {
                    db.storeResponse(URL, response, -1);
                }
                Log.d("datamanager", "Online; loaded from internet");
                return response;
            } else {
                // There is no locally stored data and we are not connected to the internet.
                Log.d("datamanager", "Offline; no data!");
                throw new DataManager.NoDataException("There is no internet connection and no local cache for this team!");
            }
        }
    }
}
