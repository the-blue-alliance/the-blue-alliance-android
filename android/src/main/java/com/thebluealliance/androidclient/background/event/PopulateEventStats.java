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
import com.thebluealliance.androidclient.adapters.EventStatsFragmentAdapter;
import com.thebluealliance.androidclient.comparators.TeamSortByAlphanumComparator;
import com.thebluealliance.androidclient.comparators.TeamSortByStatComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
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
    private RequestParams requestParams;
    private String statToSortBy;
    private EventStatsFragmentAdapter adapter;

    public PopulateEventStats(EventStatsFragment f, RequestParams requestParams, String statToSortBy) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.requestParams = requestParams;
        this.statToSortBy = statToSortBy;
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        teams = new ArrayList<>();

        try {
            // Retrieve the data
            APIResponse<JsonObject> response = DataManager.Events.getEventStats(activity, eventKey, requestParams);
            JsonObject stats = response.getData();

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            ArrayList<Map.Entry<String, JsonElement>>
                    opr = new ArrayList<>(),
                    dpr = new ArrayList<>(),
                    ccwm = new ArrayList<>(),
                    statToUse = new ArrayList<>();
            // to get the total size of the stats list elements, take the size of the stat array list being sorted.

            LinkedHashMap<String, Double>
                    oprSorted = new LinkedHashMap<>(),
                    dprSorted = new LinkedHashMap<>(),
                    ccwmSorted = new LinkedHashMap<>();

            // Default to OPR on first startup.
            // Also putting team sort in here since sorting using another comparator makes life easier
            // rather than creating a different list for teams which doesn't have the same properties as the stats.
            if (statToSortBy == null || statToSortBy.equals("opr") || statToSortBy.equals("team")) {
                if (stats.has("oprs") && !stats.get("oprs").getAsJsonObject().entrySet().isEmpty()) {

                    opr.addAll(stats.get("oprs").getAsJsonObject().entrySet());
                    statToUse = opr;

                    if (statToSortBy == null || statToSortBy.equals("opr")) {
                        // Sort OPRs in decreasing order (highest to lowest)
                        Collections.sort(opr, new TeamSortByStatComparator());
                        Collections.reverse(opr);
                    } else if (statToSortBy.equals("team")) {
                        Collections.sort(opr, new TeamSortByAlphanumComparator());
                    }

                    oprSorted = sortedListByStat(opr, stats.get("oprs").getAsJsonObject());

                    // Put the DPRs & CCWMs into a linked hashmap in the same order as the sorted OPRs.
                    if (stats.has("dprs") && !stats.get("dprs").getAsJsonObject().entrySet().isEmpty()) {
                        dprSorted = sortedListByStat(opr, stats.get("dprs").getAsJsonObject());
                    }

                    if (stats.has("ccwms") && !stats.get("ccwms").getAsJsonObject().entrySet().isEmpty()) {
                        ccwmSorted = sortedListByStat(opr, stats.get("ccwms").getAsJsonObject());
                    }
                }
            } else if (statToSortBy.equals("dpr")) {
                if (stats.has("dprs") && !stats.get("dprs").getAsJsonObject().entrySet().isEmpty()) {

                    dpr.addAll(stats.get("dprs").getAsJsonObject().entrySet());
                    statToUse = dpr;

                    // Sort DPRs in increasing order (lowest to highest)
                    Collections.sort(dpr, new TeamSortByStatComparator());

                    dprSorted = sortedListByStat(dpr, stats.get("dprs").getAsJsonObject());

                    // Put the OPRs & CCWMs into a linked hashmap in the same order as the sorted DPRs.
                    if (stats.has("oprs") && !stats.get("oprs").getAsJsonObject().entrySet().isEmpty()) {
                        oprSorted = sortedListByStat(dpr, stats.get("oprs").getAsJsonObject());
                    }

                    if (stats.has("ccwms") && !stats.get("ccwms").getAsJsonObject().entrySet().isEmpty()) {
                        ccwmSorted = sortedListByStat(dpr, stats.get("ccwms").getAsJsonObject());
                    }
                }
            } else if (statToSortBy.equals("ccwm")) {
                if (stats.has("ccwms") && !stats.get("ccwms").getAsJsonObject().entrySet().isEmpty()) {

                    ccwm.addAll(stats.get("ccwms").getAsJsonObject().entrySet());
                    statToUse = ccwm;

                    // Sort CCWMs in decreasing order (highest to lowest)
                    Collections.sort(ccwm, new TeamSortByStatComparator());
                    Collections.reverse(ccwm);

                    ccwmSorted = sortedListByStat(ccwm, stats.get("ccwms").getAsJsonObject());

                    // Put the OPRs & DPRs into a linked hashmap in the same order as the sorted CCWMs.
                    if (stats.has("oprs") && !stats.get("oprs").getAsJsonObject().entrySet().isEmpty()) {
                        oprSorted = sortedListByStat(ccwm, stats.get("oprs").getAsJsonObject());
                    }

                    if (stats.has("dprs") && !stats.get("dprs").getAsJsonObject().entrySet().isEmpty()) {
                        dprSorted = sortedListByStat(ccwm, stats.get("dprs").getAsJsonObject());
                    }
                }
            }

            // Combine the stats into one string to be displayed onscreen.
            for (int i = 0; i < statToUse.size(); i++) {
                String statsString = activity.getString(R.string.opr) + " " + Stat.displayFormat.format(oprSorted.values().toArray()[i])
                        + ", " + activity.getString(R.string.dpr) + " " + Stat.displayFormat.format(dprSorted.values().toArray()[i])
                        + ", " + activity.getString(R.string.ccwm) + " " + Stat.displayFormat.format(ccwmSorted.values().toArray()[i]);


                String teamKey = "frc" + statToUse.get(i).getKey();

                // We might get a multi-team key from offseason events.
                // If so, take out the extra letter at the end to prevent NPE.
                if (TeamHelper.validateMultiTeamKey(teamKey)) {
                    teamKey = teamKey.substring(0, teamKey.length() - 1);
                }
                Team team = DataManager.Teams.getTeamFromDB(activity, teamKey);
                String nickname;
                if(team == null){
                    nickname = "Team "+teamKey.substring(3);
                }else{
                    nickname = team.getNickname();
                }
                teams.add(new StatsListElement(teamKey, statToUse.get(i).getKey(), nickname, statsString,
                        Double.valueOf(oprSorted.values().toArray()[i].toString()),
                        Double.valueOf(dprSorted.values().toArray()[i].toString()),
                        Double.valueOf(ccwmSorted.values().toArray()[i].toString())));

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
            adapter = new EventStatsFragmentAdapter(activity, teams);
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
                noDataText.setVisibility(View.GONE);
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
            requestParams.forceFromCache = false;
            PopulateEventStats secondLoad = new PopulateEventStats(mFragment, requestParams, statToSortBy);
            mFragment.updateTask(secondLoad);
            secondLoad.execute(eventKey);
        } else {
            // Show notification if we've refreshed data.
            if (activity != null && mFragment instanceof RefreshListener) {
                Log.d(Constants.REFRESH_LOG, "Event  " + eventKey + " Stats refresh complete");
                activity.notifyRefreshComplete(mFragment);
            }
        }
    }

    /**
     * Creates a new linked hash map for a stat based on the order of another sorted stat.
     *
     * @param stat the stat to sort by
     * @param data the data to sort with
     * @return newly sorted linked hash map
     */
    private LinkedHashMap<String, Double> sortedListByStat(ArrayList<Map.Entry<String, JsonElement>> stat, JsonObject data) {

        LinkedHashMap<String, Double> statSorted = new LinkedHashMap<>();

        for (int i = 0; i < stat.size(); i++) {
            String newKey = stat.get(i).getKey();
            Double newValue = data.get(newKey).getAsDouble();
            statSorted.put(newKey, newValue);
        }

        return statSorted;
    }
}
