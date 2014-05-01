package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.TeamListElement;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamList extends AsyncTask<Integer, String, Void> {

    private Fragment fragment;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teams;
    private ListViewAdapter adapter;

    public PopulateTeamList(Fragment fragment) {
        this.fragment = fragment;

        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();
    }

    @Override
    protected Void doInBackground(Integer... params) {
        int start = params[0];
        int end = params[1];
        //some more temp data

        teamKeys = new ArrayList<String>();
        teams = new ArrayList<ListItem>();
        if (start >= 0 && end < 1000) {
            teamKeys.add("frc177");
            teams.add(new TeamListElement("frc177", 177, "Bobcat Robotics", "South Windsor, CT"));
            teamKeys.add("frc254");
            teams.add(new TeamListElement("frc254", 254, "Teh Chezy Pofs", "San Jose, CA"));
        }
        if (start >= 1000 && end < 2000) {
            teamKeys.add("frc1114");
            teams.add(new TeamListElement("frc1114", 1114, "Simbotics", "St. Catharines, ON"));
            teamKeys.add("frc1124");
            teams.add(new TeamListElement("frc1124", 1124, "The UberBots", "Avon, CT"));
        }
        if (start >= 2000 && end < 3000) {
            teamKeys.add("frc2056");
            teams.add(new TeamListElement("frc2056", 2056, "OP Robotics", "Stoney Creek, ON"));
        }

        adapter = new ListViewAdapter(fragment.getActivity(), teams, teamKeys);

        adapter.notifyDataSetChanged();
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        ListView eventList = (ListView) fragment.getView().findViewById(R.id.team_list);
        eventList.setAdapter(adapter);
    }
}
