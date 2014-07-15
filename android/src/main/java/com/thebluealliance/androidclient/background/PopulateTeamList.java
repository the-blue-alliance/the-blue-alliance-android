package com.thebluealliance.androidclient.background;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.TeamCursorAdapter;
import com.thebluealliance.androidclient.datafeed.APIResponse;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.fragments.TeamListFragment;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.ListItem;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamList extends AsyncTask<Integer, String, APIResponse.CODE> {

    private TeamListFragment fragment;
    private boolean forceFromCache;
    private int start, end;

    private RefreshableHostActivity activity;
    private ArrayList<ListItem> teamItems;
    private Cursor teams;

    public PopulateTeamList(TeamListFragment fragment, boolean forceFromCache) {
        this.fragment = fragment;
        activity = (RefreshableHostActivity) fragment.getActivity();
        this.forceFromCache = forceFromCache;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showMenuProgressBar();
        teamItems = new ArrayList<>();
    }

    @Override
    protected APIResponse.CODE doInBackground(Integer... params) {
        start = params[0];
        end = params[1];
        Log.d("doInBackground", "is cancelled? " + isCancelled());
        APIResponse<Cursor> response = new APIResponse<>(null, APIResponse.CODE.NODATA);
        if (!isCancelled()) {
            try {
                response = DataManager.Teams.getCursorForTeamsInRange(activity, start, end);
                teams = response.getData();
            } catch (Exception e) {
                Log.w(Constants.LOG_TAG, "unable to load team list");
            }
        }
        return response.getCode();
    }


    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        super.onPostExecute(code);

        View view = fragment.getView();
        if (activity != null && view != null) {
            TeamCursorAdapter adapter = new TeamCursorAdapter(activity, teams, 0);
            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no teams in the adapter or if we can't download info
            // off the web, display a message.
            if (code == APIResponse.CODE.NODATA || teams == null || !teams.moveToFirst()) {
                noDataText.setText(R.string.no_team_list);
                noDataText.setVisibility(View.VISIBLE);
            } else {
                ListView eventList = (ListView) view.findViewById(R.id.list);
                Parcelable state = eventList.onSaveInstanceState();
                eventList.setAdapter(adapter);
                eventList.onRestoreInstanceState(state);
            }

            if (code == APIResponse.CODE.OFFLINECACHE) {
                activity.showWarningMessage(fragment.getString(R.string.warning_using_cached_data));
            }
            view.findViewById(R.id.progress).setVisibility(View.GONE);
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);

            if (code == APIResponse.CODE.LOCAL && !isCancelled()) {
                /**
                 * The data has the possibility of being updated, but we at first loaded
                 * what we have cached locally for performance reasons.
                 * Thus, fire off this task again with a flag saying to actually load from the web
                 */
                PopulateTeamList secondLoad = new PopulateTeamList(fragment, false);
                fragment.updateTask(secondLoad);
                secondLoad.execute(start, end);
            } else {
                // Show notification if we've refreshed data.
                Log.i(Constants.REFRESH_LOG, "Team list " + start + " - " + end + " refresh complete");
                if (fragment instanceof RefreshListener) {
                    activity.notifyRefreshComplete(fragment);
                }
            }
        }
    }
}
