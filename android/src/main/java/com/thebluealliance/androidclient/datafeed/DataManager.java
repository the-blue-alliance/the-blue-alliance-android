package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.listitems.APIResponse;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Nathan on 4/30/2014.
 */
public class DataManager {

    public static final String ALL_TEAMS_LOADED_TO_DATABASE = "all_teams_loaded",
            ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR = "all_events_loaded_for_year_";

    private static HashMap<Integer, HashMap<String, ArrayList<SimpleEvent>>> eventsByYear = new HashMap<>();

    public synchronized static APIResponse<Team> getTeam(Context c, String teamKey) throws NoDataException {
        final String URL = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM), teamKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true);
        Team team = JSONManager.getGson().fromJson(response.getData(), Team.class);

        return new APIResponse<>(team, response.getCode());
    }

    public synchronized static APIResponse<Team> getTeam(Context c, String teamKey, int year) throws NoDataException {
        final String URL = "http://thebluealliance.com/api/v2/team/" + teamKey + "/" + year;
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true);
        Team team = JSONManager.getGson().fromJson(response.getData(), Team.class);

        return new APIResponse<>(team, response.getCode());
    }

    public synchronized static APIResponse<ArrayList<SimpleEvent>> getSimpleEventsForTeamInYear(Context c, String teamKey, int year) throws NoDataException {
        ArrayList<SimpleEvent> events = new ArrayList<>();
        APIResponse<Team> response = getTeam(c, teamKey, year);
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
            final String URL = TBAv2.API_URL.get(TBAv2.QUERY.CSV_TEAMS);
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
        final String URL = String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_INFO), key);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true);
        Event event = JSONManager.getGson().fromJson(response.getData(), Event.class);
        return new APIResponse<>(event, response.getCode());
    }

    public static synchronized APIResponse<ArrayList<Team>> getEventTeams(Context c, String eventKey) throws NoDataException {
        ArrayList<Team> teams = new ArrayList<>();
        Log.d("event teams", "Fetching teams for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_TEAMS), eventKey), true);
        //Log.d("get event teams: ","data: "+response);
        JsonArray teamList = JSONManager.getasJsonArray(response.getData());
        for (JsonElement aTeamList : teamList) {
            teams.add(JSONManager.getGson().fromJson(aTeamList.getAsJsonObject(), Team.class));
        }
        return new APIResponse<>(teams, response.getCode());
    }

    public static synchronized APIResponse<ArrayList<JsonArray>> getEventRankings(Context c, String eventKey) throws NoDataException {
        ArrayList<JsonArray> rankings = new ArrayList<>();
        Log.d("event ranks", "Fetching rankings for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_RANKS), eventKey), true);
        JsonArray rankList = JSONManager.getasJsonArray(response.getData());
        for (JsonElement aRankList : rankList) {
            rankings.add(aRankList.getAsJsonArray());
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
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_MATCHES), eventKey), true);
        for (JsonElement jsonElement : JSONManager.getasJsonArray(response.getData())) {
            Match match = JSONManager.getGson().fromJson(jsonElement.getAsJsonObject(), Match.class);
            results.get(match.getType()).add(match);
        }
        return new APIResponse<>(results, response.getCode());
    }

    public static synchronized APIResponse<Integer> getRankForTeamAtEvent(Context c, String teamKey, String eventKey) throws NoDataException {
        APIResponse<ArrayList<JsonArray>> allRankings = getEventRankings(c, eventKey);
        String teamNumber = teamKey.substring(3);

        ArrayList<JsonArray> data = allRankings.getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).get(1).getAsString().equals(teamNumber)) {
                return new APIResponse<>(i, allRankings.getCode());
            }
        }
        return new APIResponse<>(-1, allRankings.getCode());
    }

    public static synchronized APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey) throws NoDataException {
        return getMatchList(c, eventKey, "");
    }

    public static synchronized APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey, String teamKey) throws NoDataException {
        ArrayList<Match> results = new ArrayList<>();
        Log.d("match list", "fetching matches for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/matches", true);
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

    public synchronized static APIResponse<JsonObject> getEventStats(Context c, String eventKey) throws NoDataException {
        return getEventStats(c, eventKey, "");
    }

    public synchronized static APIResponse<JsonObject> getEventStats(Context c, String eventKey, String teamKey) throws NoDataException {
        APIResponse<String> results = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/stats", true);
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

    public synchronized static APIResponse<ArrayList<Award>> getEventAwards(Context c, String eventKey, String teamKey) throws NoDataException {
        ArrayList<Award> awards = new ArrayList<>();
        Log.d("event awards", "Fetching awards for " + eventKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/event/" + eventKey + "/awards", true);
        for (JsonElement jsonElement : JSONManager.getasJsonArray(response.getData())) {
            Award award = JSONManager.getGson().fromJson(jsonElement.getAsJsonObject(), Award.class);
            if (award.getWinners().toString().contains(teamKey.isEmpty() ? "" : teamKey.substring(3) + ",")) {
                awards.add(award);
            }
        }
        return new APIResponse<>(awards, response.getCode());
    }

    public synchronized static APIResponse<ArrayList<Award>> getEventAwards(Context c, String eventKey) throws NoDataException {
        return getEventAwards(c, eventKey, "");
    }

    public synchronized static APIResponse<ArrayList<SimpleEvent>> getSimpleEventsInWeek(Context c, int year, int week) throws NoDataException {
        Log.d("get events for week", "getting for week: " + week);

        APIResponse<HashMap<String, ArrayList<SimpleEvent>>> events = getEventsByYear(c, year);
        String weekLabel = Event.weekLabelFromNum(year, week);

        if (eventsByYear.get(year).containsKey(weekLabel)) {
            return new APIResponse<>(eventsByYear.get(year).get(weekLabel), events.getCode());
        } else {
            //nothing found...
            Log.w(Constants.LOG_TAG, "Unable to find events for tag " + weekLabel);
            return new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

    }

    public synchronized static APIResponse<HashMap<String, ArrayList<SimpleEvent>>> getEventsByYear(Context c, int year) throws NoDataException {
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
                eventListResponse = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_LIST), year), false);
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

    public synchronized static APIResponse<ArrayList<Media>> getTeamMedia(Context c, String teamKey, int year) throws NoDataException {
        ArrayList<Media> output = new ArrayList<>();
        String apiUrl = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM_MEDIA), teamKey, year);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, apiUrl, true);
        JsonArray mediaArray = JSONManager.getasJsonArray(response.getData());
        for (JsonElement media : mediaArray) {
            output.add(JSONManager.getGson().fromJson(media, Media.class));
        }
        return new APIResponse<>(output, response.getCode());
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