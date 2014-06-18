package com.thebluealliance.androidclient.datafeed.datamanger;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.SimpleTeam;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;

/**
 * File created by phil on 6/18/14.
 */
public class Teams {
    public static final String ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE = "all_teams_loaded_for_page_";

    public synchronized static APIResponse<Team> getTeam(Context c, String teamKey, boolean loadFromCache) throws DataManager.NoDataException {
        final String URL = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM), teamKey);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true, loadFromCache);
        Team team = JSONManager.getGson().fromJson(response.getData(), Team.class);

        return new APIResponse<>(team, response.getCode());
    }

    public synchronized static APIResponse<Team> getTeam(Context c, String teamKey, int year, boolean loadFromCache) throws DataManager.NoDataException {
        final String URL = "http://thebluealliance.com/api/v2/team/" + teamKey + "/" + year;
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, URL, true, loadFromCache);
        Team team = JSONManager.getGson().fromJson(response.getData(), Team.class);

        return new APIResponse<>(team, response.getCode());
    }

    public synchronized static APIResponse<Cursor> getCursorForSimpleTeamsInRange(Context c, int lowerBound, int upperBound) throws DataManager.NoDataException {
        ArrayList<Integer> requiredPageNums = new ArrayList();
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

                ArrayList<SimpleTeam> teams = TBAv2.getTeamList(teamListResponse.getData());
                Database.getInstance(c).storeTeams(teams);
                if (teamListResponse.getCode() != APIResponse.CODE.NODATA) {
                    //only update preference if actual data was loaded
                    PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE + pageNum, true).commit();
                }
            }
        }

        cursor = Database.getInstance(c).getCursorForTeamsInRange(lowerBound, upperBound);
        APIResponse.CODE[] a = new APIResponse.CODE[teamListResponseCodes.size()];

        return new APIResponse<>(cursor, APIResponse.mergeCodes(teamListResponseCodes.toArray(a)));
    }

    public synchronized static APIResponse<ArrayList<SimpleTeam>> getSimpleTeamsInRange(Context c, int lowerBound, int upperBound) throws DataManager.NoDataException {
        ArrayList<Integer> requiredPageNums = new ArrayList();
        for (int pageNum = lowerBound / Constants.API_TEAM_LIST_PAGE_SIZE; pageNum <= upperBound / Constants.API_TEAM_LIST_PAGE_SIZE; pageNum++) {
            requiredPageNums.add(pageNum);
        }
        Log.d("get simple teams", "getting teams in range: " + lowerBound + " - " + upperBound + ". requires pages: " + requiredPageNums.toString());

        ArrayList<SimpleTeam> teams = new ArrayList<>();
        ArrayList<APIResponse.CODE> teamListResponseCodes = new ArrayList<>();

        for (int i = 0; i < requiredPageNums.size(); i++) {
            int pageNum = requiredPageNums.get(i);

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

                teams = TBAv2.getTeamList(teamListResponse.getData());
                Database.getInstance(c).storeTeams(teams);
                teams = Database.getInstance(c).getTeamsInRange(lowerBound, upperBound);
                if (teamListResponse.getCode() != APIResponse.CODE.NODATA) {
                    //only update preference if actual data was loaded
                    PreferenceManager.getDefaultSharedPreferences(c).edit().putBoolean(ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE + pageNum, true).commit();
                }
            }
        }

        teams = Database.getInstance(c).getTeamsInRange(lowerBound, upperBound);
        APIResponse.CODE[] a = new APIResponse.CODE[teamListResponseCodes.size()];
        APIResponse<String> response = new APIResponse<>("", APIResponse.mergeCodes(teamListResponseCodes.toArray(a)));

        return new APIResponse<>(teams, response.getCode());
    }

    public synchronized static APIResponse<ArrayList<Media>> getTeamMedia(Context c, String teamKey, int year, boolean loadFromCache) throws DataManager.NoDataException {
        ArrayList<Media> output = new ArrayList<>();
        String apiUrl = String.format(TBAv2.API_URL.get(TBAv2.QUERY.TEAM_MEDIA), teamKey, year);
        APIResponse<String> response = TBAv2.getResponseFromURLOrThrow(c, apiUrl, true, loadFromCache);
        JsonArray mediaArray = JSONManager.getasJsonArray(response.getData());
        for (JsonElement media : mediaArray) {
            output.add(JSONManager.getGson().fromJson(media, Media.class));
        }
        return new APIResponse<>(output, response.getCode());
    }

    public static synchronized APIResponse<Integer> getRankForTeamAtEvent(Context c, String teamKey, String eventKey, boolean loadFromCache) throws DataManager.NoDataException {
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
