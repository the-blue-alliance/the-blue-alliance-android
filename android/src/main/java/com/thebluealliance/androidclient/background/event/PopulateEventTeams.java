package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.TeamSortByNumberComparator;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.fragments.event.EventTeamsFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Retrieves team list for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *         <p/>
 *         File created by phil on 4/22/14.
 */
public class PopulateEventTeams extends AsyncTask<String, String, APIResponse.CODE> {

    private EventTeamsFragment mFragment;
    private RefreshableHostActivity activity;
    private ArrayList<ListItem> teams;
    private String eventKey;
    private RequestParams requestParams;
    private ListViewAdapter adapter;
    private long startTime;

    public PopulateEventTeams(EventTeamsFragment f, RequestParams requestParams) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
        this.requestParams = requestParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        Log.d("EventTeamsFragment", "doInBackground started");
        eventKey = params[0];

        teams = new ArrayList<>();

        try {
            APIResponse<ArrayList<Team>> response = DataManager.Events.getEventTeams(activity, eventKey, requestParams);
            ArrayList<Team> teamList = response.getData();

            if (isCancelled()) {
                return APIResponse.CODE.NODATA;
            }

            Collections.sort(teamList, new TeamSortByNumberComparator());
            for (Team t : teamList) {
                teams.add(t.render(true));
            }
            adapter = new ListViewAdapter(activity, teams);
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event teams");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);
        View view = mFragment.getView();
        if (view != null && activity != null) {
            //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no awards in the adapter or if we can't download info
            // off the web, display a message.
            if (code == APIResponse.CODE.NODATA || adapter.values.isEmpty()) {
                noDataText.setText(R.string.no_team_data);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView teamList = (ListView) view.findViewById(R.id.list);
                Parcelable state = teamList.onSaveInstanceState();
                teamList.setAdapter(adapter);
                noDataText.setVisibility(View.GONE);
                teamList.onRestoreInstanceState(state);
            }

            // Display warning if offline.
            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
            // Remove progress spinner and show content, since we're done loading the data.
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                requestParams.forceFromCache = false;
                PopulateEventTeams secondLoad = new PopulateEventTeams(mFragment, requestParams);
                mFragment.updateTask(secondLoad);
                secondLoad.execute(eventKey);
            } else {
                // Show notification if we've refreshed data.
                if (activity != null && mFragment instanceof RefreshListener) {
                    Log.d(Constants.REFRESH_LOG, "Event " + eventKey + " Teams refresh complete");
                    activity.notifyRefreshComplete(mFragment);
                }
            }
            AnalyticsHelper.sendTimingUpdate(activity, System.currentTimeMillis() - startTime, "event teams", eventKey);
        }
    }
}
