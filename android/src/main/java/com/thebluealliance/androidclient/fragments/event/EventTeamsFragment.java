package com.thebluealliance.androidclient.fragments.event;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.subscribers.TeamListSubscriber;

import java.util.List;

import rx.Observable;

public class EventTeamsFragment
  extends DatafeedFragment<List<Team>, ListViewAdapter, TeamListSubscriber, ListviewBinder> {

    private static final String KEY = "event_key";

    private String mEventKey;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    public static EventTeamsFragment newInstance(String eventKey) {
        EventTeamsFragment f = new EventTeamsFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) view.findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        mBinder.mListView = mListView;
        mBinder.mProgressBar = progressBar;
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
        }
        mListView.setOnItemClickListener((adapterView, view1, position, id) -> {
            Log.d(Constants.LOG_TAG, "Team clicked!");
            String teamKey = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
            Intent intent = TeamAtEventActivity.newInstance(getActivity(), mEventKey, teamKey);

            /* Track the call */
            AnalyticsHelper.sendClickUpdate(getActivity(), "team@event_click", "EventTeamsFragment", EventTeamHelper.generateKey(mEventKey, teamKey));

            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
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
    protected Observable<List<Team>> getObservable() {
        return mDatafeed.fetchEventTeams(mEventKey);
    }
}
