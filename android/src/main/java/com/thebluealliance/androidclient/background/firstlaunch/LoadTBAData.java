package com.thebluealliance.androidclient.background.firstlaunch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Team;

import java.util.Arrays;
import java.util.List;

public class LoadTBAData extends AsyncTask<Short, LoadTBAData.LoadProgressInfo, Void> {

    public static final String DATA_TO_LOAD = "data_to_load";
    public static final short LOAD_TEAMS = 0,
            LOAD_EVENTS = 1,
            LOAD_DISTRICTS = 2;

    private CacheableDatafeed datafeed;
    private LoadTBADataCallbacks callbacks;
    private Context context;
    private long startTime;

    public LoadTBAData(CacheableDatafeed datafeed, LoadTBADataCallbacks callbacks, Context c) {
        this.datafeed = datafeed;
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
            dataToLoad = new Short[]{LOAD_TEAMS,
                    LOAD_EVENTS,
                    LOAD_DISTRICTS};
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
            int maxPageNum = 0;

            // First, wipe all relevant data from the database
            Database db = Database.getInstance(context);

            if (Arrays.binarySearch(dataToLoad, LOAD_TEAMS) != -1) {
                db.getTeamsTable().deleteAllRows();
                // First we will load all the teams
                for (int pageNum = 0; pageNum < 20; pageNum++) {  // limit to 20 pages to prevent potential infinite loop
                    if (isCancelled()) {
                        return null;
                    }
                    int start = pageNum * Constants.API_TEAM_LIST_PAGE_SIZE;
                    int end = start + Constants.API_TEAM_LIST_PAGE_SIZE - 1;
                    start = start == 0 ? 1 : start;
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, String.format(context.getString(R.string.loading_teams), start, end)));
                    List<Team> teamListResponse;
                    teamListResponse = datafeed.fetchTeamPage(pageNum, null).toBlocking().last();
                    if (teamListResponse != null && teamListResponse.size() == 0) {
                        // No teams found for a page; we are done
                        break;
                    }
                    maxPageNum = Math.max(maxPageNum, pageNum);
                }
            }

            if (Arrays.binarySearch(dataToLoad, LOAD_EVENTS) != -1) {
                db.getEventsTable().deleteAllRows();
                // Now we load all events
                for (int year = Constants.FIRST_COMP_YEAR; year <= Constants.MAX_COMP_YEAR; year++) {
                    if (isCancelled()) {
                        return null;
                    }
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, String.format(context.getString(R.string.loading_events), Integer.toString(year))));
                    List<Event> eventListResponse;
                    eventListResponse = datafeed.fetchEventsInYear(year, null).toBlocking().last();
                    if (eventListResponse == null) {
                        continue;
                    }
                    Log.i(Constants.LOG_TAG, String.format("Loaded %1$d events in %2$d", eventListResponse.size(), year));
                }
            }

            if (Arrays.binarySearch(dataToLoad, LOAD_DISTRICTS) != -1) {
                db.getDistrictsTable().deleteAllRows();
                //load all districts
                for (int year = Constants.FIRST_DISTRICT_YEAR; year <= Constants.MAX_COMP_YEAR; year++) {
                    if (isCancelled()) {
                        return null;
                    }
                    publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, String.format(context.getString(R.string.loading_districts), year)));
                    List<District> districtListResponse;
                    districtListResponse = datafeed.fetchDistrictList(year, null).toBlocking().last();
                    if (districtListResponse == null) {
                        continue;
                    }
                    Log.i(Constants.LOG_TAG, String.format("Loaded %1$d districts in %2$d", districtListResponse.size(), year));
                }
            }

            if (isCancelled()) {
                return null;
            }
            // If no exception has been thrown at this point, we have all the data. We can now
            // insert it into the database.
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, context.getString(R.string.loading_almost_finished)));


            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            // Loop through all pages
            for (int pageNum = 0; pageNum <= maxPageNum; pageNum++) {
                editor.putBoolean(Database.ALL_TEAMS_LOADED_TO_DATABASE_FOR_PAGE + pageNum, true);
            }
            // Loop through all years
            for (int year = Constants.FIRST_COMP_YEAR; year <= Constants.MAX_COMP_YEAR; year++) {
                editor.putBoolean(Database.ALL_EVENTS_LOADED_TO_DATABASE_FOR_YEAR + year, true);
            }
            // Loop through years for districts
            for (int year = Constants.FIRST_DISTRICT_YEAR; year <= Constants.MAX_COMP_YEAR; year++) {
                editor.putBoolean(Database.ALL_DISTRICTS_LOADED_TO_DATABASE_FOR_YEAR + year, true);
            }
            editor.putInt(Constants.APP_VERSION_KEY, BuildConfig.VERSION_CODE);
            editor.apply();
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_FINISHED, context.getString(R.string.loading_finished)));
        } catch (Exception e) {
            // This is bad, probably an error in the response from the server
            e.printStackTrace();
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

    public interface LoadTBADataCallbacks {
        public void onProgressUpdate(LoadProgressInfo info);
    }
}