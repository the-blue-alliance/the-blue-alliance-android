package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.models.Award;
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
            ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR = "all_events_loaded_for_year_";

    private static HashMap<Integer, HashMap<String, ArrayList<SimpleEvent>>> eventsByYear = new HashMap<>();

    public synchronized static APIResponse<Team> getTeam(Context c, String teamKey) throws NoDataException {
        final String URL = "http://thebluealliance.com/api/v2/team/" + teamKey;
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true);
        Team team = JSONManager.getGson().fromJson(response.getData(), Team.class);

        return new APIResponse<>(team, response.getCode());
    }

    public synchronized static APIResponse<ArrayList<SimpleEvent>> getSimpleEventsForTeamInYear(Context c, String teamKey, int year) throws NoDataException {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        // This will throw an exception if there is no local data and no internet connection
        // We want this to propagate up the stack
        APIResponse<Team> response = getTeam(c, teamKey);
        JsonArray jsonEvents = response.getData().getEvents();
        for (int i = 0; i < jsonEvents.size(); i++) {
            JsonObject currentEvent = jsonEvents.get(i).getAsJsonObject();
            SimpleEvent event = JSONManager.getGson().fromJson(currentEvent, SimpleEvent.class);
            events.add(event);
        }
        return new APIResponse<>(events, response.getCode());
    }

    public synchronized static APIResponse<ArrayList<SimpleTeam>> getSimpleTeamsInRange(Context c, int lowerBound, int upperBound) throws NoDataException {
        Log.d("get simple teams", "getting teams in range " + lowerBound + " - " + upperBound);
        ArrayList<SimpleTeam> teams = new ArrayList<>();
        //TODO move to PreferenceHandler class
        boolean allTeamsLoaded = PreferenceManager.getDefaultSharedPreferences(c).getBoolean(ALL_TEAMS_LOADED_TO_DATABASE, false);
        // TODO check for updated data from the API and update response accordingly
        APIResponse<String> response;
        if (allTeamsLoaded) {
            teams = Database.getInstance(c).getTeamsInRange(lowerBound, upperBound);
            response = new APIResponse<>("", ConnectionDetector.isConnectedToInternet(c) ? APIResponse.CODE.CACHED304 : APIResponse.CODE.OFFLINECACHE);
        } else {
            // We need to load teams from the API
            //TODO move to TBAv2 class
            final String URL = "http://www.thebluealliance.com/api/csv/teams/all?X-TBA-App-Id=" + Constants.getApiHeader();
            response = TBAv2.getResponseFromURLOrThrow(c, URL, false);
            Log.d("get simple teams", "starting parse");
            teams = CSVManager.parseTeamsFromCSV(response.getData());
            Log.d("get simple teams", "ending parse");
            Log.d("get simple teams", "starting insert");
            Database.getInstance(c).storeTeams(teams);
            Log.d("get simple teams", "ending insert");
            teams = Database.getInstance(c).getTeamsInRange(lowerBound, upperBound);
            if (response.getCode() != APIResponse.CODE.NODATA) {
                //only update preference if actual data was loaded
                PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_TEAMS_LOADED_TO_DATABASE, true).commit();
            }
        }

        return new APIResponse<>(teams, response.getCode());
    }

    public static synchronized APIResponse<Event> getEvent(Context c, String key) throws NoDataException {
        final String URL = "http://thebluealliance.com/api/v2/event/" + key;
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true);
        Event event = JSONManager.getGson().fromJson(response.getData(), Event.class);
        return new APIResponse<>(event, response.getCode());
    }

    public static synchronized APIResponse<ArrayList<Team>> getEventTeams(Context c, String eventKey) throws NoDataException {
        ArrayList<Team> teams = new ArrayList<>();
        Log.d("event teams", "Fetching teams for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/teams", true);
        //Log.d("get event teams: ","data: "+response);
        JsonArray teamList = JSONManager.getasJsonArray(response.getData());
        Iterator<JsonElement> iterator = teamList.iterator();
        while (iterator.hasNext()) {
            teams.add(JSONManager.getGson().fromJson(iterator.next().getAsJsonObject(), Team.class));
        }
        return new APIResponse<>(teams, response.getCode());
    }

    public static synchronized APIResponse<ArrayList<JsonArray>> getEventRankings(Context c, String eventKey) throws NoDataException {
        ArrayList<JsonArray> rankings = new ArrayList<>();
        Log.d("event ranks", "Fetching rankings for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/rankings", true);
        JsonArray rankList = JSONManager.getasJsonArray(response.getData());
        Iterator<JsonElement> iterator = rankList.iterator();
        while (iterator.hasNext()) {
            rankings.add(iterator.next().getAsJsonArray());
        }
        return new APIResponse<>(rankings, response.getCode());
    }

    public static synchronized APIResponse<HashMap<Match.TYPE, ArrayList<Match>>> getEventResults(Context c, String eventKey) throws NoDataException {
        HashMap<Match.TYPE, ArrayList<Match>> results = new HashMap<Match.TYPE, ArrayList<Match>>();
        results.put(Match.TYPE.QUAL, new ArrayList<Match>());
        results.put(Match.TYPE.QUARTER, new ArrayList<Match>());
        results.put(Match.TYPE.SEMI, new ArrayList<Match>());
        results.put(Match.TYPE.FINAL, new ArrayList<Match>());
        Log.d("event results", "Fetching results for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/matches", true);
        Iterator<JsonElement> iterator = JSONManager.getasJsonArray(response.getData()).iterator();
        while (iterator.hasNext()) {
            Match match = JSONManager.getGson().fromJson(iterator.next().getAsJsonObject(), Match.class);
            results.get(match.getType()).add(match);
        }
        return new APIResponse<>(results, response.getCode());
    }

    public static synchronized APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey) throws NoDataException {
        ArrayList<Match> results = new ArrayList<>();
        Log.d("match list", "fetching matches for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/matches", true);
        Iterator<JsonElement> iterator = JSONManager.getasJsonArray(response.getData()).iterator();
        while (iterator.hasNext()) {
            Match match = JSONManager.getGson().fromJson(iterator.next().getAsJsonObject(), Match.class);
            results.add(match);
        }
        return new APIResponse<>(results, response.getCode());
    }

    public synchronized static APIResponse<JsonObject> getEventStats(Context c, String eventKey) throws NoDataException {
        APIResponse<String> results = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/stats", true);
        return new APIResponse<>(JSONManager.getasJsonObject(results.getData()), results.getCode());
    }

    public synchronized static APIResponse<ArrayList<Award>> getEventAwards(Context c, String eventKey) throws NoDataException {
        ArrayList<Award> awards = new ArrayList<>();
        Log.d("event awards", "Fetching awards for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/awards", true);
        ;
        Iterator<JsonElement> iterator = JSONManager.getasJsonArray(response.getData()).iterator();
        while (iterator.hasNext()) {
            Award award = JSONManager.getGson().fromJson(iterator.next().getAsJsonObject(), Award.class);
            awards.add(award);
        }
        return new APIResponse<>(awards, response.getCode());
    }

    public synchronized static APIResponse<ArrayList<SimpleEvent>> getSimpleEventsInWeek(Context c, int year, int week) throws NoDataException {
        Log.d("get events for week", "getting for week: " + week);

        APIResponse<HashMap<String, ArrayList<SimpleEvent>>> events = getEventsByYear(c, year);
        String weekLabel = Event.weekLabelFromNum(events.getData(), week);

        if(eventsByYear.get(year).containsKey(weekLabel)){
            return new APIResponse<>(eventsByYear.get(year).get(weekLabel), events.getCode());
        }else{
            //nothing found...
            Log.w(Constants.LOG_TAG, "Unable to find events for tag "+ weekLabel);
            return new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

    }

    public synchronized static APIResponse<HashMap<String, ArrayList<SimpleEvent>>> getEventsByYear(Context c, int year) throws NoDataException {
        if(eventsByYear.containsKey(year)){
            return new APIResponse<>(eventsByYear.get(year), APIResponse.CODE.CACHED304);
        }else {
            ArrayList<SimpleEvent> events = new ArrayList<>();
            boolean allEventsLoaded = PreferenceManager.getDefaultSharedPreferences(c).getBoolean(ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR + year, false);
            HashMap<String, ArrayList<SimpleEvent>> groupedEvents;
            APIResponse<String> eventListResponse;
            //TODO check for updates and update response accordingly
            if (allEventsLoaded) {
                Log.d("get events for week", "loading from db");
                events = Database.getInstance(c).getEventsInYear(year);
                eventListResponse = new APIResponse<>("", ConnectionDetector.isConnectedToInternet(c) ? APIResponse.CODE.CACHED304 : APIResponse.CODE.OFFLINECACHE);
                groupedEvents = SimpleEvent.groupByWeek(events);
            } else {
                eventListResponse = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/events/" + year, false);
                Log.d(Constants.LOG_TAG, "Response: " + eventListResponse.getData().toString());
                events = TBAv2.getEventList(eventListResponse.getData());
                Database.getInstance(c).storeEvents(events);
                groupedEvents = SimpleEvent.groupByWeek(events);
                if (eventListResponse.getCode() != APIResponse.CODE.NODATA) {
                    PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR + year, true).commit();
                }
            }
            eventsByYear.put(year, groupedEvents);
            return new APIResponse<>(groupedEvents, eventListResponse.getCode());
        }
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