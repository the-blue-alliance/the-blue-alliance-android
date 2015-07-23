package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.binders.ExpandableListBinder;
import com.thebluealliance.androidclient.binders.MatchListBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.subscribers.MatchListSubscriber;
import com.thebluealliance.androidclient.views.ExpandableListView;

import java.util.List;

import rx.Observable;

public class EventMatchesFragment
  extends DatafeedFragment<List<Match>, List<ListGroup>, MatchListSubscriber, MatchListBinder> {

    private static final String KEY = "eventKey";
    private static final String TEAM = "teamKey";
    public static final String DATAFEED_TAG_FORMAT = "event_matches_%1$s";

    private String mEventKey;
    private String mTeamKey;
    private String mDatafeedTag;
    private Parcelable mListState;
    private MatchListAdapter mAdapter;
    private ExpandableListView mListView;
    private int mFirstVisiblePosition;

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
        if (getArguments() != null) {
            mEventKey = getArguments().getString(KEY, "");
            mTeamKey = getArguments().getString(TEAM, "");
        }
        mDatafeedTag = String.format(DATAFEED_TAG_FORMAT, mEventKey);
        super.onCreate(savedInstanceState);

        mSubscriber.setEventKey(mEventKey);
        mSubscriber.setTeamKey(mTeamKey);
        mBinder.setExpandMode(ExpandableListBinder.MODE_EXPAND_ONLY);
        mBinder.setSelectedTeam(mTeamKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_results, null);
        mListView = (ExpandableListView) v.findViewById(R.id.match_results);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress);
        mBinder.expandableList = mListView;
        mBinder.progressBar = progressBar;
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mListView.setSelection(mFirstVisiblePosition);
            Log.d("onCreateView", "using existing adapter");
            progressBar.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListView != null) {
            Log.d("onPause", "saving adapter");
            mAdapter = (MatchListAdapter) mListView.getExpandableListAdapter();
            mListState = mListView.onSaveInstanceState();
            mFirstVisiblePosition = mListView.getFirstVisiblePosition();
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Match>> getObservable() {
        if (mTeamKey == null || mTeamKey.isEmpty()) {
            return mDatafeed.fetchEventMatches(mEventKey);
        } else {
            return mDatafeed.fetchTeamAtEventMatches(mTeamKey, mEventKey);
        }
    }

    @Override
    protected String getDatafeedTag() {
        return mDatafeedTag;
    }
}
