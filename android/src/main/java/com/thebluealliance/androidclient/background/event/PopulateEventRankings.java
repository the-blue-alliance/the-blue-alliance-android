package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.event.EventRankingsFragment;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.RankingListElement;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Retrieves event rankings for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *         <p/>
 *         File created by phil on 4/23/14.
 */
public class PopulateEventRankings extends AsyncTask<String, Void, APIResponse.CODE> {

    private EventRankingsFragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey;
    private ArrayList<ListItem> teams;
    private boolean forceFromCache;

    private ListViewAdapter adapter;

    public PopulateEventRankings(EventRankingsFragment f, boolean forceFromCache) {
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
            APIResponse<ArrayList<JsonArray>> response = DataManager.Events.getEventRankings(activity, eventKey, forceFromCache);
            ArrayList<JsonArray> rankList = response.getData();
            if(isCancelled()){
                return APIResponse.CODE.NODATA;
            }
            if (!rankList.isEmpty()) {
                JsonArray headerRow = rankList.remove(0);
                for (JsonArray row : rankList) {
                /* Assume that the list of lists has rank first
                 * and team # second, always
                 */
                    String teamKey = "frc" + row.get(1).getAsString();
                    String rankingString = "";
                    CaseInsensitiveMap<String> rankingElements = new CaseInsensitiveMap<>();
                    for (int i = 2; i < row.size(); i++) {
                        rankingElements.put(headerRow.get(i).getAsString(), row.get(i).getAsString());
                    }
                    String record = null;
                    // Find if the rankings contain a record; remove it if it does
                    Iterator it = rankingElements.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Object> entry = (Map.Entry) it.next();
                        if (entry.getKey().toLowerCase().contains("record".toLowerCase())) {
                            record = "(" + rankingElements.get(entry.getKey()) + ")";
                            it.remove();
                            break;
                        }
                    }
                    if (record == null) {
                        Set<String> keys = rankingElements.keySet();
                        if (keys.contains("wins") && keys.contains("losses") && keys.contains("ties")) {
                            record = "(" + rankingElements.get("wins") + "-" + rankingElements.get("losses") + "-" + rankingElements.get("ties") + ")";
                            rankingElements.remove("wins");
                            rankingElements.remove("losses");
                            rankingElements.remove("ties");
                        }
                    }
                    if (record == null) {
                        record = "";
                    }
                    // Construct rankings string
                    it = rankingElements.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        String value = entry.getValue().toString();
                        // If we have a number like 235.00, remove the useless .00 so it looks cleaner
                        if (value.contains(".00")) {
                            value = value.replace(".00", "");
                        }
                        rankingString += entry.getKey() + ": " + value;
                        if (it.hasNext()) {
                            rankingString += ", ";
                        }
                    }
                    Team team = DataManager.Teams.getTeamFromDB(activity, teamKey);
                    teams.add(new RankingListElement(teamKey, row.get(1).getAsInt(), team.getNickname(), row.get(0).getAsInt(), record, rankingString));
                }
                adapter = new ListViewAdapter(activity, teams);
                return response.getCode();
            } else {
                return APIResponse.CODE.NODATA;
            }
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event rankings");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && activity != null) {
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no rankings in the adapter or if we can't download info
            // off the web, display a message.
            if (code == APIResponse.CODE.NODATA || adapter.values.isEmpty()) {
                noDataText.setText(R.string.no_ranking_data);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView rankings = (ListView) view.findViewById(R.id.list);
                Parcelable state = rankings.onSaveInstanceState();
                rankings.setAdapter(adapter);
                rankings.onRestoreInstanceState(state);
            }

            // Display a warning if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            // Remove progress indicator and show content since we're done loading data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);
        }

        if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
            /**
             * The data has the possibility of being updated, but we at first loaded
             * what we have cached locally for performance reasons.
             * Thus, fire off this task again with a flag saying to actually load from the web
             */
            PopulateEventRankings secondLoad = new PopulateEventRankings(mFragment, false);
            mFragment.updateTask(secondLoad);
            secondLoad.execute(eventKey);
        } else {
            // Show notification if we've refreshed data.
            if (mFragment instanceof RefreshListener) {
                Log.d(Constants.LOG_TAG, "Event Rankings refresh complete");
                activity.notifyRefreshComplete(mFragment);
            }
        }
    }

    private class CaseInsensitiveMap<K> extends HashMap<String, K> {

        @Override
        public K put(String key, K value) {
            return super.put(key.toLowerCase(), value);
        }

        public K get(String key) {
            return super.get(key.toLowerCase());
        }

        public boolean contains(String key) {
            return get(key) != null;
        }
    }
}
