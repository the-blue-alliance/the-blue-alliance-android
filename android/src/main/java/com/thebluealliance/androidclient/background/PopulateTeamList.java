package com.thebluealliance.androidclient.background;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datatypes.ListElement;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.TeamListElement;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamList extends AsyncTask<Integer,String,String> {

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
    protected String doInBackground(Integer... params) {
        int start = params[0] * 1000;
        //some more temp data

        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();
        switch (start){
            default:case 0:
                teamKeys.add("frc177");     teams.add(new TeamListElement("frc177","177","Bobcat Robotics","South Windsor, CT"));
                teamKeys.add("frc 254");    teams.add(new TeamListElement("frc254","254","Teh Chezy Pofs","San Jose, CA"));
                break;
            case 1000:
                teamKeys.add("frc1114");    teams.add(new TeamListElement("frc1114","1114","Simbotics","St. Catharines, ON"));
                teamKeys.add("frc1124");    teams.add(new TeamListElement("frc1124","1124","The UberBots","Avon, CT"));
                break;
            case 2000:
                teamKeys.add("frc2056");    teams.add(new TeamListElement("frc2056","2056", "OP Robotics","Stoney Creek, ON"));
                break;
        }

        adapter = new ListViewAdapter(activity,teams,teamKeys);
        adapter.notifyDataSetChanged();
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
