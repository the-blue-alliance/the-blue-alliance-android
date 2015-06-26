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
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.AllianceListSubscriber;

import rx.Observable;

public class EventAlliancesFragment
  extends DatafeedFragment<Event, ListViewAdapter, AllianceListSubscriber, ListviewBinder> {
    private static final String KEY = "event_key";

    private String mEventKey;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    public static EventAlliancesFragment newInstance(String eventKey) {
        EventAlliancesFragment f = new EventAlliancesFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) view.findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        //disable touch feedback (you can't click the elements here...)
        mListView.setCacheColorHint(getResources().getColor(android.R.color.transparent));
        mListView.setSelector(R.drawable.transparent);

        mBinder.mListView = mListView;
        mBinder.mProgressBar = progressBar;

        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            progressBar.setVisibility(View.GONE);
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
    protected Observable<Event> getObservable() {
        return mDatafeed.fetchEvent(mEventKey);
    }
}
