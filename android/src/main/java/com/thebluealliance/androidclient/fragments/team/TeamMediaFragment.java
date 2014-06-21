package com.thebluealliance.androidclient.fragments.team;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.background.team.PopulateTeamMedia;
import com.thebluealliance.androidclient.interfaces.OnYearChangedListener;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * File created by phil on 5/31/14.
 */
public class TeamMediaFragment extends Fragment implements RefreshListener, OnYearChangedListener {

    private ViewTeamActivity parent;

    public static final String TEAM_KEY = "team", YEAR = "year";

    private String teamKey;
    private int year;
    private PopulateTeamMedia task;

    public static Fragment newInstance(String teamKey, int year) {
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, teamKey);
        args.putInt(YEAR, year);

        Fragment f = new TeamMediaFragment();
        f.setArguments(args);
        return f;
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
        if (!(getActivity() instanceof ViewTeamActivity)) {
            throw new IllegalArgumentException("TeamMediaFragment must be hosted by a ViewTeamActivity!");
        } else {
            parent = (ViewTeamActivity) getActivity();
        }

        parent.registerRefreshableActivityListener(this);
        parent.addOnYearChangedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_media, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (year != -1) {
            parent.startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart() {
        // Reset the view
        ((ViewGroup) getView()).removeAllViews();
        ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_team_media, (ViewGroup) getView(), true);

        task = new PopulateTeamMedia(this, true);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teamKey, year);
        View view = getView();
        if (view != null) {
            // Indicate loading; the task will hide the progressbar and show the content when loading is complete
            view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            view.findViewById(R.id.team_media_list).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefreshStop() {
        if (task != null) {
            task.cancel(false);
        }
    }

    public void updateTask(PopulateTeamMedia newTask){
        task = newTask;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parent.deregisterRefreshableActivityListener(this);
        parent.removeOnYearChangedListener(this);
    }

    @Override
    public void onYearChanged(int newYear) {
        year = newYear;
        parent.startRefresh(this);
    }
}