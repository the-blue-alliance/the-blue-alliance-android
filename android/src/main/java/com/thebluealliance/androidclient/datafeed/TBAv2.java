package com.thebluealliance.androidclient.datafeed;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
        JsonObject data = JSONManager.getasJsonObject(HTTP.GET("http://thebluealliance.com/api/v2/event/" + key));
        data.add("matches", JSONManager.getasJsonArray(HTTP.GET("http://thebluealliance.com/api/v2/event/" + key + "/matches")));
        data.add("stats", JSONManager.getasJsonObject(HTTP.GET("http://thebluealliance.com/api/v2/event/" + key + "/stats")));
        data.add("teams", JSONManager.getasJsonObject(HTTP.GET("http://thebluealliance.com/api/v2/event/" + key + "/teams")));
        /*
         * NOT YET IMPLEMENTED:
		 * data.add("rankings", JSONManager.getasJsonArray(HTTP.GET("http://thebluealliance.com/api/v2/event/"+key+"/rankings")));
		   data.add("webcasts", JSONManager.getasJsonObject(HTTP.GET("http://thebluealliance.com/api/v2/event/"+key+"/webcasts")));
		 */
        return JSONManager.getGson().fromJson(data, Event.class);
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
}
