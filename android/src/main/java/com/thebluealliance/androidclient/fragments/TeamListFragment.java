package com.thebluealliance.androidclient.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateTeamList;

/**
 * File created by phil on 4/20/14.
 */
public class TeamListFragment extends Fragment {

    private Activity activity;
    private int teamNumberStart, teamNumberEnd;

    public TeamListFragment(int start, int end) {
        super();
        teamNumberStart = start;
        teamNumberEnd = end;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teams, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView().findViewById(R.id.team_list) != null) {
            ((ListView) getView().findViewById(R.id.team_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    String teamKey = ((ListViewAdapter) adapterView.getAdapter()).getKey(position);
                    Intent i = new Intent(getActivity(), ViewTeamActivity.class);
                    i.putExtra(ViewTeamActivity.TEAM_KEY, "frc254");
                    startActivity(i);
                }
            });
        }
        new PopulateTeamList(this).execute(teamNumberStart, teamNumberEnd);
    }
}
