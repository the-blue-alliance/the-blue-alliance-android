package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.comparators.MatchSortByDisplayOrderComparator;
import com.thebluealliance.androidclient.datafeed.deserializers.MatchDeserializer;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Nathan on 4/30/2014.
 */
public class DataManager {

    public static class NoDataException extends Exception {
        public NoDataException(String message) {
            super(message);
        }

        public NoDataException(String message, Throwable t) {
            super(message, t);
        }
    }

    public static class Teams {
        public static final String ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE = "all_teams_loaded_for_page_";

        public static APIResponse<Team> getTeam(Context c, String teamKey, boolean loadFromCache) throws NoDataException {
            final String URL = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM), teamKey);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true, loadFromCache);
            Team team = JSONManager.getGson().fromJson(response.getData(), Team.class);

            return new APIResponse<>(team, response.getCode());
        }

        public static APIResponse<Team> getTeam(Context c, String teamKey, int year, boolean loadFromCache) throws NoDataException {
            final String URL = "http://www.thebluealliance.com/api/v2/team/" + teamKey + "/" + year;
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true, loadFromCache);
            Team team = JSONManager.getGson().fromJson(response.getData(), Team.class);

            return new APIResponse<>(team, response.getCode());
        }

        public static APIResponse<Cursor> getCursorForSimpleTeamsInRange(Context c, int lowerBound, int upperBound) throws NoDataException {
            ArrayList<Integer> requiredPageNums = new ArrayList<>();
            for (int pageNum = lowerBound / Constants.API_TEAM_LIST_PAGE_SIZE; pageNum <= upperBound / Constants.API_TEAM_LIST_PAGE_SIZE; pageNum++) {
                requiredPageNums.add(pageNum);
            }
            Log.d("get cursor for simple teams", "getting cursor for teams in range: " + lowerBound + " - " + upperBound + ". requires pages: " + requiredPageNums.toString());

            ArrayList<APIResponse.CODE> teamListResponseCodes = new ArrayList<>();
            Cursor cursor = null;
            for (Integer requiredPageNum : requiredPageNums) {
                int pageNum = requiredPageNum;

                //TODO move to PreferenceHandler class
                boolean allTeamsLoadedForPage = PreferenceManager.getDefaultSharedPreferences(c).getBoolean(ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE + pageNum, false);
                // TODO check for updated data from the API and update response accordingly
                if (allTeamsLoadedForPage) {
                    teamListResponseCodes.add(ConnectionDetector.isConnectedToInternet(c) ? APIResponse.CODE.CACHED304 : APIResponse.CODE.OFFLINECACHE);
                } else {
                    // We need to load teams from the API
                    final String URL = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM_LIST), pageNum);
                    APIResponse<String> teamListResponse = TBAv2.getResponseFromURLOrThrow(c, URL, false, false);
                    teamListResponseCodes.add(teamListResponse.getCode());

                    ArrayList<Team> teams = TBAv2.getTeamList(teamListResponse.getData());
                    Database.getInstance(c).getTeamsTable().storeTeams(teams);
                    if (teamListResponse.getCode() != APIResponse.CODE.NODATA) {
                        //only update preference if actual data was loaded
                        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE + pageNum, true).commit();
                    }
                }
            }
            cursor = Database.getInstance(c).getTeamsTable().getCursorForTeamsInRange(lowerBound, upperBound);
            APIResponse.CODE[] a = new APIResponse.CODE[teamListResponseCodes.size()];

            return new APIResponse<>(cursor, APIResponse.mergeCodes(teamListResponseCodes.toArray(a)));
        }

        public static APIResponse<ArrayList<String>> getYearsParticipated(Context c, String teamKey, boolean loadFromCache) throws NoDataException {
            ArrayList<String> output = new ArrayList<>();
            String apiUrl = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM_YEARS_PARTICIPATED), teamKey);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, apiUrl, true, loadFromCache);
            JsonArray yearsArray = JSONManager.getasJsonArray(response.getData());
            for (JsonElement year : yearsArray) {
                output.add(year.getAsString());
            }
            return new APIResponse<>(output, response.getCode());
        }

        public static APIResponse<ArrayList<Event>> getEventsForTeam(Context c, String teamKey, int year, boolean loadFromCache) throws NoDataException {
            ArrayList<Event> output = new ArrayList<>();
            String apiUrl = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM_EVENTS), teamKey, year);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, apiUrl, true, loadFromCache);
            JsonArray eventsArray = JSONManager.getasJsonArray(response.getData());
            for (JsonElement event : eventsArray) {
                output.add(JSONManager.getGson().fromJson(event, Event.class));
            }
            return new APIResponse<>(output, response.getCode());
        }

        public static APIResponse<ArrayList<Match>> getMatchesForTeamAtEvent(Context c, String teamKey, String eventKey, boolean loadFromCache) throws NoDataException {
            ArrayList<Match> output = new ArrayList<>();
            String apiUrl = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM_EVENT_MATCHES), teamKey, eventKey);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, apiUrl, true, loadFromCache);
            JsonArray matchesArray = JSONManager.getasJsonArray(response.getData());
            for (JsonElement match : matchesArray) {
                output.add(JSONManager.getGson().fromJson(match, Match.class));
            }
            Collections.sort(output, new MatchSortByDisplayOrderComparator());
            return new APIResponse<>(output, response.getCode());
        }

        public static APIResponse<ArrayList<Award>> getAwardsForTeamAtEvent(Context c, String teamKey, String eventKey, boolean loadFromCache) throws NoDataException {
            ArrayList<Award> output = new ArrayList<>();
            String apiUrl = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM_EVENT_AWARDS), teamKey, eventKey);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, apiUrl, true, loadFromCache);
            JsonArray awardsArray = JSONManager.getasJsonArray(response.getData());
            for (JsonElement award : awardsArray) {
                output.add(JSONManager.getGson().fromJson(award, Award.class));
            }
            return new APIResponse<>(output, response.getCode());
        }

        public static APIResponse<ArrayList<Media>> getTeamMedia(Context c, String teamKey, int year, boolean loadFromCache) throws NoDataException {
            ArrayList<Media> output = new ArrayList<>();
            String apiUrl = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM_MEDIA), teamKey, year);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, apiUrl, true, loadFromCache);
            JsonArray mediaArray = JSONManager.getasJsonArray(response.getData());
            for (JsonElement media : mediaArray) {
                output.add(JSONManager.getGson().fromJson(media, Media.class));
            }
            return new APIResponse<>(output, response.getCode());
        }

        public static APIResponse<Integer> getRankForTeamAtEvent(Context c, String teamKey, String eventKey, boolean loadFromCache) throws NoDataException {
            APIResponse<ArrayList<JsonArray>> allRankings = Events.getEventRankings(c, eventKey, loadFromCache);
            String teamNumber = teamKey.substring(3);

            ArrayList<JsonArray> data = allRankings.getData();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).get(1).getAsString().equals(teamNumber)) {
                    return new APIResponse<>(i, allRankings.getCode());
                }
            }
            return new APIResponse<>(-1, allRankings.getCode());
        }
    }

    public static class Events {
        public static final String ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR = "all_events_loaded_for_year_";
        private static HashMap<Integer, HashMap<String, ArrayList<Event>>> eventsByYear = new HashMap<>();

        public static APIResponse<Event> getEvent(Context c, String key, boolean loadFromCache) throws NoDataException {
            final String URL = String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_INFO), key);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true, loadFromCache);
            Event event = JSONManager.getGson().fromJson(response.getData(), Event.class);
            return new APIResponse<>(event, response.getCode());
        }

        public static APIResponse<ArrayList<Event>> getSimpleEventsInWeek(Context c, int year, int week) throws NoDataException {
            Log.d("get events for week", "getting for week: " + week);

            APIResponse<HashMap<String, ArrayList<Event>>> events = getEventsByYear(c, year);
            String weekLabel = EventHelper.weekLabelFromNum(year, week);

            if (eventsByYear.get(year).containsKey(weekLabel)) {
                return new APIResponse<>(eventsByYear.get(year).get(weekLabel), events.getCode());
            } else {
                //nothing found...
                Log.w(Constants.LOG_TAG, "Unable to find events for tag " + weekLabel);
                return new APIResponse<>(null, APIResponse.CODE.NODATA);
            }

        }

        public static APIResponse<HashMap<String, ArrayList<Event>>> getEventsByYear(Context c, int year) throws NoDataException {
            if (eventsByYear.containsKey(year)) {
                return new APIResponse<>(eventsByYear.get(year), APIResponse.CODE.CACHED304);
            } else {
                ArrayList<Event> events = new ArrayList<>();
                boolean allEventsLoaded = PreferenceManager.getDefaultSharedPreferences(c).getBoolean(ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR + year, false);
                HashMap<String, ArrayList<Event>> groupedEvents;
                APIResponse<String> eventListResponse;
                //TODO check for updates and update response accordingly
                if (allEventsLoaded) {
                    Log.d("get events for week", "loading from db");
                    events = Database.getInstance(c).getEventsTable().getInYear(year);
                    eventListResponse = new APIResponse<>("", ConnectionDetector.isConnectedToInternet(c) ? APIResponse.CODE.CACHED304 : APIResponse.CODE.OFFLINECACHE);
                    groupedEvents = EventHelper.groupByWeek(events);
                } else {
                    eventListResponse = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_LIST), year), false, false);
                    events = TBAv2.getEventList(eventListResponse.getData());
                    Database.getInstance(c).getEventsTable().storeEvents(events);
                    groupedEvents = EventHelper.groupByWeek(events);
                    if (eventListResponse.getCode() != APIResponse.CODE.NODATA) {
                        PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR + year, true).commit();
                    }
                }
                eventsByYear.put(year, groupedEvents);
                return new APIResponse<>(groupedEvents, eventListResponse.getCode());
            }
        }

        public static APIResponse<ArrayList<Team>> getEventTeams(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
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

        public static APIResponse<ArrayList<JsonArray>> getEventRankings(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
            ArrayList<JsonArray> rankings = new ArrayList<>();
            Log.d("event ranks", "Fetching rankings for " + eventKey);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_RANKS), eventKey), true, loadFromCache);
            JsonArray rankList = JSONManager.getasJsonArray(response.getData());
            for (JsonElement aRankList : rankList) {
                rankings.add(aRankList.getAsJsonArray());
            }
            return new APIResponse<>(rankings, response.getCode());
        }

        public static APIResponse<HashMap<MatchHelper.TYPE, ArrayList<Match>>> getEventResults(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
            HashMap<MatchHelper.TYPE, ArrayList<Match>> results = new HashMap<MatchHelper.TYPE, ArrayList<Match>>();
            results.put(MatchHelper.TYPE.QUAL, new ArrayList<Match>());
            results.put(MatchHelper.TYPE.QUARTER, new ArrayList<Match>());
            results.put(MatchHelper.TYPE.SEMI, new ArrayList<Match>());
            results.put(MatchHelper.TYPE.FINAL, new ArrayList<Match>());
            Log.d("event results", "Fetching results for " + eventKey);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, String.format(TBAv2.API_URL.get(TBAv2.QUERY.EVENT_MATCHES), eventKey), true, loadFromCache);
            for (JsonElement jsonElement : JSONManager.getasJsonArray(response.getData())) {
                Match match = JSONManager.getGson().fromJson(jsonElement.getAsJsonObject(), Match.class);
                try {
                    results.get(match.getType()).add(match);
                } catch (BasicModel.FieldNotDefinedException e) {
                    throw new NoDataException(e.getMessage());
                }
            }
            return new APIResponse<>(results, response.getCode());
        }

        public static APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
            return getMatchList(c, eventKey, "", loadFromCache);
        }

        public static APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey, String teamKey, boolean loadFromCache) throws NoDataException {
            ArrayList<Match> results = new ArrayList<>();
            Log.d("match list", "fetching matches for " + eventKey);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://www.thebluealliance.com/api/v2/event/" + eventKey + "/matches", true, loadFromCache);
            for (JsonElement jsonElement : JSONManager.getasJsonArray(response.getData())) {
                JsonObject matchObject = jsonElement.getAsJsonObject();
                if (matchObject.get(MatchDeserializer.ALLIANCE_TAG).toString().contains(teamKey + "\"")) {
                    //if team key is empty, it'll be contained so we add all matches. Perfect.
                    Match match = JSONManager.getGson().fromJson(matchObject, Match.class);
                    results.add(match);
                }
            }
            Collections.sort(results, new MatchSortByDisplayOrderComparator());
            return new APIResponse<>(results, response.getCode());
        }

        public static APIResponse<JsonObject> getEventStats(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
            return getEventStats(c, eventKey, "", loadFromCache);
        }

        public static APIResponse<JsonObject> getEventStats(Context c, String eventKey, String teamKey, boolean loadFromCache) throws NoDataException {
            APIResponse<String> results = TBAv2.getResponseFromURLOrThrow(c, "http://www.thebluealliance.com/api/v2/event/" + eventKey + "/stats", true, loadFromCache);
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

        public static APIResponse<ArrayList<Award>> getEventAwards(Context c, String eventKey, String teamKey, boolean loadFromCache) throws NoDataException {
            ArrayList<Award> awards = new ArrayList<>();
            Log.d("event awards", "Fetching awards for " + eventKey);
            APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, "http://www.thebluealliance.com/api/v2/event/" + eventKey + "/awards", true, loadFromCache);
            for (JsonElement jsonElement : JSONManager.getasJsonArray(response.getData())) {
                Award award = JSONManager.getGson().fromJson(jsonElement.getAsJsonObject(), Award.class);
                try {
                    if (award.getWinners().toString().contains(teamKey.isEmpty() ? "" : teamKey.substring(3) + ",")) {
                        awards.add(award);
                    }
                } catch (BasicModel.FieldNotDefinedException e) {
                    throw new NoDataException(e.getMessage());
                }
            }
            return new APIResponse<>(awards, response.getCode());
        }

        public static APIResponse<ArrayList<Award>> getEventAwards(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
            return getEventAwards(c, eventKey, "", loadFromCache);
        }
    }
}