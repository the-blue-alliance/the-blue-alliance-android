package com.thebluealliance.androidclient.fragments;

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

    private static final String START = "START";
    private static final String END = "END";

    private int mTeamNumberStart, mTeamNumberEnd;

    public static TeamListFragment newInstance(int startTeamNumber, int endTeamNumber) {
        TeamListFragment f = new TeamListFragment();
        Bundle args = new Bundle();
        args.putInt(START, startTeamNumber);
        args.putInt(END, endTeamNumber);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeamNumberStart = getArguments().getInt(START);
        mTeamNumberEnd = getArguments().getInt(END);
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
                    i.putExtra(ViewTeamActivity.TEAM_KEY, teamKey);
                    startActivity(i);
                }
            });
        }
        new PopulateTeamList(this).execute(mTeamNumberStart, mTeamNumberEnd);
    }
}
