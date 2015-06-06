package com.thebluealliance.androidclient.fragments.event;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.LegacyRefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.background.event.PopulateEventMatches;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * File created by phil on 4/22/14.
 */
public class EventMatchesFragment extends Fragment implements RefreshListener {

    private Activity parent;

    private String eventKey, teamKey;
    private static final String KEY = "eventKey", TEAM = "teamKey";

    private Parcelable mListState;
    private MatchListAdapter mAdapter;
    private ExpandableListView mListView;
    private int mFirstVisiblePosition;
    private ProgressBar mProgressBar;

    private PopulateEventMatches mTask;

    public static EventMatchesFragment newInstance(String eventKey, String teamKey) {
        EventMatchesFragment f = new EventMatchesFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        data.putString(TEAM, teamKey);
        f.setArguments(data);
        return f;
    }

    public static EventMatchesFragment newInstance(String eventKey) {
        return newInstance(eventKey, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventKey = getArguments().getString(KEY, "");
            teamKey = getArguments().getString(TEAM, "");
        }
        parent = getActivity();
        if (parent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) parent).registerRefreshListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_results, null);
        mListView = (ExpandableListView) v.findViewById(R.id.match_results);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mListView.setSelection(mFirstVisiblePosition);
            Log.d("onCreateView", "using existing adapter");
            mProgressBar.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (parent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(false);
        }
        if (mListView != null) {
            Log.d("onPause", "saving adapter");
            mAdapter = (MatchListAdapter) mListView.getExpandableListAdapter();
            mListState = mListView.onSaveInstanceState();
            mFirstVisiblePosition = mListView.getFirstVisiblePosition();
        }
    }

    @Override
    public void onRefreshStart(boolean actionIconPressed) {
        Log.i(Constants.REFRESH_LOG, "Loading " + eventKey + " results");
        mTask = new PopulateEventMatches(this, new RequestParams(true, actionIconPressed));
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, eventKey, teamKey);
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    public void updateTask(PopulateEventMatches newTask) {
        mTask = newTask;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((LegacyRefreshableHostActivity) parent).unregisterRefreshListener(this);
    }
}
