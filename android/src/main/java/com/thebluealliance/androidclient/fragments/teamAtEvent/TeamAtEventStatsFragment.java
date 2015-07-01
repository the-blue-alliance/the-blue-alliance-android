package com.thebluealliance.androidclient.fragments.teamAtEvent;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.subscribers.TeamStatsSubscriber;

import java.util.List;

import rx.Observable;

public class TeamAtEventStatsFragment
  extends DatafeedFragment<JsonObject, List<ListItem>, TeamStatsSubscriber, ListviewBinder> {

    public static final String TEAM_KEY = "team", EVENT_KEY = "event";

    private String mTeamKey, mEventKey;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    public static TeamAtEventStatsFragment newInstance(String teamKey, String eventKey) {
        TeamAtEventStatsFragment f = new TeamAtEventStatsFragment();
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

        mTeamKey = getArguments().getString(TEAM_KEY);
        mEventKey = getArguments().getString(EVENT_KEY);
        mSubscriber.setTeamKey(mTeamKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Setup views & listener
        View v = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) v.findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);

        //disable touch feedback (you can't click the elements here...)
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setSelector(R.drawable.transparent);

        mBinder.mListView = mListView;
        mBinder.mProgressBar = progressBar;

        // Either reload data if returning from another fragment/activity
        // Or get data if viewing fragment for the first time.
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save the data if moving away from fragment.
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<JsonObject> getObservable() {
        return mDatafeed.fetchTeamAtEventStats(mEventKey, mTeamKey);
    }
}
