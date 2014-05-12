package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.comparators.TeamSortByNumberComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.models.Team;

import java.util.ArrayList;
import java.util.Collections;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventTeams extends AsyncTask<String, String, String> {

    private Fragment mFragment;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;
    private String eventKey;

    public PopulateEventTeams(Fragment f) {
        mFragment = f;
    }

    @Override
    protected String doInBackground(String... params) {
        eventKey = params[0];
        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();

        Log.d("load event teams: ", "event key: " + eventKey);
        try {
            ArrayList<Team> teamList = DataManager.getEventTeams(mFragment.getActivity(), eventKey);
            Collections.sort(teamList, new TeamSortByNumberComparator());
            for (Team t : teamList) {
                teamKeys.add(t.getTeamKey());
                teams.add(t.render());
            }
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
        }

        adapter = new ListViewAdapter(mFragment.getActivity(), teams, teamKeys);
        adapter.notifyDataSetChanged();
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        View view = mFragment.getView();
        if (view != null) {
            //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
            ListView teamList = (ListView) view.findViewById(R.id.event_team_list);
            teamList.setAdapter(adapter);
        }
    }
}
