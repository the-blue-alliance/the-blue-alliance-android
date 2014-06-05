package com.thebluealliance.androidclient.fragments.team;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.team.PopulateTeamMedia;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * File created by phil on 5/31/14.
 */
public class TeamMediaFragment extends Fragment implements RefreshListener {

    public static final String TEAM_KEY = "team", YEAR = "year";

    public static Fragment newInstance(String teamKey, int year) {
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, teamKey);
        args.putInt(YEAR, year);

        Fragment f = new TeamMediaFragment();
        f.setArguments(args);
        return f;
    }

    private String teamKey;
    private int year;
    private PopulateTeamMedia task;

    public TeamMediaFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null || !args.containsKey(TEAM_KEY) || !args.containsKey(YEAR)) {
            throw new IllegalArgumentException("TeamMediaFragment must be constructed with a team key and year");
        }
        teamKey = args.getString(TEAM_KEY);
        year = args.getInt(YEAR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_media, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        task = new PopulateTeamMedia(this);
        task.execute(teamKey, year);
    }

    @Override
    public void onRefreshStart() {
        task = new PopulateTeamMedia(this);
        task.execute(teamKey, year);
    }

    @Override
    public void onRefreshStop() {
        task.cancel(false);
    }
}