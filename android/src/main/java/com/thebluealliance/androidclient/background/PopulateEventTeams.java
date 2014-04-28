package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.TeamListElement;

import java.util.ArrayList;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventTeams extends AsyncTask<String, String, String> {

    private Activity activity;
    private View view;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventTeams(Activity activity, View view) {
        this.activity = activity;
        this.view = view;

        //generate the teams attending data

    }

    @Override
    protected String doInBackground(String... params) {
        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();

        //put some static data here
        teamKeys.add("frc281");
        teams.add(new TeamListElement("frc281", 281, "EnTech Green Villains", "Greenville, SC"));
        teamKeys.add("frc342");
        teams.add(new TeamListElement("frc342", 342, "Burning Magnetos", "North Charleston, SC"));
        teamKeys.add("frc343");
        teams.add(new TeamListElement("frc343", 343, "Metal-In-Motion", "Seneca, SC"));
        teamKeys.add("frc346");
        teams.add(new TeamListElement("frc346", 346, "RoboHawks", "Chesterfield, VA"));

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
