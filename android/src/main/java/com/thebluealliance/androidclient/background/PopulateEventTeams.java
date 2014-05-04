package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
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

    private Activity activity;
    private View view;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;
    private String eventKey;

    public PopulateEventTeams(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    protected String doInBackground(String... params) {
        eventKey = params[0];
        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();

        Log.d("load event teams: ", "event key: " + eventKey);
        try {
            ArrayList<Team> teamList = DataManager.getEventTeams(activity,eventKey);
            Collections.sort(teamList, new TeamSortByNumberComparator());
            for(Team t:teamList){
                teamKeys.add(t.getTeamKey());
                teams.add(t.render());
            }
        } catch (DataManager.NoDataException e) {
            e.printStackTrace();
        }

        adapter = new ListViewAdapter(activity, teams, teamKeys);
        adapter.notifyDataSetChanged();
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        ListView teamList = (ListView) view.findViewById(R.id.event_team_list);
        teamList.setAdapter(adapter);
    }
}
