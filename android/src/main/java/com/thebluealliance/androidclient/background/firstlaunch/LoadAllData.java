package com.thebluealliance.androidclient.background.firstlaunch;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.CSVManager;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.datafeed.TBAv2;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.fragments.firstlaunch.LoadingFragment;
import com.thebluealliance.androidclient.models.SimpleEvent;
import com.thebluealliance.androidclient.models.SimpleTeam;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * File created by phil on 4/20/14.
 */
public class LoadAllData extends AsyncTask<Void, LoadAllData.LoadProgressInfo, Void> {

    private LoadingFragment mFragment;
    private Context c;

    public LoadAllData(LoadingFragment fragment) {
        mFragment = fragment;
        c = mFragment.getActivity();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (mFragment == null) {
            throw new IllegalArgumentException("Fragment must not be null!");
        }

        /* We need to download and cache every team and event into the database. To avoid
         * unexpected behavior caused by changes in network connectivity, we will load all
         * teams into memory first. Once we have loaded everything, only then will we wipe the
         * database and insert all the new teams and events.        *
         */
        try {
            ArrayList<SimpleTeam> teams;
            ArrayList<SimpleEvent> events = new ArrayList();

            // First we will load all the teams
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, "Teams"));
            APIResponse<String> response;
            final String URL = "http://www.thebluealliance.com/api/csv/teams/all?X-TBA-App-Id=" + Constants.getApiHeader();
            response = TBAv2.getResponseFromURLOrThrow(c, URL, true);
            Log.d("get simple teams", "starting parse");
            teams = CSVManager.parseTeamsFromCSV(response.getData());
            Log.d("get simple teams", "ending parse");

            // Now we load all events
            for (int year = Constants.FIRST_COMP_YEAR; year < Calendar.getInstance().get(Calendar.YEAR) + 1; year++) {
                publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_LOADING, year + " Events"));
                APIResponse<String> eventListResponse;
                eventListResponse = TBAv2.getResponseFromURLOrThrow(c, "http://thebluealliance.com/api/v2/events/" + year, true);
                JsonElement responseObject = new JsonParser().parse(eventListResponse.getData());
                if (responseObject instanceof JsonObject) {
                    if (((JsonObject) responseObject).has("404")) {
                        // No events found for that year; skip it
                        continue;
                    }
                }
                ArrayList<SimpleEvent> yearEvents = TBAv2.getEventList(eventListResponse.getData());
                events.addAll(yearEvents);
            }

            // If no exception has been thrown at this point, we have all the data. We can now
            // insert it into the database.
            Database.getInstance(c).storeTeams(teams);
            Database.getInstance(c).storeEvents(events);
            PreferenceManager.getDefaultSharedPreferences(c).edit()
                    .putBoolean(DataManager.ALL_TEAMS_LOADED_TO_DATABASE, true)
                    .putBoolean(DataManager.ALL_EVENTS_LOADED_TO_DATABASE, true).commit();
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_FINISHED, "Finished"));
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
            publishProgress(new LoadProgressInfo(LoadProgressInfo.STATE_NO_CONNECTION, "Internet connection lost."));
            // Wipe any partially cached responses
            Database.getInstance(c).deleteAllResponses();
        } catch (Exception e) {
            // This is bad, probably an error in the response from the server
            e.printStackTrace();
            // Wipe any partially cached responses
            Database.getInstance(c).deleteAllResponses();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(LoadProgressInfo... values) {
        mFragment.onLoadingProgressUpdate(values[0]);
    }

    public class LoadProgressInfo {

        public static final int STATE_LOADING = 0;
        public static final int STATE_FINISHED = 1;
        public static final int STATE_NO_CONNECTION = 2;

        public int state = -1;
        public String message = "";

        public LoadProgressInfo(int state, String message) {
            this.state = state;
            this.message = message;
        }

    }
}