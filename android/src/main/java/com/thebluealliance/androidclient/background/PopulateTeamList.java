package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.TeamListElement;
import com.thebluealliance.androidclient.models.SimpleTeam;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamList extends AsyncTask<Integer, String, Void> {

    private Fragment fragment;
    private ArrayList<String> teamKeys;
    private ArrayList<ListItem> teamItems;
    private ListViewAdapter adapter;

    public PopulateTeamList(Fragment fragment) {
        this.fragment = fragment;

        teamKeys = new ArrayList<String>();
        teamItems = new ArrayList<ListItem>();
    }

    @Override
    protected Void doInBackground(Integer... params) {
        int start = params[0];
        int end = params[1];
        Log.d("doInBackground", "is cancelled? " + isCancelled());
        if (!isCancelled()) {
            try {
                ArrayList<SimpleTeam> teams = DataManager.getSimpleTeamsInRange(fragment.getActivity(), start, end);
                for (SimpleTeam team : teams) {
                    if (isCancelled()) {
                        break;
                    }
                    TeamListElement e = team.render();
                    teamKeys.add(e.getKey());
                    teamItems.add(e);
                }
            } catch (Exception e) {
                teamKeys.add("frc2056");
                teamItems.add(new TeamListElement("frc2056", 2056, "OP Robotics", "Stoney Creek, ON"));
                e.printStackTrace();
            }
        }
        if (!isCancelled()) {
            adapter = new ListViewAdapter(fragment.getActivity(), teamItems, teamKeys);

            adapter.notifyDataSetChanged();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);

        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        if (fragment.getView() != null) {
            ListView eventList = (ListView) fragment.getView().findViewById(R.id.team_list);
            eventList.setAdapter(adapter);
        }
    }
}
