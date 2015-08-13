package com.thebluealliance.androidclient.datafeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.appspot.tbatv_prod_hrd.tbaMobile.TbaMobile;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesFavoriteCollection;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesFavoriteMessage;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesSubscriptionCollection;
import com.appspot.tbatv_prod_hrd.tbaMobile.model.ModelsMobileApiMessagesSubscriptionMessage;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.database.tables.FavoritesTable;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.database.tables.SubscriptionsTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.helpers.ConnectionDetector;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.helpers.DistrictTeamHelper;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Favorite;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Subscription;
import com.thebluealliance.androidclient.models.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

        public static APIResponse<Team> getTeam(Context c, String teamKey, RequestParams requestParams) throws NoDataException {
            final String URL = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.TEAM), teamKey);
            final String sqlWhere = TeamsTable.KEY + " = ?";
            return Team.query(c, requestParams, null, sqlWhere, new String[]{teamKey}, new String[]{URL});
        }

        public static APIResponse<Cursor> getCursorForTeamsInRange(Context c, int lowerBound, int upperBound, RequestParams requestParams) throws NoDataException {
            ArrayList<Integer> requiredPageNums = new ArrayList<>();
            for (int pageNum = lowerBound / Constants.API_TEAM_LIST_PAGE_SIZE; pageNum <= upperBound / Constants.API_TEAM_LIST_PAGE_SIZE; pageNum++) {
                requiredPageNums.add(pageNum);
            }
            Log.d(Constants.DATAMANAGER_LOG, "getting cursor for teams in range: " + lowerBound + " - " + upperBound + ". requires pages: " + requiredPageNums.toString());

            ArrayList<APIResponse.CODE> teamListResponseCodes = new ArrayList<>();
            Cursor cursor;
            for (Integer requiredPageNum : requiredPageNums) {

                final String URL = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.TEAM_LIST), requiredPageNum);
                APIResponse<String> teamListResponse = LegacyAPIHelper.getResponseFromURLOrThrow(c, URL, requestParams);
                teamListResponseCodes.add(teamListResponse.getCode());

                ArrayList<Team> teams = LegacyAPIHelper.getTeamList(teamListResponse.getData());
                if (teamListResponse.getCode() == APIResponse.CODE.WEBLOAD || teamListResponse.getCode() == APIResponse.CODE.UPDATED && teams.size() > 0) {
                    Database.getInstance(c).getTeamsTable().add(teams);
                }
            }
            cursor = Database.getInstance(c).getTeamsTable().getCursorForTeamsInRange(lowerBound, upperBound);
            APIResponse.CODE[] a = new APIResponse.CODE[teamListResponseCodes.size()];

            return new APIResponse<>(cursor, APIResponse.mergeCodes(teamListResponseCodes.toArray(a)));
        }

        public static APIResponse<ArrayList<Integer>> getYearsParticipated(Context c, String teamKey, RequestParams requestParams) throws NoDataException {
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.TEAM_YEARS_PARTICIPATED), teamKey);
            String sqlWhere = TeamsTable.KEY + " = ?";
            String[] teamFields = new String[]{TeamsTable.KEY, TeamsTable.YEARS_PARTICIPATED, TeamsTable.SHORTNAME, TeamsTable.NUMBER};
            APIResponse<Team> teamResponse = Team.query(c, requestParams, teamFields, sqlWhere, new String[]{teamKey}, new String[]{apiUrl});
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

        public static APIResponse<ArrayList<Event>> getEventsForTeam(Context c, String teamKey, int year, RequestParams requestParams) throws NoDataException {
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.TEAM_EVENTS), teamKey, year);
            String sqlWhere = EventTeamsTable.TEAMKEY + " = ? AND " + EventTeamsTable.YEAR + " = ?";
            APIResponse<ArrayList<EventTeam>> eventTeams = EventTeam.queryList(c, requestParams, teamKey, null, sqlWhere, new String[]{teamKey, Integer.toString(year)}, new String[]{apiUrl});
            ArrayList<Event> events = new ArrayList<>();
            APIResponse.CODE code = eventTeams.getCode();
            for (EventTeam e : eventTeams.getData()) {
                try {
                    APIResponse<Event> event = Events.getEvent(c, e.getEventKey(), requestParams);
                    events.add(event.getData());
                    code = APIResponse.mergeCodes(code, event.getCode());
                } catch (BasicModel.FieldNotDefinedException e1) {
                    Log.e(Constants.LOG_TAG, "Unable to query event for team");
                }
            }
            return new APIResponse<>(events, code);
        }

        public static APIResponse<ArrayList<Event>> getDistrictEventsForTeam(Context c, String teamKey, String districtKey, RequestParams requestParams) throws NoDataException {
            int year = Integer.parseInt(districtKey.substring(0, 4));
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.TEAM_EVENTS), teamKey, year);
            String sqlWhere = EventTeamsTable.TEAMKEY + " = ? AND " + EventTeamsTable.YEAR + " = ?";
            APIResponse<ArrayList<EventTeam>> eventTeams = EventTeam.queryList(c, requestParams, teamKey, null, sqlWhere, new String[]{teamKey, Integer.toString(year)}, new String[]{apiUrl});
            ArrayList<Event> events = new ArrayList<>();
            APIResponse.CODE code = eventTeams.getCode();
            for (EventTeam e : eventTeams.getData()) {
                try {
                    APIResponse<Event> event = Events.getEventBasic(c, e.getEventKey(), requestParams);
                    events.add(event.getData());
                    code = APIResponse.mergeCodes(code, event.getCode());
                } catch (BasicModel.FieldNotDefinedException e1) {
                    Log.e(Constants.LOG_TAG, "Unable to query event for team");
                }
            }
            return new APIResponse<>(events, code);
        }

        public static APIResponse<ArrayList<Media>> getTeamMedia(Context c, String teamKey, int year, RequestParams requestParams) throws NoDataException {
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.TEAM_MEDIA), teamKey, year);
            String sqlWhere = MediasTable.TEAMKEY + " = ? AND " + MediasTable.YEAR + " = ?";
            return Media.queryList(c, teamKey, year, requestParams, null, sqlWhere, new String[]{teamKey, Integer.toString(year)}, new String[]{apiUrl});
        }

        public static APIResponse<JsonArray> getRankForTeamAtEvent(Context c, String teamKey, String eventKey, RequestParams requestParams) throws NoDataException {
            APIResponse<ArrayList<JsonArray>> allRankings = Events.getEventRankings(c, eventKey, requestParams);
            String teamNumber = teamKey.substring(3);

            ArrayList<JsonArray> data = allRankings.getData();
            JsonArray ret = new JsonArray();
            for (JsonArray i : data) {
                if (i.get(1).getAsString().equals(teamNumber)) {
                    ret.add(data.get(0)); // Add the header row
                    ret.add(i);
                    return new APIResponse<>(ret, allRankings.getCode());
                }
            }
            return new APIResponse<>(ret, allRankings.getCode());
        }
    }

    public static class Events {
        public static final String ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR = "all_events_loaded_for_year_";

        public static APIResponse<Event> getEvent(Context c, String key, RequestParams requestParams) throws NoDataException {
            final String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_INFO), key);
            String sqlWhere = EventsTable.KEY + " = ?";
            return Event.query(c, key, requestParams, null, sqlWhere, new String[]{key}, new String[]{apiUrl});
        }

        public static APIResponse<Event> getEventBasic(Context c, String key, RequestParams requestParams) throws NoDataException {
            final String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_INFO), key);
            String[] fields = new String[]{EventsTable.KEY, EventsTable.NAME, EventsTable.DISTRICT};
            String sqlWhere = EventsTable.KEY + " = ?";
            return Event.query(c, key, requestParams, fields, sqlWhere, new String[]{key}, new String[]{apiUrl});
        }

        public static APIResponse<ArrayList<Event>> getSimpleEventsInWeek(Context c, int year, int week, RequestParams requestParams) throws NoDataException {
            Log.d("get events for week", "getting for week: " + week);

            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_LIST), year);
            String sqlWhere;
            String[] whereArgs;
            if (week > Utilities.getCmpWeek(year)) {
                sqlWhere = EventsTable.YEAR + " = ? AND " + EventsTable.WEEK + " > ?";
                whereArgs = new String[]{Integer.toString(year), Integer.toString(Utilities.getCmpWeek(year))};
            } else {
                sqlWhere = EventsTable.YEAR + " = ? AND " + EventsTable.WEEK + " = ?";
                whereArgs = new String[]{Integer.toString(year), Integer.toString(week)};
            }

            return Event.queryList(c, requestParams, null, sqlWhere, whereArgs, new String[]{apiUrl});
        }

        public static APIResponse<HashMap<String, List<Event>>> getEventsByYear(Context c, int year, RequestParams requestParams) throws NoDataException {
            HashMap<String, List<Event>> groupedEvents;
            APIResponse<ArrayList<Event>> eventListResponse;
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_LIST), year);
            String sqlWhere = EventsTable.YEAR + " =?";
            eventListResponse = Event.queryList(c, requestParams, null, sqlWhere, new String[]{Integer.toString(year)}, new String[]{apiUrl});

            if (eventListResponse.getCode() != APIResponse.CODE.NODATA) {
                PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR + year, true).commit();
            }
            groupedEvents = EventHelper.groupByWeek(eventListResponse.getData());
            return new APIResponse<>(groupedEvents, eventListResponse.getCode());
        }

        public static APIResponse<ArrayList<Team>> getEventTeams(Context c, String eventKey, RequestParams requestParams) throws NoDataException {
            ArrayList<Team> teams = new ArrayList<>();
            Log.d("event teams", "Fetching teams for " + eventKey);
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_TEAMS), eventKey);
            String sqlWhere = EventsTable.KEY + " = ?";
            String[] eventFields = new String[]{EventsTable.KEY, EventsTable.NAME, EventsTable.YEAR, EventsTable.TYPE, EventsTable.TEAMS};
            APIResponse<Event> eventResponse = Event.query(c, eventKey, requestParams, eventFields, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
            try {
                JsonArray teamList = eventResponse.getData().getTeams();
                Log.d(Constants.LOG_TAG, "Found " + teamList.size() + " teams");
                for (JsonElement t : teamList) {
                    teams.add(JSONHelper.getGson().fromJson(t, Team.class));
                }
                return new APIResponse<>(teams, eventResponse.getCode());
            } catch (BasicModel.FieldNotDefinedException e) {
                throw new NoDataException(e.getMessage());
            }
        }

        public static APIResponse<ArrayList<JsonArray>> getEventRankings(Context c, String eventKey, RequestParams requestParams) throws NoDataException {
            ArrayList<JsonArray> rankings = new ArrayList<>();
            Log.d("event ranks", "Fetching rankings for " + eventKey);
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_RANKS), eventKey);
            String sqlWhere = EventsTable.KEY + " = ?";
            String[] eventFields = new String[]{EventsTable.KEY, EventsTable.NAME, EventsTable.YEAR, EventsTable.TYPE, EventsTable.RANKINGS};
            APIResponse<Event> eventResponse = Event.query(c, eventKey, requestParams, eventFields, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
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

        public static APIResponse<ArrayList<Event>> getEventsInDistrict(Context c, String districtKey, RequestParams requestParams) throws NoDataException {
            Log.d(Constants.LOG_TAG, "getting for district: " + districtKey);

            int year = Integer.parseInt(districtKey.substring(0, 4));
            DistrictHelper.DISTRICTS districtType = DistrictHelper.districtTypeFromKey(districtKey);

            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.DISTRICT_EVENTS), districtType.getAbbreviation(), year);
            String sqlWhere = EventsTable.DISTRICT + " = ? AND " + EventsTable.YEAR + " = ? ";
            String[] whereArgs = new String[]{Integer.toString(districtType.ordinal()), Integer.toString(year)};

            return Event.queryList(c, requestParams, null, sqlWhere, whereArgs, new String[]{apiUrl});
        }

        public static APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey, RequestParams requestParams) throws NoDataException {
            return getMatchList(c, eventKey, "", requestParams);
        }

        public static APIResponse<ArrayList<Match>> getMatchList(Context c, String eventKey, String teamKey, RequestParams requestParams) throws NoDataException {
            ArrayList<Match> results = new ArrayList<>();
            Log.d("match list", "fetching matches for " + eventKey);

            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_MATCHES), eventKey);
            String sqlWhere = MatchesTable.EVENT + " = ?";
            APIResponse<ArrayList<Match>> matchResponse = Match.queryList(c, requestParams, null, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
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

            return new APIResponse<>(results, matchResponse.getCode());
        }

        public static APIResponse<JsonObject> getEventStats(Context c, String eventKey, RequestParams requestParams) throws NoDataException {
            return getEventStats(c, eventKey, "", requestParams);
        }

        public static APIResponse<JsonObject> getEventStats(Context c, String eventKey, String teamKey, RequestParams requestParams) throws NoDataException {
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_STATS), eventKey);
            String sqlWhere = EventsTable.KEY + " = ?";
            String[] eventFields = new String[]{EventsTable.KEY, EventsTable.NAME, EventsTable.YEAR, EventsTable.TYPE, EventsTable.STATS};
            APIResponse<Event> eventResponse = Event.query(c, eventKey, requestParams, eventFields, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
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

        public static APIResponse<ArrayList<Award>> getEventAwards(Context c, String eventKey, String teamKey, RequestParams requestParams) throws NoDataException {
            ArrayList<Award> awards = new ArrayList<>();
            Log.d("event awards", "Fetching awards for " + eventKey + " " + teamKey);
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_AWARDS), eventKey);
            String sqlWhere = AwardsTable.EVENTKEY + " = ?";
            APIResponse<ArrayList<Award>> awardResponse = Award.queryList(c, requestParams, null, null, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
            for (Award award : awardResponse.getData()) {
                try {
                    if (teamKey.isEmpty()) {
                        awards.add(award);
                    } else {
                        JsonArray winners = award.getWinners();
                        for (JsonElement winner : winners) {
                            JsonObject w = winner.getAsJsonObject();
                            JsonElement team_number = w.get("team_number");
                            if (!JSONHelper.isNull(team_number) && team_number.getAsString().equals(teamKey.substring(3))) {
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

        public static APIResponse<JsonObject> getDistrictPointsForEvent(Context c, String eventKey, RequestParams requestParams) throws NoDataException {
            return getDistrictPointsForEvent(c, eventKey, "", requestParams);
        }

        public static APIResponse<JsonObject> getDistrictPointsForEvent(Context c, String eventKey, String teamKey, RequestParams requestParams) throws NoDataException {
            Log.d(Constants.DATAMANAGER_LOG, "Fetching district points for " + eventKey + " " + teamKey);
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_DISTRICT_POINTS), eventKey);
            String sqlWhere = EventsTable.KEY + " = ?";
            String[] eventFields = new String[]{EventsTable.KEY, EventsTable.NAME, EventsTable.YEAR, EventsTable.TYPE, EventsTable.DISTRICT_POINTS};
            APIResponse<Event> eventResponse = Event.query(c, eventKey, requestParams, eventFields, sqlWhere, new String[]{eventKey}, new String[]{apiUrl});
            try {
                if (teamKey.isEmpty()) {
                    //we want all the event's points
                    if (eventResponse.getData().getDistrictPoints().has("points")) {
                        return new APIResponse<>(eventResponse.getData().getDistrictPoints().get("points").getAsJsonObject(), eventResponse.getCode());
                    } else {
                        return new APIResponse<>(new JsonObject(), eventResponse.getCode());
                    }
                } else {
                    //we want a single team's points at this event
                    if (eventResponse.getData().getDistrictPoints().has("points")) {
                        return new APIResponse<>(DistrictHelper.findPointsForTeam(eventResponse.getData().getDistrictPoints().getAsJsonObject(), teamKey), eventResponse.getCode());
                    } else {
                        return new APIResponse<>(new JsonObject(), eventResponse.getCode());
                    }
                }
            } catch (BasicModel.FieldNotDefinedException e) {
                throw new NoDataException(e.getMessage());
            }
        }
    }

    public static class Matches {

        public static APIResponse<Match> getMatch(Context c, String matchKey, RequestParams requestParams) throws NoDataException {
            if (!MatchHelper.validateMatchKey(matchKey)) {
                throw new NoDataException("Invalid match key");
            }
            String eventKey = matchKey.substring(0, matchKey.indexOf("_"));
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.EVENT_MATCHES), eventKey);
            String sqlWhere = MatchesTable.KEY + " = ?";
            return Match.query(c, matchKey, requestParams, null, sqlWhere, new String[]{matchKey}, new String[]{apiUrl});
        }

    }

    public static class Districts {

        public static final String ALL_DISTRICTS_LOADED_TO_DATABASE_FOR_YEAR = "all_districts_loaded_for_year_";

        public static APIResponse<District> getDistrict(Context c, String districtKey) throws NoDataException {
            String sqlWhere = DistrictsTable.KEY + " = ?";
            String[] whereArgs = new String[]{districtKey};
            return District.query(c, new RequestParams(true, false), null, sqlWhere, whereArgs, new String[]{});
        }

        public static APIResponse<ArrayList<District>> getDistrictsInYear(Context c, int year, RequestParams requestParams) throws NoDataException {
            Log.d(Constants.DATAMANAGER_LOG, "getting districts in : " + year);

            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.DISTRICT_LIST), year);
            String sqlWhere = DistrictsTable.YEAR + " = ?";
            String[] whereArgs = new String[]{Integer.toString(year)};

            return District.queryList(c, requestParams, null, sqlWhere, whereArgs, new String[]{apiUrl});
        }

        public static APIResponse<DistrictTeam> getDistrictTeam(Context c, String districtTeamKey, RequestParams requestParams) throws NoDataException {
            Log.d(Constants.DATAMANAGER_LOG, "getting district team: " + districtTeamKey);

            String districtKey = DistrictTeamHelper.getDistrictKey(districtTeamKey);
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.DISTRICT_RANKINGS), districtKey.substring(4), Integer.parseInt(districtKey.substring(0, 4)));
            String sqlWhere = DistrictTeamsTable.KEY + " = ?";
            String[] whereArgs = new String[]{districtTeamKey};

            return DistrictTeam.query(c, districtTeamKey, requestParams, null, sqlWhere, whereArgs, new String[]{apiUrl});
        }

        public static APIResponse<DistrictTeam> getDistrictTeamEvents(Context c, String districtTeamKey, RequestParams requestParams) throws NoDataException {
            Log.d(Constants.DATAMANAGER_LOG, "getting district team: " + districtTeamKey);

            String districtKey = DistrictTeamHelper.getDistrictKey(districtTeamKey);
            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.DISTRICT_RANKINGS), districtKey.substring(4), Integer.parseInt(districtKey.substring(0, 4)));
            String sqlWhere = DistrictTeamsTable.KEY + " = ?";
            String[] whereArgs = new String[]{districtTeamKey};
            String[] fields = new String[]{DistrictTeamsTable.KEY, DistrictTeamsTable.EVENT1_KEY, DistrictTeamsTable.EVENT2_KEY, DistrictTeamsTable.CMP_KEY};

            return DistrictTeam.query(c, districtTeamKey, requestParams, fields, sqlWhere, whereArgs, new String[]{apiUrl});
        }

        public static APIResponse<ArrayList<DistrictTeam>> getDistrictRankings(Context c, String districtKey, RequestParams requestParams) throws NoDataException {
            Log.d(Constants.DATAMANAGER_LOG, "getting district rankings for: " + districtKey);

            String apiUrl = String.format(LegacyAPIHelper.getTBAApiUrl(c, LegacyAPIHelper.QUERY.DISTRICT_RANKINGS), districtKey.substring(4), Integer.parseInt(districtKey.substring(0, 4)));
            String sqlWhere = DistrictTeamsTable.DISTRICT_KEY + " = ?";
            String[] whereArgs = new String[]{districtKey};

            return DistrictTeam.queryList(c, requestParams, null, sqlWhere, whereArgs, new String[]{apiUrl});
        }

        public static int getNumEventsForDistrict(Context c, String districtKey) {
            String[] fields = new String[]{DistrictsTable.KEY};
            String year = districtKey.substring(0, 4);
            int districtEnum = DistrictHelper.DISTRICTS.fromAbbreviation(districtKey.substring(4)).ordinal();
            String whereClause = EventsTable.YEAR + " = ? AND " + EventsTable.DISTRICT + " = ?";
            String[] whereArgs = new String[]{year, Integer.toString(districtEnum)};
            Cursor cursor = Database.getInstance(c).getEventsTable().query(fields, whereClause, whereArgs, null, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                return 0;
            } else {
                return cursor.getCount();
            }
        }
    }

    public static class MyTBA {

        /**
         * These methods will fetch the current user's myTBA data from the web and store it in the
         * local db They also return an ArrayList of the favorite/subscription models and a
         * convienence
         */

        public static final String LAST_FAVORITES_UPDATE = "last_mytba_favorites_update_%s";
        public static final String LAST_SUBSCRIPTIONS_UPDATE = "last_mytba_subscriptions_update_%s";

        public static APIResponse<ArrayList<Favorite>> updateUserFavorites(final Context context, RequestParams requestParams) {
            String currentUser = AccountHelper.getSelectedAccount(context);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String prefString = String.format(LAST_FAVORITES_UPDATE, currentUser);

            ArrayList<Favorite> favoriteModels = new ArrayList<>();
            Date now = new Date();
            Date futureTime = new Date(prefs.getLong(prefString, 0) + Constants.MY_TBA_UPDATE_TIMEOUT);
            // TODO this endpoint needs some caching so we keep load off the server
            if (!requestParams.forceFromWeb && now.before(futureTime)) {
                //don't hit the API too often.
                Log.d(Constants.LOG_TAG, "Not updating myTBA favorites. Too soon since last update");
                return new APIResponse<>(null, APIResponse.CODE.CACHED304);
            }

            if (!ConnectionDetector.isConnectedToInternet(context)) {
                return new APIResponse<>(null, APIResponse.CODE.OFFLINECACHE);
            }

            Log.d(Constants.LOG_TAG, "Updating myTBA favorites");
            TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
            if (service == null) {
                Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
                Handler mainHandler = new Handler(context.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show();
                    }
                });
                return new APIResponse<>(null, APIResponse.CODE.NODATA);
            }
            ModelsMobileApiMessagesFavoriteCollection favoriteCollection;
            try {
                favoriteCollection = service.favorites().list().execute();
            } catch (IOException e) {
                Log.w(Constants.LOG_TAG, "Unable to update myTBA favorites");
                e.printStackTrace();
                return new APIResponse<>(null, APIResponse.CODE.NODATA);
            }

            FavoritesTable favorites = Database.getInstance(context).getFavoritesTable();
            favorites.recreate(currentUser);
            if (favoriteCollection.getFavorites() != null) {
                for (ModelsMobileApiMessagesFavoriteMessage f : favoriteCollection.getFavorites()) {
                    favoriteModels.add(new Favorite(currentUser, f.getModelKey(), f.getModelType().intValue()));
                }
                favorites.add(favoriteModels);
                Log.d(Constants.LOG_TAG, "Added " + favoriteModels.size() + " favorites");
            }

            prefs.edit().putLong(prefString, new Date().getTime()).apply();
            return new APIResponse<>(favoriteModels, APIResponse.CODE.WEBLOAD);
        }

        public static APIResponse<ArrayList<Subscription>> updateUserSubscriptions(final Context context, RequestParams requestParams) {
            String currentUser = AccountHelper.getSelectedAccount(context);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String prefString = String.format(LAST_SUBSCRIPTIONS_UPDATE, currentUser);

            ArrayList<Subscription> subscriptionModels = new ArrayList<>();
            Date now = new Date();
            Date futureTime = new Date(prefs.getLong(prefString, 0) + Constants.MY_TBA_UPDATE_TIMEOUT);
            // TODO this endpoint needs some caching so we keep load off the server
            if (!requestParams.forceFromWeb && now.before(futureTime)) {
                //don't hit the API too often.
                Log.d(Constants.LOG_TAG, "Not updating myTBA subscriptions. Too soon since last update");
                return new APIResponse<>(null, APIResponse.CODE.CACHED304);
            }

            if (!ConnectionDetector.isConnectedToInternet(context)) {
                return new APIResponse<>(null, APIResponse.CODE.OFFLINECACHE);
            }

            Log.d(Constants.LOG_TAG, "Updating myTBA subscriptions");
            TbaMobile service = AccountHelper.getAuthedTbaMobile(context);
            if (service == null) {
                Log.e(Constants.LOG_TAG, "Couldn't get TBA Mobile Service");
                Handler mainHandler = new Handler(context.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.mytba_error_no_account), Toast.LENGTH_SHORT).show();
                    }
                });
                return new APIResponse<>(null, APIResponse.CODE.NODATA);
            }
            ModelsMobileApiMessagesSubscriptionCollection subscriptionCollection;
            try {
                subscriptionCollection = service.subscriptions().list().execute();
            } catch (IOException e) {
                Log.w(Constants.LOG_TAG, "Unable to update myTBA subscriptions");
                e.printStackTrace();
                return new APIResponse<>(null, APIResponse.CODE.NODATA);
            }

            SubscriptionsTable subscriptions = Database.getInstance(context).getSubscriptionsTable();
            subscriptions.recreate(currentUser);
            if (subscriptionCollection.getSubscriptions() != null) {
                for (ModelsMobileApiMessagesSubscriptionMessage s : subscriptionCollection.getSubscriptions()) {
                    subscriptionModels.add(new Subscription(currentUser, s.getModelKey(), s.getNotifications(), s.getModelType().intValue()));
                }
                subscriptions.add(subscriptionModels);
            }

            Log.d(Constants.LOG_TAG, "Added " + subscriptionCollection.size() + " subscriptions");
            prefs.edit().putLong(prefString, new Date().getTime()).apply();
            return new APIResponse<>(subscriptionModels, APIResponse.CODE.WEBLOAD);
        }
    }
}