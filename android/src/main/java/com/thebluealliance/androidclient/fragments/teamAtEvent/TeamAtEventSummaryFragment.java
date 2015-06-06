package com.thebluealliance.androidclient.fragments.teamAtEvent;

import android.app.Activity;
import android.content.BroadcastReceiver;
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
import com.thebluealliance.androidclient.activities.LegacyRefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.teamAtEvent.PopulateTeamAtEventSummary;
import com.thebluealliance.androidclient.datafeed.RequestParams;
import com.thebluealliance.androidclient.interfaces.RefreshListener;

/**
 * Created by phil on 7/16/14.
 */
public class TeamAtEventSummaryFragment extends Fragment implements RefreshListener {

    public static final String TEAM_KEY = "team", EVENT_KEY = "event";

    private String teamKey, eventKey;
    private Activity parent;
    private Parcelable listState;
    private ListViewAdapter adapter;
    private ListView listView;
    private PopulateTeamAtEventSummary task;
    private BroadcastReceiver receiver;

    public static TeamAtEventSummaryFragment newInstance(String teamKey, String eventKey) {
        TeamAtEventSummaryFragment f = new TeamAtEventSummaryFragment();
        Bundle args = new Bundle();
        args.putString(TEAM_KEY, teamKey);
        args.putString(EVENT_KEY, eventKey);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(TEAM_KEY) || !getArguments().containsKey(EVENT_KEY)) {
            throw new IllegalArgumentException("TeamAtEventSummaryFragment must contain both team key and event key");
        }

        teamKey = getArguments().getString(TEAM_KEY);
        eventKey = getArguments().getString(EVENT_KEY);

        parent = getActivity();

        if (parent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) parent).registerRefreshListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Setup views & listener
        View v = inflater.inflate(R.layout.list_view_with_spinner, null);
        listView = (ListView) v.findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);

        //disable touch feedback (you can't click the elements here...)
        listView.setCacheColorHint(android.R.color.transparent);
        listView.setSelector(R.drawable.transparent);

        // Either reload data if returning from another fragment/activity
        // Or get data if viewing fragment for the first time.
        if (adapter != null) {
            listView.setAdapter(adapter);
            listView.onRestoreInstanceState(listState);
            progressBar.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save the data if moving away from fragment.
        if (task != null) {
            task.cancel(false);
        }
        if (listView != null) {
            adapter = (ListViewAdapter) listView.getAdapter();
            listState = listView.onSaveInstanceState();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (parent instanceof LegacyRefreshableHostActivity) {
            ((LegacyRefreshableHostActivity) parent).startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart(boolean actionIconPressed) {
        Log.i(Constants.REFRESH_LOG, "Loading " + teamKey + "@" + eventKey + " summary");
        task = new PopulateTeamAtEventSummary(this, new RequestParams(true, actionIconPressed));
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, teamKey, eventKey);
    }

    @Override
    public void onRefreshStop() {
        if (task != null) {
            task.cancel(false);
        }
    }

    public void updateTask(PopulateTeamAtEventSummary newTask) {
        task = newTask;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((LegacyRefreshableHostActivity) parent).unregisterRefreshListener(this);
    }

}
