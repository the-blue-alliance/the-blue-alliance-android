package com.thebluealliance.androidtest.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidtest.adapters.ListViewAdapter;
import com.thebluealliance.androidtest.datatypes.ListElement;
import com.thebluealliance.androidtest.datatypes.ListItem;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamList extends AsyncTask<String,String,String> {

    private Activity activity;
    private View view;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateTeamList(Activity activity, View view){
        this.activity = activity;
        this.view = view;

        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();
    }

    @Override
    protected String doInBackground(String... params) {
        //some more temp data
        teamKeys.add("frc1124");    teams.add(new ListElement("1124","frc1124"));
        teamKeys.add("frc177");     teams.add(new ListElement("177","frc177"));
        teamKeys.add("frc1114");    teams.add(new ListElement("1114","frc1114"));
        teamKeys.add("frc 254");    teams.add(new ListElement("254","frc254"));
        teamKeys.add("frc2056");    teams.add(new ListElement("2056","frc2056"));

        adapter = new ListViewAdapter(activity,teams,teamKeys);

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        ListView eventList = (ListView)view.findViewById(R.id.team_list);
        eventList.setAdapter(adapter);
    }
}
