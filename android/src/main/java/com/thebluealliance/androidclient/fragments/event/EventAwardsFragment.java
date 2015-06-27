package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.subscribers.AwardsListSubscriber;

import java.util.List;

import rx.Observable;

public class EventAwardsFragment
  extends DatafeedFragment<List<Award>, ListViewAdapter, AwardsListSubscriber, ListviewBinder> {
    private static final String EVENT_KEY = "eventKey", TEAM_KEY = "teamKey";

    private String mEventKey, mTeamKey;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    public static EventAwardsFragment newInstance(String eventKey) {
        EventAwardsFragment f = new EventAwardsFragment();
        Bundle data = new Bundle();
        data.putString(EVENT_KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    public static EventAwardsFragment newInstance(String eventKey, String teamKey) {
        EventAwardsFragment f = new EventAwardsFragment();
        Bundle data = new Bundle();
        data.putString(EVENT_KEY, eventKey);
        data.putString(TEAM_KEY, teamKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEventKey = getArguments().getString(EVENT_KEY, "");
            mTeamKey = getArguments().getString(TEAM_KEY, "");
        }
        mSubscriber.setEventKey(mEventKey);
        mSubscriber.setTeamKey(mTeamKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_carded, null);
        mListView = (ListView) view.findViewById(R.id.list);
        ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.progress);

        mBinder.mListView = mListView;
        mBinder.mProgressBar = mProgressBar;

        //disable touch feedback (you can't click the elements here...)
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setSelector(R.drawable.transparent);

        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        }
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
    protected Observable<List<Award>> getObservable() {
        return mDatafeed.fetchEventAwards(mEventKey);
    }
}
