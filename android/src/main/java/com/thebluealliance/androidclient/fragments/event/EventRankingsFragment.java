package com.thebluealliance.androidclient.fragments.event;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.subscribers.RankingsListSubscriber;

import rx.Observable;

/**
 * Fragment that displays the rankings for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 */
public class EventRankingsFragment
  extends DatafeedFragment<RankingsListSubscriber, ListviewBinder> {

    private static final String KEY = "eventKey";

    private String eventKey;
    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    /**
     * Creates new rankings fragment for an event
     *
     * @param eventKey the key that represents an FRC event
     * @return new event rankings fragment
     */
    public static EventRankingsFragment newInstance(String eventKey) {
        EventRankingsFragment f = new EventRankingsFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComponent.inject(this);
        // Reload key if returning from another activity/fragment
        if (getArguments() != null) {
            eventKey = getArguments().getString(KEY, "");
        }
        mSubscriber.setConsumer(mBinder);
        mBinder.setContext(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Setup views & listener
        View v = inflater.inflate(R.layout.list_view_with_spinner, null);
        mListView = (ListView) v.findViewById(R.id.list);
        ProgressBar mProgressBar = (ProgressBar) v.findViewById(R.id.progress);

        mBinder.mListView = mListView;
        mBinder.mProgressBar = mProgressBar;

        // Either reload data if returning from another fragment/activity
        // Or get data if viewing fragment for the first time.
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String teamKey = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
                Intent intent = TeamAtEventActivity.newInstance(getActivity(), eventKey, teamKey);
                
                 /* Track the call */
                AnalyticsHelper.sendClickUpdate(getActivity(), "team@event_click", "EventRankingsFragment", eventKey);

                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Observable<JsonArray> rankingsObservable = mDatafeed.fetchEventRankings(eventKey);
        rankingsObservable.subscribe(mSubscriber);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save the data if moving away from fragment.
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
        if (mSubscriber != null) {
            mSubscriber.unsubscribe();
        }
    }
}
