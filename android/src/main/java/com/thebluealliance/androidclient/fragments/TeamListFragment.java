package com.thebluealliance.androidclient.fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.SimpleCursorLoader;
import com.thebluealliance.androidclient.adapters.TeamCursorAdapter;
import com.thebluealliance.androidclient.background.PopulateTeamList;
import com.thebluealliance.androidclient.datafeed.Database;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * File created by phil on 4/20/14.
 */

public class TeamListFragment extends Fragment implements RefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

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
            ((RefreshableHostActivity) parent).registerRefreshableActivityListener(this);
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
                startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Each loader needs a unique id; use the sum of the start and end numbers
        int id = mTeamNumberStart + mTeamNumberEnd;
        getActivity().getSupportLoaderManager().initLoader(id, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new SimpleCursorLoader(getActivity()) {
            @Override
            public Cursor loadInBackground() {
                return Database.getInstance(getActivity()).getTeamsTable().getCursorForTeamsInRange(mTeamNumberStart, mTeamNumberEnd);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursorLoader.getId() != mTeamNumberStart + mTeamNumberEnd) {
            return;
        }
        Log.d(Constants.LOG_TAG, "Load finished!");
        mProgressBar.setVisibility(View.GONE);
        mListView.setAdapter(new TeamCursorAdapter(getActivity(), cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListView.setAdapter(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (parent instanceof RefreshableHostActivity) {
            ((RefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart() {
        Log.i(Constants.REFRESH_LOG, "Loading teams between " + mTeamNumberStart + " and " + mTeamNumberEnd);
        mTask = new PopulateTeamList(this, true);
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
        ((RefreshableHostActivity) parent).deregisterRefreshableActivityListener(this);
    }
}
