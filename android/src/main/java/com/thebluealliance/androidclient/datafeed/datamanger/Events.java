package com.thebluealliance.androidclient.datafeed.datamanger;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * File created by phil on 6/18/14.
 */
public class Events {
    public static final String ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR = "all_events_loaded_for_year_";
    private static HashMap<Integer, HashMap<String, ArrayList<SimpleEvent>>> eventsByYear = new HashMap<>();

    public static synchronized APIResponse<Event> getEvent(Context c, String key, boolean loadFromCache) throws DataManager.NoDataException {
        final String URL = String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_INFO), key);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true, loadFromCache);
        Event event = JSONManager.getGson().fromJson(response.getData(), Event.class);
        return new APIResponse<>(event, response.getCode());
    }

    public synchronized static APIResponse<ArrayList<SimpleEvent>> getSimpleEventsInWeek(Context c, int year, int week) throws DataManager.NoDataException {
        Log.d("get events for week", "getting for week: " + week);

        APIResponse<HashMap<String, ArrayList<SimpleEvent>>> events = getEventsByYear(c, year);
        String weekLabel = EventHelper.weekLabelFromNum(year, week);

        if (eventsByYear.get(year).containsKey(weekLabel)) {
            return new APIResponse<>(eventsByYear.get(year).get(weekLabel), events.getCode());
        } else {
            //nothing found...
            Log.w(Constants.LOG_TAG, "Unable to find events for tag " + weekLabel);
            return new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

    }

    public synchronized static APIResponse<HashMap<String, ArrayList<SimpleEvent>>> getEventsByYear(Context c, int year) throws DataManager.NoDataException {
        if (eventsByYear.containsKey(year)) {
            return new APIResponse<>(eventsByYear.get(year), APIResponse.CODE.CACHED304);
        } else {
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
                eventListResponse = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_LIST), year), false, false);
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

    public synchronized static APIResponse<ArrayList<SimpleEvent>> getSimpleEventsForTeamInYear(Context c, String teamKey, int year, boolean loadFromCache) throws DataManager.NoDataException {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        APIResponse<Team> response = Teams.getTeam(c, teamKey, year, loadFromCache);
        JsonArray jsonEvents = response.getData().getEvents();
        for (int i = 0; i < jsonEvents.size(); i++) {
            JsonObject currentEvent = jsonEvents.get(i).getAsJsonObject();
            SimpleEvent event = JSONManager.getGson().fromJson(currentEvent, SimpleEvent.class);
            events.add(event);
        }
        return new APIResponse<>(events, response.getCode());
    }

    public static synchronized APIResponse<ArrayList<Team>> getEventTeams(Context c, String eventKey, boolean loadFromCache) throws DataManager.NoDataException {
        ArrayList<Team> teams = new ArrayList<>();
        Log.d("event teams", "Fetching teams for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_TEAMS), eventKey), true, loadFromCache);
        //Log.d("get event teams: ","data: "+response);
        JsonArray teamList = JSONManager.getasJsonArray(response.getData());
        for (JsonElement aTeamList : teamList) {
            teams.add(JSONManager.getGson().fromJson(aTeamList.getAsJsonObject(), Team.class));
        }
        return new APIResponse<>(teams, response.getCode());
    }

    public static synchronized APIResponse<ArrayList<JsonArray>> getEventRankings(Context c, String eventKey, boolean loadFromCache) throws DataManager.NoDataException {
        ArrayList<JsonArray> rankings = new ArrayList<>();
        Log.d("event ranks", "Fetching rankings for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_RANKS), eventKey), true, loadFromCache);
        JsonArray rankList = JSONManager.getasJsonArray(response.getData());
        for (JsonElement aRankList : rankList) {
            rankings.add(aRankList.getAsJsonArray());
        }
        return new APIResponse<>(rankings, response.getCode());
    }

    public static synchronized APIResponse<HashMap<MatchHelper.TYPE, ArrayList<Match>>> getEventResults(Context c, String eventKey, boolean loadFromCache) throws DataManager.NoDataException {
        HashMap<MatchHelper.TYPE, ArrayList<Match>> results = new HashMap<MatchHelper.TYPE, ArrayList<Match>>();
        results.put(MatchHelper.TYPE.QUAL, new ArrayList<Match>());
        results.put(MatchHelper.TYPE.QUARTER, new ArrayList<Match>());
        results.put(MatchHelper.TYPE.SEMI, new ArrayList<Match>());
        results.put(MatchHelper.TYPE.FINAL, new ArrayList<Match>());
        Log.d("event results", "Fetching results for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_MATCHES), eventKey), true, loadFromCache);
        for (JsonElement jsonElement : JSONManager.getasJsonArray(response.getData())) {
            Match match = JSONManager.getGson().fromJson(jsonElement.getAsJsonObject(), Match.class);
            results.get(match.getType()).add(match);
        }
        return new APIResponse<>(results, response.getCode());
    }

    public static synchronized APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey, boolean loadFromCache) throws DataManager.NoDataException {
        return getMatchList(c, eventKey, "", loadFromCache);
    }

    public static synchronized APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey, String teamKey, boolean loadFromCache) throws DataManager.NoDataException {
        ArrayList<Match> results = new ArrayList<>();
        Log.d("match list", "fetching matches for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/matches", true, loadFromCache);
        for (JsonElement jsonElement : JSONManager.getasJsonArray(response.getData())) {
            JsonObject matchObject = jsonElement.getAsJsonObject();
            if (matchObject.get(MatchDeserializer.ALLIANCE_TAG).toString().contains(teamKey + "\"")) {
                //if team key is empty, it'll be contained so we add all matches. Perfect.
                Match match = JSONManager.getGson().fromJson(matchObject, Match.class);
                results.add(match);
            }
        }
        Collections.sort(results, new MatchSortByPlayOrderComparator());
        return new APIResponse<>(results, response.getCode());
    }

    public synchronized static APIResponse<JsonObject> getEventStats(Context c, String eventKey, boolean loadFromCache) throws DataManager.NoDataException {
        return getEventStats(c, eventKey, "", loadFromCache);
    }

    public synchronized static APIResponse<JsonObject> getEventStats(Context c, String eventKey, String teamKey, boolean loadFromCache) throws DataManager.NoDataException {
        APIResponse<String> results = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/stats", true, loadFromCache);
        JsonObject allStats = JSONManager.getasJsonObject(results.getData());
        if (teamKey.isEmpty()) {
            return new APIResponse<>(allStats, results.getCode());
        } else {
            JsonObject teamStats = new JsonObject();
            String teamNumber = teamKey.substring(3);
            if (allStats.has("oprs")) {
                JsonObject oprs = allStats.get("oprs").getAsJsonObject();
                if (oprs.has(teamNumber)) {
                    teamStats.addProperty("opr", oprs.get(teamNumber).getAsDouble());
                }
            }
            if (allStats.has("dprs")) {
                JsonObject oprs = allStats.get("dprs").getAsJsonObject();
                if (oprs.has(teamNumber)) {
                    teamStats.addProperty("dpr", oprs.get(teamNumber).getAsDouble());
                }
            }
            if (allStats.has("ccwms")) {
                JsonObject oprs = allStats.get("ccwms").getAsJsonObject();
                if (oprs.has(teamNumber)) {
                    teamStats.addProperty("ccwm", oprs.get(teamNumber).getAsDouble());
                }
            }
            return new APIResponse<>(teamStats, results.getCode());
        }
    }

    public synchronized static APIResponse<ArrayList<Award>> getEventAwards(Context c, String eventKey, String teamKey, boolean loadFromCache) throws DataManager.NoDataException {
        ArrayList<Award> awards = new ArrayList<>();
        Log.d("event awards", "Fetching awards for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/awards", true, loadFromCache);
        for (JsonElement jsonElement : JSONManager.getasJsonArray(response.getData())) {
            Award award = JSONManager.getGson().fromJson(jsonElement.getAsJsonObject(), Award.class);
            if (award.getWinners().toString().contains(teamKey.isEmpty() ? "" : teamKey.substring(3) + ",")) {
                awards.add(award);
            }
        }
        return new APIResponse<>(awards, response.getCode());
    }

    public synchronized static APIResponse<ArrayList<Award>> getEventAwards(Context c, String eventKey, boolean loadFromCache) throws DataManager.NoDataException {
        return getEventAwards(c, eventKey, "", loadFromCache);
    }
}
