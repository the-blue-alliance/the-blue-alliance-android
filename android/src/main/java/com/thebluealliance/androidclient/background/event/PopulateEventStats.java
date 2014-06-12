package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.TeamSortByOPRComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.StatsListElement;
import com.thebluealliance.androidclient.models.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Retrieves performance statistics on teams competing at an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *
 * File created by phil on 4/23/14.
 */
public class PopulateEventStats extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey;
    private ArrayList<ListItem> teams;

    public PopulateEventStats(Fragment f) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        teams = new ArrayList<>();

        try {
            // Retrieve the data
            APIResponse<JsonObject> response = DataManager.getEventStats(activity, eventKey);
            JsonObject stats = response.getData();
            ArrayList<Map.Entry<String, JsonElement>>
                    opr = new ArrayList<>(),
                    dpr = new ArrayList<>(),
                    ccwm = new ArrayList<>();

            LinkedHashMap<String, Double>
                       dprSorted = new LinkedHashMap<>(),
                       ccwmSorted = new LinkedHashMap<>();

            // Put each stat into its own array list,
            // but make sure it actually has stats (and not just an empty set).
            if (stats.has("oprs") &&
               !stats.get("oprs").getAsJsonObject().entrySet().isEmpty()) {
                opr.addAll(stats.get("oprs").getAsJsonObject().entrySet());

                // Sort OPRs in decreasing order (highest to lowest)
                Collections.sort(opr, new TeamSortByOPRComparator());
                Collections.reverse(opr);
            }

            // Put the DPRs & CCWMs into a linked hashmap in the same order as the sorted OPRs.
            if (stats.has("dprs") &&
               !stats.get("dprs").getAsJsonObject().entrySet().isEmpty()) {

                dpr.addAll(stats.get("dprs").getAsJsonObject().entrySet());

                for (int i = 0; i < opr.size(); i++){
                  String dprKey = opr.get(i).getKey();
                  Double dprValue = stats.get("dprs").getAsJsonObject().get(dprKey).getAsDouble();
                  dprSorted.put(dprKey, dprValue);
                }

            }

            if (stats.has("ccwms") &&
               !stats.get("ccwms").getAsJsonObject().entrySet().isEmpty()) {

                ccwm.addAll(stats.get("ccwms").getAsJsonObject().entrySet());

                for (int i = 0; i < opr.size(); i++){
                    String ccwmKey = opr.get(i).getKey();
                    Double ccwmValue = stats.get("ccwms").getAsJsonObject().get(ccwmKey).getAsDouble();
                    ccwmSorted.put(ccwmKey, ccwmValue);
                }

            }

            // Combine the stats into one string to be displayed onscreen.
            for (int i = 0; i < opr.size(); i++) {
                String statsString = activity.getString(R.string.opr)+" " + Stat.displayFormat.format(opr.get(i).getValue().getAsDouble())
                        + ", "+activity.getString(R.string.dpr)+" " + Stat.displayFormat.format(dprSorted.values().toArray()[i])
                        + ", "+activity.getString(R.string.ccwm)+" " + Stat.displayFormat.format(ccwmSorted.values().toArray()[i]);
                String teamKey = "frc" + opr.get(i).getKey();
                teams.add(new StatsListElement(teamKey, Integer.parseInt(opr.get(i).getKey()), "", "", statsString));
                //TODO the blank fields above are team name and location
            }

            return response.getCode();
        } catch (DataManager.NoDataException e) {
            // Return an error, since we can't get the data for some reason.
            Log.w(Constants.LOG_TAG, "unable to load event stats");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && activity != null) {
            // Set the new info.
            ListViewAdapter adapter = new ListViewAdapter(activity, teams);
            ListView stats = (ListView) view.findViewById(R.id.list);
            stats.setAdapter(adapter);

            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no stats in the adapter or if we can't download info
            // off the web, display a message.
            if (code == APIResponse.CODE.NODATA || adapter.values.isEmpty())
            {
                noDataText.setText(R.string.no_stats_data);
                noDataText.setVisibility(View.VISIBLE);
            }

            // Display warning if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            // Remove progress spinner, since we're done loading the data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
}
