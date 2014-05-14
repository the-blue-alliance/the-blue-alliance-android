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
import com.thebluealliance.androidclient.background.event.PopulateEventAwards;

/**
 * File created by phil on 4/22/14.
 */
public class EventAwardsFragment extends Fragment {

    private String mEventKey;
    private static final String EVENT_KEY = "eventKey";

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

    private PopulateEventAwards mTask;

    public static EventAwardsFragment newInstance(String eventKey) {
        EventAwardsFragment f = new EventAwardsFragment();
        Bundle data = new Bundle();
        data.putString(EVENT_KEY, eventKey);
        f.setArguments(data);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEventKey = getArguments().getString(EVENT_KEY, "");
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(EVENT_KEY)) {
            mEventKey = savedInstanceState.getString(EVENT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_awards, null);
        mListView = (ListView) view.findViewById(R.id.event_awards);
        if(mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            mTask = new PopulateEventAwards(this);
            mTask.execute(mEventKey);
        }
        return view;
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
