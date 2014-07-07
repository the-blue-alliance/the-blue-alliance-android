package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.os.Parcelable;
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
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.event.EventStatsFragment;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.StatsListElement;
import com.thebluealliance.androidclient.models.Stat;
import com.thebluealliance.androidclient.models.Team;

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
 *         <p/>
 *         File created by phil on 4/23/14.
 */
public class PopulateEventStats extends AsyncTask<String, Void, APIResponse.CODE> {

    private EventStatsFragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey;
    private ArrayList<ListItem> teams;
    private boolean forceFromCache;

    public PopulateEventStats(EventStatsFragment f, boolean forceFromCache) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showMenuProgressBar();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        teams = new ArrayList<>();

        try {
            // Retrieve the data
            APIResponse<JsonObject> response = DataManager.Events.getEventStats(activity, eventKey, forceFromCache);
            JsonObject stats = response.getData();

            if(isCancelled()){
                return APIResponse.CODE.NODATA;
            }

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

                for (int i = 0; i < opr.size(); i++) {
                    String dprKey = opr.get(i).getKey();
                    Double dprValue = stats.get("dprs").getAsJsonObject().get(dprKey).getAsDouble();
                    dprSorted.put(dprKey, dprValue);
                }

            }

            if (stats.has("ccwms") &&
                    !stats.get("ccwms").getAsJsonObject().entrySet().isEmpty()) {

                ccwm.addAll(stats.get("ccwms").getAsJsonObject().entrySet());

                for (int i = 0; i < opr.size(); i++) {
                    String ccwmKey = opr.get(i).getKey();
                    Double ccwmValue = stats.get("ccwms").getAsJsonObject().get(ccwmKey).getAsDouble();
                    ccwmSorted.put(ccwmKey, ccwmValue);
                }

            }

            // Combine the stats into one string to be displayed onscreen.
            for (int i = 0; i < opr.size(); i++) {
                String statsString = activity.getString(R.string.opr) + " " + Stat.displayFormat.format(opr.get(i).getValue().getAsDouble())
                        + ", " + activity.getString(R.string.dpr) + " " + Stat.displayFormat.format(dprSorted.values().toArray()[i])
                        + ", " + activity.getString(R.string.ccwm) + " " + Stat.displayFormat.format(ccwmSorted.values().toArray()[i]);
                String teamKey = "frc" + opr.get(i).getKey();
                // We might get a multi-team key from offseason events.
                // If so, take out the extra letter at the end to prevent NPE.
                if (TeamHelper.validateMultiTeamKey(teamKey))
                {
                    teamKey = teamKey.substring(0, teamKey.length() - 1);
                }
                Team team = DataManager.Teams.getTeamFromDB(activity, teamKey);
                teams.add(new StatsListElement(teamKey, opr.get(i).getKey(), team.getNickname(), statsString));
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
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no stats in the adapter or if we can't download info
            // off the web, display a message.
            if (code == APIResponse.CODE.NODATA || adapter.values.isEmpty()) {
                noDataText.setText(R.string.no_stats_data);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView stats = (ListView) view.findViewById(R.id.list);
                Parcelable state = stats.onSaveInstanceState();
                stats.setAdapter(adapter);
                stats.onRestoreInstanceState(state);
            }

            // Display warning if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            // Remove progress spinner and show content, since we're done loading the data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);

        }

        if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
            /**
             * The data has the possibility of being updated, but we at first loaded
             * what we have cached locally for performance reasons.
             * Thus, fire off this task again with a flag saying to actually load from the web
             */
            PopulateEventStats secondLoad = new PopulateEventStats(mFragment, false);
            mFragment.updateTask(secondLoad);
            secondLoad.execute(eventKey);
        } else {
            // Show notification if we've refreshed data.
            if (mFragment instanceof RefreshListener) {
                Log.d(Constants.REFRESH_LOG, "Event  " + eventKey + " Stats refresh complete");
                activity.notifyRefreshComplete(mFragment);
            }
        }
    }
}
