package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.comparators.MatchSortByDisplayOrderComparator;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
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

        public static Team getTeamFromDB(Context c, String teamKey) {
            synchronized (Database.getInstance(c)) {
                return Database.getInstance(c).getTeamsTable().get(teamKey);
            }
        }

        public static APIResponse<Team> getTeam(Context c, String teamKey, boolean loadFromCache) throws NoDataException {
            final String URL = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.TEAM), teamKey);
            final String sqlWhere = Database.Teams.KEY + " = ?";
            return Team.query(c, loadFromCache, null, sqlWhere, new String[]{teamKey}, new String[]{URL});
        }

        public static APIResponse<Cursor> getCursorForTeamsInRange(Context c, int lowerBound, int upperBound) throws NoDataException {
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
                if (allTeamsLoadedForPage) {
                    teamListResponseCodes.add(ConnectionDetector.isConnectedToInternet(c) ? APIResponse.CODE.CACHED304 : APIResponse.CODE.OFFLINECACHE);
                } else {
                    // We need to load teams from the API
                    final String URL = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.TEAM_LIST), pageNum);
                    APIResponse<String> teamListResponse = TBAv2.getResponseFromURLOrThrow(c, URL, false);
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

        public static APIResponse<ArrayList<Integer>> getYearsParticipated(Context c, String teamKey, boolean loadFromCache) throws NoDataException {
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.TEAM_YEARS_PARTICIPATED), teamKey);
            String sqlWhere = Database.Teams.KEY + " = ?";
            String[] teamFields = new String[]{Database.Teams.KEY, Database.Teams.YEARS_PARTICIPATED, Database.Teams.SHORTNAME, Database.Teams.NUMBER};
            APIResponse<Team> teamResponse = Team.query(c, loadFromCache, teamFields, sqlWhere, new String[]{teamKey}, new String[]{apiUrl});
            ArrayList<Integer> years = new ArrayList<>();
            try {
                for (JsonElement year : teamResponse.getData().getYearsParticipated()) {
                    years.add(year.getAsInt());
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                Log.w(Constants.DATAMANAGER_LOG, "Unable to fetch years participated");
            }
            return new APIResponse<>(years, teamResponse.getCode());
        }

        public static APIResponse<ArrayList<Event>> getEventsForTeam(Context c, String teamKey, int year, boolean loadFromCache) throws NoDataException {
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.TEAM_EVENTS), teamKey, year);
            String sqlWhere = Database.EventTeams.TEAMKEY + " = ? AND " + Database.EventTeams.YEAR + " = ?";
            APIResponse<ArrayList<EventTeam>> eventTeams = EventTeam.queryList(c, loadFromCache, teamKey, null, sqlWhere, new String[]{teamKey, Integer.toString(year)}, new String[]{apiUrl});
            ArrayList<Event> events = new ArrayList<>();
            APIResponse.CODE code = eventTeams.getCode();
            for (EventTeam e : eventTeams.getData()) {
                try {
                    APIResponse<Event> event = Events.getEvent(c, e.getEventKey(), loadFromCache);
                    events.add(event.getData());
                    code = APIResponse.mergeCodes(code, event.getCode());
                } catch (BasicModel.FieldNotDefinedException e1) {
                    Log.e(Constants.LOG_TAG, "Unable to query event for team");
                }
            }
            return new APIResponse<>(events, code);
        }

        public static APIResponse<ArrayList<Match>> getMatchesForTeamAtEvent(Context c, String teamKey, String eventKey, boolean loadFromCache) throws NoDataException {
            APIResponse<ArrayList<Match>> output;
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_MATCHES), eventKey);
            String sqlWhere = Database.Matches.EVENT + " = ? AND " + Database.Matches.ALLIANCES + " LIKE ? ";
            output = Match.queryList(c, loadFromCache, teamKey, null, sqlWhere, new String[]{eventKey, "%" + teamKey + "%"}, new String[]{apiUrl});
            Collections.sort(output.getData(), new MatchSortByDisplayOrderComparator());
            return output;
        }

        public static APIResponse<ArrayList<Award>> getAwardsForTeamAtEvent(Context c, String teamKey, String eventKey, boolean loadFromCache) throws NoDataException {
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.TEAM_EVENT_AWARDS), teamKey, eventKey);
            String sqlWhere = Database.Awards.EVENTKEY + " = ? AND " + Database.Awards.WINNERS + " LIKE ? ";
            return Award.queryList(c, loadFromCache, teamKey, null, sqlWhere, new String[]{eventKey, "%" + teamKey.substring(3) + "%"}, new String[]{apiUrl});
        }

        public static APIResponse<ArrayList<Media>> getTeamMedia(Context c, String teamKey, int year, boolean loadFromCache) throws NoDataException {
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.TEAM_MEDIA), teamKey, year);
            String sqlWhere = Database.Medias.TEAMKEY + " = ? AND " + Database.Medias.YEAR + " = ?";
            return Media.queryList(c, teamKey, year, loadFromCache, null, sqlWhere, new String[]{teamKey, Integer.toString(year)}, new String[]{apiUrl});
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

        public static APIResponse<Event> getEvent(Context c, String key, boolean loadFromCache) throws NoDataException {
            final String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_INFO), key);
            String sqlWhere = Database.Events.KEY + " = ?";
            return Event.query(c, loadFromCache, null, sqlWhere, new String[]{key}, new String[]{apiUrl});
        }

        public static APIResponse<ArrayList<Event>> getSimpleEventsInWeek(Context c, int year, int week, boolean loadFromCache) throws NoDataException {
            Log.d("get events for week", "getting for week: " + week);

            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_LIST), year);
            String sqlWhere;
            String[] whereArgs;
            if (week > Utilities.getCmpWeek(year)) {
                sqlWhere = Database.Events.YEAR + " = ? AND " + Database.Events.WEEK + " > ?";
                whereArgs = new String[]{Integer.toString(year), Integer.toString(Utilities.getCmpWeek(year))};
            } else {
                sqlWhere = Database.Events.YEAR + " = ? AND " + Database.Events.WEEK + " = ?";
                whereArgs = new String[]{Integer.toString(year), Integer.toString(week)};
            }

            return Event.queryList(c, loadFromCache, null, sqlWhere, whereArgs, new String[]{apiUrl});
        }

        public static APIResponse<HashMap<String, ArrayList<Event>>> getEventsByYear(Context c, int year, boolean loadFromCache) throws NoDataException {
            HashMap<String, ArrayList<Event>> groupedEvents;
            APIResponse<ArrayList<Event>> eventListResponse;
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_LIST), year);
            String sqlWhere = Database.Events.YEAR + " =?";
            eventListResponse = Event.queryList(c, loadFromCache, null, sqlWhere, new String[]{Integer.toString(year)}, new String[]{apiUrl});

            if (eventListResponse.getCode() != APIResponse.CODE.NODATA) {
                PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR + year, true).commit();
            }
            groupedEvents = EventHelper.groupByWeek(eventListResponse.getData());
            return new APIResponse<>(groupedEvents, eventListResponse.getCode());
        }

        public static APIResponse<ArrayList<Team>> getEventTeams(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
            ArrayList<Team> teams = new ArrayList<>();
            Log.d("event teams", "Fetching teams for " + eventKey);
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_TEAMS), eventKey);
            String sqlWhere = Database.Events.KEY + " = ?";
            String[] eventFields = new String[]{Database.Events.KEY, Database.Events.NAME, Database.Events.YEAR, Database.Events.TYPE, Database.Events.TEAMS};
            APIResponse<Event> eventResponse = Event.query(c, loadFromCache, eventFields, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
            try {
                JsonArray teamList = eventResponse.getData().getTeams();
                Log.d(Constants.LOG_TAG, "Found " + teamList.size() + " teams");
                for (JsonElement t : teamList) {
                    teams.add(JSONManager.getGson().fromJson(t, Team.class));
                }
                return new APIResponse<>(teams, eventResponse.getCode());
            } catch (BasicModel.FieldNotDefinedException e) {
                throw new NoDataException(e.getMessage());
            }
        }

        public static APIResponse<ArrayList<JsonArray>> getEventRankings(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
            ArrayList<JsonArray> rankings = new ArrayList<>();
            Log.d("event ranks", "Fetching rankings for " + eventKey);
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_RANKS), eventKey);
            String sqlWhere = Database.Events.KEY + " = ?";
            String[] eventFields = new String[]{Database.Events.KEY, Database.Events.NAME, Database.Events.YEAR, Database.Events.TYPE, Database.Events.RANKINGS};
            APIResponse<Event> eventResponse = Event.query(c, loadFromCache, eventFields, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
            try {
                JsonArray rankArray = eventResponse.getData().getRankings();
                for (JsonElement r : rankArray) {
                    rankings.add(r.getAsJsonArray());
                }
                return new APIResponse<>(rankings, eventResponse.getCode());
            } catch (BasicModel.FieldNotDefinedException e) {
                throw new NoDataException(e.getMessage());
            }
        }

        public static APIResponse<ArrayList<Event>> getEventsInDistrict(Context c, String districtKey, boolean loadFromCache) throws NoDataException{
            Log.d(Constants.LOG_TAG, "getting for district: " + districtKey);

            int year = Integer.parseInt(districtKey.substring(0, 4));
            DistrictHelper.DISTRICTS districtType = DistrictHelper.districtTypeFromKey(districtKey);

            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.DISTRICT_EVENTS), districtType.getAbbreviation(), year);
            String sqlWhere = Database.Events.DISTRICT + " = ? AND " + Database.Events.YEAR + " = ? ";
            String[] whereArgs = new String[]{Integer.toString(districtType.ordinal()), Integer.toString(year)};

            return Event.queryList(c, loadFromCache, null, sqlWhere, whereArgs, new String[]{apiUrl});
        }

        public static APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
            return getMatchList(c, eventKey, "", loadFromCache);
        }

        public static APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey, String teamKey, boolean loadFromCache) throws NoDataException {
            ArrayList<Match> results = new ArrayList<>();
            Log.d("match list", "fetching matches for " + eventKey);

            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_MATCHES), eventKey);
            String sqlWhere = Database.Matches.EVENT + " = ?";
            APIResponse<ArrayList<Match>> matchResponse = Match.queryList(c, loadFromCache, null, null, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
            Log.d(Constants.LOG_TAG, "Found " + matchResponse.getData().size() + " matches");
            for (Match match : matchResponse.getData()) {
                try {
                    if (match.getAlliances().toString().contains(teamKey + "\"")) {
                        results.add(match);
                    }
                } catch (BasicModel.FieldNotDefinedException e) {
                    throw new NoDataException(e.getMessage());
                }
            }

            Collections.sort(results, new MatchSortByDisplayOrderComparator());
            return new APIResponse<>(results, matchResponse.getCode());
        }

        public static APIResponse<JsonObject> getEventStats(Context c, String eventKey, boolean loadFromCache) throws NoDataException {
            return getEventStats(c, eventKey, "", loadFromCache);
        }

        public static APIResponse<JsonObject> getEventStats(Context c, String eventKey, String teamKey, boolean loadFromCache) throws NoDataException {
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_STATS), eventKey);
            String sqlWhere = Database.Events.KEY + " = ?";
            String[] eventFields = new String[]{Database.Events.KEY, Database.Events.NAME, Database.Events.YEAR, Database.Events.TYPE, Database.Events.STATS};
            APIResponse<Event> eventResponse = Event.query(c, loadFromCache, eventFields, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
            try {
                JsonObject allStats = eventResponse.getData().getStats();
                if (teamKey.isEmpty()) {
                    return new APIResponse<>(allStats, eventResponse.getCode());
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
                    return new APIResponse<>(teamStats, eventResponse.getCode());
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                e.printStackTrace();
                throw new NoDataException(e.getMessage());
            }
        }

        public static APIResponse<ArrayList<Award>> getEventAwards(Context c, String eventKey, String teamKey, boolean loadFromCache) throws NoDataException {
            ArrayList<Award> awards = new ArrayList<>();
            Log.d("event awards", "Fetching awards for " + eventKey+" "+teamKey);
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_AWARDS), eventKey);
            String sqlWhere = Database.Awards.EVENTKEY + " = ?";
            APIResponse<ArrayList<Award>> awardResponse = Award.queryList(c, loadFromCache, null, null, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
            for (Award award : awardResponse.getData()) {
                try {
                    if (teamKey.isEmpty()) {
                        awards.add(award);
                    } else {
                        JsonArray winners = award.getWinners();
                        for (JsonElement winner : winners) {
                            JsonObject w = winner.getAsJsonObject();
                            if (w.has("team_number") && !w.get("team_number").isJsonNull() && w.get("team_number").getAsString().equals(teamKey.substring(3))) {
                                awards.add(award);
                                break;
                            }
                        }
                    }
                } catch (BasicModel.FieldNotDefinedException e) {
                    throw new NoDataException(e.getMessage());
                }
            }
            return new APIResponse<>(awards, awardResponse.getCode());
        }
    }

    public static class Matches {

        public static APIResponse<Match> getMatch(Context c, String matchKey, boolean loadFromCache) throws NoDataException {
            if (!MatchHelper.validateMatchKey(matchKey)) {
                throw new NoDataException("Invalid match key");
            }
            String eventKey = matchKey.substring(0, matchKey.indexOf("_"));
            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.EVENT_MATCHES), eventKey);
            String sqlWhere = Database.Matches.KEY + " = ?";
            return Match.query(c, matchKey, loadFromCache, null, sqlWhere, new String[]{matchKey}, new String[]{apiUrl});
        }

    }

    public static class Districts {

        public static final String ALL_DISTRICTS_LOADED_TO_DATABASE_FOR_YEAR = "all_districts_loaded_for_year_";

        public static APIResponse<ArrayList<District>> getDistrictsInYear(Context c, int year, boolean loadFromCache) throws NoDataException{
            Log.d(Constants.DATAMANAGER_LOG, "getting districts in : " + year);

            String apiUrl = String.format(TBAv2.getTBAApiUrl(c, TBAv2.QUERY.DISTRICT_LIST), year);
            String sqlWhere = Database.Districts.YEAR + " = ?";
            String[] whereArgs = new String[]{Integer.toString(year)};

            return District.queryList(c, loadFromCache, null, sqlWhere, whereArgs, new String[]{apiUrl});
        }

    }
}