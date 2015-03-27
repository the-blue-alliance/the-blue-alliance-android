package com.thebluealliance.androidclient.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.TeamCursorAdapter;
import com.thebluealliance.androidclient.background.PopulateTeamList;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * File created by phil on 4/20/14.
 */

public class TeamListFragment extends Fragment implements RefreshListener {

    private Activity parent;


    private static final String START = "START";
    private static final String END = "END";

    private ListView mListView;
    private ProgressBar mProgressBar;

    private PopulateTeamList mTask;

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
        parent = getActivity();
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).registerRefreshListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setFastScrollAlwaysVisible(true);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String teamKey = ((TeamCursorAdapter) adapterView.getAdapter()).getKey(position);
                Intent i = new Intent(getActivity(), ViewTeamActivity.class);
                i.putExtra(ViewTeamActivity.TEAM_KEY, teamKey);

                AnalyticsHelper.sendClickUpdate(getActivity(), "team_click", i.getDataString(), teamKey);
                
                startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart(boolean actionItemPressed) {
        Log.i(Constants.REFRESH_LOG, "Loading teams between " + mTeamNumberStart + " and " + mTeamNumberEnd);
        mTask = new PopulateTeamList(this, new RequestParams(true, actionItemPressed));
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTeamNumberStart, mTeamNumberEnd);
    }

    public void updateTask(PopulateTeamList newTask) {
        mTask = newTask;
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((RefreshableHostActivity) parent).unregisterRefreshListener(this);
    }
}
