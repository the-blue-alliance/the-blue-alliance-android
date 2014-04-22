package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEvent;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datatypes.ListElement;
import com.thebluealliance.androidclient.datatypes.ListItem;

import java.util.ArrayList;

/**
 * File created by phil on 4/22/14.
 */
public class PopulateEventTeams extends AsyncTask<String,String,String> {

    private Activity activity;
    private View view;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateEventTeams(Activity activity,View view){
        this.activity = activity;
        this.view = view;

        //generate the teams attending data

    }

    @Override
    protected String doInBackground(String... params) {
        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();

        //put some static data here
        teamKeys.add("frc281");     teams.add(new ListElement("281 - EnTech Green Villains","frc281"));
        teamKeys.add("frc342");     teams.add(new ListElement("342 - Burning Magnetos","frc342"));
        teamKeys.add("frc343");     teams.add(new ListElement("343 - Metal-In-Motion","frc343"));
        teamKeys.add("frc346");     teams.add(new ListElement("346 - RoboHawks","frc346"));

        adapter = new ListViewAdapter(activity,teams,teamKeys);
        adapter.notifyDataSetChanged();
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        ListView teamList = (ListView)view.findViewById(R.id.event_team_list);
        teamList.setAdapter(adapter);
    }
}
