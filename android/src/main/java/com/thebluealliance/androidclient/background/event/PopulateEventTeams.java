package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.TeamSortByNumberComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Retrieves team list for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *
 * File created by phil on 4/22/14.
 */
public class PopulateEventTeams extends AsyncTask<String, String, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private ArrayList<ListItem> teams;
    private String eventKey;

    public PopulateEventTeams(Fragment f) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        teams = new ArrayList<>();

        Log.d("load event teams: ", "event key: " + eventKey);
        try {
            APIResponse<ArrayList<Team>> response = DataManager.getEventTeams(activity, eventKey);
            ArrayList<Team> teamList = response.getData();
            Collections.sort(teamList, new TeamSortByNumberComparator());
            for (Team t : teamList) {
                teams.add(t.render(true));
            }
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
            ListViewAdapter adapter = new ListViewAdapter(activity, teams);
            adapter.notifyDataSetChanged();
            ListView teamList = (ListView) view.findViewById(R.id.list);
            teamList.setAdapter(adapter);

            TextView noDataText = (TextView) view.findViewById(R.id.no_data);

            // If there's no awards in the adapter or if we can't download info
            // off the web, display a message.
            if (code == APIResponse.CODE.NODATA || adapter.values.isEmpty())
            {
                noDataText.setText(R.string.no_team_data);
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
