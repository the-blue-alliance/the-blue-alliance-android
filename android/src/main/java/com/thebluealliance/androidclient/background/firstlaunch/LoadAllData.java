package com.thebluealliance.androidclient.background.firstlaunch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.LaunchActivity;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * File created by phil on 4/20/14.
 */
public class LoadAllData extends AsyncTask<Short, LoadAllData.LoadProgressInfo, Void> {

    private LoadAllDataCallbacks callbacks;
    private Context context;
    private long startTime;

    public LoadAllData(LoadAllDataCallbacks callbacks, Context c) {
        this.callbacks = callbacks;
        this.context = c.getApplicationContext();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    protected Void doInBackground(Short... params) {
        if (callbacks == null) {
            throw new IllegalArgumentException("callbacks must not be null!");
        }

        Log.d(Constants.LOG_TAG, "Input: " + Arrays.deepToString(params));

        Short[] dataToLoad;
        if (params == null) {
            dataToLoad = new Short[]{LaunchActivity.LoadAllDataTaskFragment.LOAD_TEAMS,
                    LaunchActivity.LoadAllDataTaskFragment.LOAD_EVENTS,
                    LaunchActivity.LoadAllDataTaskFragment.LOAD_DISTRICTS};
        } else {
            dataToLoad = params;
        }

        Log.d(Constants.LOG_TAG, "Loading: " + Arrays.deepToString(dataToLoad));

        /* We need to download and cache every team and event into the database. To avoid
         * unexpected behavior caused by changes in network connectivity, we will load all
         * teams into memory first. Once we have loaded everything, only then will we wipe the
         * database and insert all the new teams and events.        *
         */

        try {
            ArrayList<Team> teams = new ArrayList<>();
            ArrayList<Event> events = new ArrayList<>();
            ArrayList<District> districts = new ArrayList<>();
            int maxPageNum = 0;

            if (Arrays.binarySearch(dataToLoad, LaunchActivity.LoadAllDataTaskFragment.LOAD_TEAMS) != -1) {
                // First we will load all the teams
                for (int pageNum = 0; pageNum < 20; pageNum++) {  // limit to 20 pages to prevent potential infinite loop
                    if (isCancelled()) {
                        return null;
                    }
                    int start = pageNum * Constants.API_TEAM_LIST_PAGE_SIZE;
                    int end = start + Constants.API_TEAM_LIST_PAGE_SIZE - 1;
                    start = start == 0 ? 1 : start;
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, String.format(context.getString(R.string.loading_teams), start, end)));
                    APIResponse<String> teamListResponse;
                    teamListResponse = TBAv2.getResponseFromURLOrThrow(context, String.format(TBAv2.getTBAApiUrl(context, TBAv2.QUERY.TEAM_LIST), pageNum), new RequestParams());
                    JsonArray responseObject = JSONManager.getasJsonArray(teamListResponse.getData());
                    if (responseObject != null) {
                        if (responseObject.size() == 0) {
                            // No teams found for a page; we are done
                            break;
                        }
                    }
                    maxPageNum = Math.max(maxPageNum, pageNum);
                    ArrayList<Team> pageTeams = TBAv2.getTeamList(teamListResponse.getData());
                    teams.addAll(pageTeams);
                }
            }

            if (Arrays.binarySearch(dataToLoad, LaunchActivity.LoadAllDataTaskFragment.LOAD_EVENTS) != -1) {
                // Now we load all events
                for (int year = Constants.FIRST_COMP_YEAR; year <= Constants.MAX_COMP_YEAR; year++) {
                    if (isCancelled()) {
                        return null;
                    }
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, String.format(context.getString(R.string.loading_events), Integer.toString(year))));
                    APIResponse<String> eventListResponse;
                    String eventsUrl = String.format(TBAv2.getTBAApiUrl(context, TBAv2.QUERY.EVENT_LIST), year);
                    eventListResponse = TBAv2.getResponseFromURLOrThrow(context, eventsUrl, new RequestParams());
                    if (eventListResponse.getCode() == APIResponse.CODE.WEBLOAD || eventListResponse.getCode() == APIResponse.CODE.UPDATED) {
                        if (eventListResponse.getData() == null || eventListResponse.getData().isEmpty()) {
                            onConnectionError();
                            return null;
                        }
                        try {
                            JsonElement responseObject = JSONManager.getParser().parse(eventListResponse.getData());
                            if (responseObject instanceof JsonObject) {
                                if (((JsonObject) responseObject).has("404")) {
                                    // No events found for that year; skip it
                                    continue;
                                }
                            }
                        } catch (JsonSyntaxException ex) {
                            Log.w(Constants.LOG_TAG, "Couldn't parse bad json: " + eventListResponse.getData());
                            continue;
                        }

                        ArrayList<Event> yearEvents = TBAv2.getEventList(eventListResponse.getData());
                        events.addAll(yearEvents);
                    }
                }
            }

            if (Arrays.binarySearch(dataToLoad, LaunchActivity.LoadAllDataTaskFragment.LOAD_DISTRICTS) != -1) {
                //load all districts
                for (int year = Constants.FIRST_DISTRICT_YEAR; year <= Constants.MAX_COMP_YEAR; year++) {
                    if (isCancelled()) {
                        return null;
                    }
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, String.format(context.getString(R.string.loading_districts), year)));
                    APIResponse<String> districtListResponse;
                    String url = String.format(TBAv2.getTBAApiUrl(context, TBAv2.QUERY.DISTRICT_LIST), year);
                    districtListResponse = TBAv2.getResponseFromURLOrThrow(context, url, new RequestParams());
                    if (districtListResponse.getData() == null) {
                        continue;
                    }
                    JsonElement responseObject = JSONManager.getParser().parse(districtListResponse.getData());
                    if (responseObject instanceof JsonObject) {
                        if (((JsonObject) responseObject).has("404")) {
                            // No events found for that year; skip it
                            continue;
                        }
                    }
                    ArrayList<District> yearDistricts = TBAv2.getDistrictList(districtListResponse.getData(), url, districtListResponse.getVersion());
                    districts.addAll(yearDistricts);
                }
            }

            if (isCancelled()) {
                return null;
            }
            // If no exception has been thrown at this point, we have all the data. We can now
            // insert it into the database.
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, context.getString(R.string.loading_almost_finished)));
            Log.d(Constants.LOG_TAG, "storing teams");
            Database.getInstance(context).getTeamsTable().add(teams);
            Log.d(Constants.LOG_TAG, "storing events");
            Database.getInstance(context).getEventsTable().add(events);
            Log.d(Constants.LOG_TAG, "storing districts");
            Database.getInstance(context).getDistrictsTable().add(districts);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            // Loop through all pages
            for (int pageNum = 0; pageNum <= maxPageNum; pageNum++) {
                editor.putBoolean(DataManager.Teams.ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE + pageNum, true);
            }
            // Loop through all years
            for (int year = Constants.FIRST_COMP_YEAR; year <= Constants.MAX_COMP_YEAR; year++) {
                editor.putBoolean(DataManager.Events.ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR + year, true);
            }
            // Loop through years for districts
            for (int year = Constants.FIRST_DISTRICT_YEAR; year <= Constants.MAX_COMP_YEAR; year++) {
                editor.putBoolean(DataManager.Districts.ALL_DISTRICTS_LOADED_TO_DATABASE_FOR_YEAR + year, true);
            }
            editor.putInt(LaunchActivity.APP_VERSION_KEY, BuildConfig.VERSION_CODE);
            editor.apply();
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_FINISHED, context.getString(R.string.loading_finished)));
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
            onConnectionError();
        } catch (Exception e) {
            // This is bad, probably an error in the response from the server
            e.printStackTrace();
            // Wipe any partially cached responses
            Database.getInstance(context).getResponseTable().deleteAllResponses();
            // Alert the user that there was a problem
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_ERROR, Utilities.exceptionStacktraceToString(e)));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (context != null) {
            AnalyticsHelper.sendTimingUpdate(context, System.currentTimeMillis() - startTime, "load all data", "");
        }
    }

    private void onConnectionError() {
        publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_NO_CONNECTION, context.getString(R.string.connection_lost)));
        // Wipe any partially cached responses
        Database.getInstance(context).getResponseTable().deleteAllResponses();
    }

    @Override
    protected void onProgressUpdate(LoadProgressInfo... values) {
        callbacks.onProgressUpdate(values[0]);
    }

    public class LoadProgressInfo {

        public static final int STATE_LOADING = 0;
        public static final int STATE_FINISHED = 1;
        public static final int STATE_NO_CONNECTION = 2;
        public static final int STATE_ERROR = 3;

        public int state = -1;
        public String message = "";

        public LoadProgressInfo(int state, String message) {
            this.state = state;
            this.message = message;
        }

    }

    public interface LoadAllDataCallbacks {
        public void onProgressUpdate(LoadProgressInfo info);
    }
}