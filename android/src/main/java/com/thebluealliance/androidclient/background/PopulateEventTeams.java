package com.thebluealliance.androidclient.background;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.BaseActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.TeamSortByNumberComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventTeams extends AsyncTask<String, String, APIResponse.CODE> implements AdapterView.OnItemClickListener {

    private Fragment mFragment;
    private BaseActivity activity;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;
    private String eventKey;

    public PopulateEventTeams(Fragment f) {
        mFragment = f;
        activity = (BaseActivity)mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];
        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();

        Log.d("load event teams: ", "event key: " + eventKey);
        try {
            APIResponse<ArrayList<Team>> response = DataManager.getEventTeams(activity, eventKey);
            ArrayList<Team> teamList = response.getData();
            Collections.sort(teamList, new TeamSortByNumberComparator());
            for (Team t : teamList) {
                teamKeys.add(t.getTeamKey());
                teams.add(t.render());
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event teams");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE c) {
        super.onPostExecute(c);
        View view = mFragment.getView();
        if (view != null && mFragment.getActivity() != null) {
            adapter = new ListViewAdapter(mFragment.getActivity(), teams, teamKeys);
            adapter.notifyDataSetChanged();
            //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
            adapter = new ListViewAdapter(mFragment.getActivity(), teams, teamKeys);
            adapter.notifyDataSetChanged();
            ListView teamList = (ListView) view.findViewById(R.id.event_team_list);
            teamList.setAdapter(adapter);
            teamList.setOnItemClickListener(this);

            if(c == APIResponse.CODE.OFFLINECACHE /* && event is current */){
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = ViewTeamActivity.newInstance(activity,view.getTag().toString());
        mFragment.startActivity(intent);
    }
}
