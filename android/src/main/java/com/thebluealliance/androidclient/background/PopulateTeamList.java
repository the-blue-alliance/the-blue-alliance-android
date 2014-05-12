package com.thebluealliance.androidclient.background;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.TeamListElement;
import com.thebluealliance.androidclient.models.SimpleTeam;

import java.util.ArrayList;

/**
 * File created by phil on 4/20/14.
 */
public class PopulateTeamList extends AsyncTask<Integer, String, APIResponse.CODE> {

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
    protected APIResponse.CODE doInBackground(Integer... params) {
        int start = params[0];
        int end = params[1];
        Log.d("doInBackground", "is cancelled? " + isCancelled());
        APIResponse<ArrayList<SimpleTeam>> response = new APIResponse<>(null, APIResponse.CODE.NODATA);
        if (!isCancelled()) {
            try {
                response = DataManager.getSimpleTeamsInRange(fragment.getActivity(), start, end);
                ArrayList<SimpleTeam> teams = response.getData();
                for (SimpleTeam team : teams) {
                    if (isCancelled()) {
                        break;
                    }
                    TeamListElement e = team.render();
                    teamKeys.add(e.getKey());
                    teamItems.add(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!isCancelled()) {
            adapter = new ListViewAdapter(fragment.getActivity(), teamItems, teamKeys);
            adapter.notifyDataSetChanged();
        }
        return response.getCode();
    }


    @Override
    protected void onPostExecute(APIResponse.CODE v) {
        super.onPostExecute(v);

        if (!isCancelled() && fragment.getActivity() != null) {
            adapter = new ListViewAdapter(fragment.getActivity(), teamItems, teamKeys);
            adapter.notifyDataSetChanged();
        }
        //android gets angry if you modify Views off the UI thread, so we do the actual View manipulation here
        if (fragment.getView() != null) {
            ListView eventList = (ListView) fragment.getView().findViewById(R.id.team_list);
            eventList.setAdapter(adapter);
        }
    }
}
