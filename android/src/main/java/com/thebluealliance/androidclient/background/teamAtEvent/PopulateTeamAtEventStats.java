package com.thebluealliance.androidclient.background.teamAtEvent;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.LegacyRefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventStatsFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Stat;

import java.util.ArrayList;

/**
 * Created by phil on 7/16/14.
 */
public class PopulateTeamAtEventStats extends AsyncTask<String, Void, APIResponse.CODE> {

    TeamAtEventStatsFragment fragment;
    LegacyRefreshableHostActivity activity;
    ArrayList<ListItem> statsList;
    String teamKey, eventKey;
    RequestParams requestParams;
    private long startTime;

    public PopulateTeamAtEventStats(TeamAtEventStatsFragment fragment, RequestParams requestParams) {
        super();
        this.fragment = fragment;
        this.activity = (LegacyRefreshableHostActivity) fragment.getActivity();
        this.requestParams = requestParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        if (params.length != 2)
            throw new IllegalArgumentException("PopulateTeamAtEventStats must be constructed with teamKey, eventKey, recordString");
        teamKey = params[0];
        eventKey = params[1];

        APIResponse<JsonObject> statsResponse;
        try {
            statsResponse = DataManager.Events.getEventStats(activity, eventKey, teamKey, requestParams);
            JsonObject statData = statsResponse.getData();

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            statsList = new ArrayList<>();
            if (statData.has("opr")) {
                statsList.add(new LabelValueListItem(activity.getString(R.string.opr_no_colon), Stat.displayFormat.format(statData.get("opr").getAsDouble())));
            }
            if (statData.has("dpr")) {
                statsList.add(new LabelValueListItem(activity.getString(R.string.dpr_no_colon), Stat.displayFormat.format(statData.get("dpr").getAsDouble())));
            }
            if (statData.has("ccwm")) {
                statsList.add(new LabelValueListItem(activity.getString(R.string.ccwm_no_colon), Stat.displayFormat.format(statData.get("ccwm").getAsDouble())));
            }

        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "Unable to fetch stats data for " + teamKey + "@" + eventKey);
            return APIResponse.CODE.NODATA;
        }

        return statsResponse.getCode();
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        View view = fragment.getView();
        if (activity != null && view != null && code != APIResponse.CODE.NODATA) {
            ListViewAdapter adapter = new ListViewAdapter(activity, statsList);
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);
            // If the adapter has no children, display a generic "no data" message.
            // Otherwise, show the list as normal.
            if (adapter.isEmpty()) {
                noDataText.setText(R.string.no_team_stats_data);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                noDataText.setVisibility(View.GONE);
                ListView listView = (ListView) view.findViewById(R.id.list);
                listView.setVisibility(View.VISIBLE);
                // If the list hasn't previously been initialized, expand the "summary" view
                Parcelable state = listView.onSaveInstanceState();
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                listView.setAdapter(adapter);
                listView.onRestoreInstanceState(state);

                listView.setSelection(firstVisiblePosition);
                adapter.notifyDataSetChanged();
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                requestParams.forceFromCache = false;
                PopulateTeamAtEventStats secondTask = new PopulateTeamAtEventStats(fragment, requestParams);
                fragment.updateTask(secondTask);
                secondTask.execute(teamKey, eventKey);
            } else {
                // Show notification if we've refreshed data.
                Log.i(Constants.REFRESH_LOG, teamKey + "@" + eventKey + " refresh complete");
                if (activity != null && activity instanceof LegacyRefreshableHostActivity) {
                    activity.notifyRefreshComplete(fragment);
                }
            }
            AnalyticsHelper.sendTimingUpdate(activity, System.currentTimeMillis() - startTime, "team@event stats", teamKey + "@" + eventKey);
        }
    }
}
