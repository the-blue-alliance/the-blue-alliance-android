package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Nathan on 4/30/2014.
 */
public class DataManager {

    private static final String ALL_TEAMS_LOADED_TO_DATABASE = "all_teams_loaded",
                                ALL_EVENTS_LOADED_TO_DATABASE = "all_events_loaded";

    public synchronized static Team getTeam(Context c, String teamKey) throws NoDataException {
        final String URL = "http://thebluealliance.com/api/v2/team/" + teamKey;
        String response = TBAv2.getResponseFromURLOrThrow(c, URL, true);
        Team team = JSONManager.getGson().fromJson(response, Team.class);
        System.out.println("events: " + team.getEvents().toString());
        return team;
    }

    public synchronized static ArrayList<SimpleEvent> getSimpleEventsForTeamInYear(Context c, String teamKey, int year) throws NoDataException {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        // This will throw an exception if there is no local data and no internet connection
        // We want this to propagate up the stack
        Team team = getTeam(c, teamKey);
        JsonArray jsonEvents = team.getEvents();
        for (int i = 0; i < jsonEvents.size(); i++) {
            JsonObject currentEvent = jsonEvents.get(i).getAsJsonObject();
            SimpleEvent event = JSONManager.getGson().fromJson(currentEvent, SimpleEvent.class);
            events.add(event);
        }
        return events;
    }

    public synchronized static ArrayList<SimpleTeam> getSimpleTeamsInRange(Context c, int lowerBound, int upperBound) throws NoDataException {
        Log.d("get simple teams", "getting teams in range " + lowerBound + " - " + upperBound);
        ArrayList<SimpleTeam> teams = new ArrayList<>();
        //TODO move to PreferenceHandler class
        boolean allTeamsLoaded = PreferenceManager.getDefaultSharedPreferences(c).getBoolean(ALL_TEAMS_LOADED_TO_DATABASE, false);
        // TODO check for updated data from the API
        if (allTeamsLoaded) {
            teams = Database.getInstance(c).getTeamsInRange(lowerBound, upperBound);
        } else {
            // We need to load teams from the API
            //TODO move to TBAv2 class
            final String URL = "http://www.thebluealliance.com/api/csv/teams/all?X-TBA-App-Id=greg:marra:hi";
            String response = TBAv2.getResponseFromURLOrThrow(c, URL, false);
            Log.d("get simple teams", "starting parse");
            teams = CSVManager.parseTeamsFromCSV(response);
            Log.d("get simple teams", "ending parse");
            Log.d("get simple teams", "starting insert");
            Database.getInstance(c).storeTeams(teams);
            Log.d("get simple teams", "ending insert");
            teams = Database.getInstance(c).getTeamsInRange(lowerBound, upperBound);
            PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_TEAMS_LOADED_TO_DATABASE, true).commit();
        }

        return teams;
    }

    public static synchronized Event getEvent(Context c, String key) throws NoDataException{
        final String URL = "http://thebluealliance.com/api/v2/event/" + key;
        String response = TBAv2.getResponseFromURLOrThrow(c, URL, true);
        Event event = JSONManager.getGson().fromJson(response, Event.class);
        return event;
    }

    public static synchronized ArrayList<Team> getEventTeams(Context c, String eventKey) throws NoDataException{
        ArrayList<Team> teams = new ArrayList<>();
        Log.d("event teams","Fetching teams for "+eventKey);
        String response = TBAv2.getResponseFromURLOrThrow(c,"http://thebluealliance.com/api/v2/event/" + eventKey + "/teams", true);
        //Log.d("get event teams: ","data: "+response);
        JsonArray teamList = JSONManager.getasJsonArray(response);
        Iterator<JsonElement> iterator = teamList.iterator();
        while(iterator.hasNext()){
            teams.add(JSONManager.getGson().fromJson(iterator.next().getAsJsonObject(),Team.class));
        }
        return teams;
    }

    public static synchronized ArrayList<JsonArray> getEventRankings(Context c, String eventKey) throws NoDataException{
        ArrayList<JsonArray> rankings = new ArrayList<>();
        Log.d("event ranks","Fetching rankings for "+eventKey);
        String response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/rankings", true);
        JsonArray rankList = JSONManager.getasJsonArray(response);
        Iterator<JsonElement> iterator = rankList.iterator();
        while(iterator.hasNext()){
            rankings.add(iterator.next().getAsJsonArray());
        }
        return rankings;
    }

    public static synchronized HashMap<Match.TYPE,ArrayList<Match>> getEventResults(Context c, String eventKey) throws NoDataException{
        HashMap<Match.TYPE,ArrayList<Match>> results = new HashMap<Match.TYPE,ArrayList<Match>>();
        results.put(Match.TYPE.QUAL, new ArrayList<Match>());
        results.put(Match.TYPE.QUARTER, new ArrayList<Match>());
        results.put(Match.TYPE.SEMI, new ArrayList<Match>());
        results.put(Match.TYPE.FINAL, new ArrayList<Match>());
        Log.d("event results", "Fetching results for "+eventKey);
        String response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/matches",true);
        Iterator<JsonElement> iterator = JSONManager.getasJsonArray(response).iterator();
        while(iterator.hasNext()){
            Match match = JSONManager.getGson().fromJson(iterator.next().getAsJsonObject(), Match.class);
            results.get(match.getType()).add(match);
        }
        return results;
    }

    public synchronized static JsonObject getEventStats(Context c, String eventKey) throws NoDataException{
        String results = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/stats", true);
        return JSONManager.getasJsonObject(results);
    }

    public synchronized static ArrayList<SimpleEvent> getSimpleEventsInWeek(Context c, int year, int week) throws NoDataException{
        Log.d("get events for week","getting for week: "+week);
        ArrayList<SimpleEvent> events = new ArrayList<>();
        boolean allEventsLoaded = PreferenceManager.getDefaultSharedPreferences(c).getBoolean(ALL_EVENTS_LOADED_TO_DATABASE, false);
        //TODO check for updates
        if(allEventsLoaded){
            Log.d("get events for week","loading from db");
            events = Database.getInstance(c).getEventsInWeek(year,week);
        }else{
            Log.d("get events for week","loading from interwebs");
            events = TBAv2.getEventList(year);
            Database.getInstance(c).storeEvents(events);
            Log.d("get events for week","stored to db");
            // ^ stores all events, now refetch for just the week we want.
            events = Database.getInstance(c).getEventsInWeek(year,week);
            PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_EVENTS_LOADED_TO_DATABASE, true).commit();
        }
        return events;
    }

    public static class NoDataException extends Exception {
        public NoDataException(String message) {
            super(message);
        }

        public NoDataException(String message, Throwable t) {
            super(message, t);
        }
    }


}
