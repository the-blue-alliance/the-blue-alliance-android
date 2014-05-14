package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.event.PopulateEventRankings;

/**
 * File created by phil on 4/22/14.
 */
public class EventRankingsFragment extends Fragment {

    private String eventKey;
    private static final String KEY = "eventKey";

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    private PopulateEventRankings mTask;

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
        if (getArguments() != null) {
            eventKey = getArguments().getString(KEY, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_rankings, null);
        mListView = (ListView) v.findViewById(R.id.event_ranking);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            mTask = new PopulateEventRankings(this);
            mTask.execute(eventKey);
        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTask.cancel(false);
        if(mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }
}
