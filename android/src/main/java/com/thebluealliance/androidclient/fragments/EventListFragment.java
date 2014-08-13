package com.thebluealliance.androidclient.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateEventList;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.interfaces.RefreshableHost;
import com.thebluealliance.androidclient.listeners.EventClickListener;

/**
 * File created by phil on 4/20/14.
 */
public class EventListFragment extends Fragment implements RefreshListener {

    public static final String YEAR = "YEAR";
    public static final String WEEK = "WEEK";
    public static final String TEAM_KEY = "TEAM_KEY";
    public static final String WEEK_HEADER = "HEADER";
    public static final String HOST = "HOST";

    private int mYear;
    private int mWeek;
    private String mTeamKey, mHeader;

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private RefreshableHost mHost;

    private PopulateEventList mTask;

    public static EventListFragment newInstance(int year, int week, String teamKey, String weekHeader) {
        EventListFragment f = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        args.putInt(WEEK, week);
        args.putString(TEAM_KEY, teamKey);
        args.putString(WEEK_HEADER, weekHeader);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYear = getArguments().getInt(YEAR, -1);
        mWeek = getArguments().getInt(WEEK, -1);
        mTeamKey = getArguments().getString(TEAM_KEY);
        mHeader = getArguments().getString(WEEK_HEADER);
        if (mHost == null && getActivity() instanceof RefreshableHost) {
            mHost = (RefreshableHost) getActivity();
        }
    }

    public void setHost(RefreshableHost host) {
        mHost = host;
        mHost.registerRefreshableActivityListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) v.findViewById(R.id.list);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        }
        mListView.setOnItemClickListener(new EventClickListener(getActivity(), mTeamKey));
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(false);
        }
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHost != null) {
            mHost.startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart() {
        Log.i(Constants.REFRESH_LOG, "Loading events for week " + mHeader + " in " + mYear + " for " + mTeamKey);
        mTask = new PopulateEventList(this, mHost, mYear, mHeader, mTeamKey, true);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        mHost.deregisterRefreshableActivityListener(this);
    }
}
