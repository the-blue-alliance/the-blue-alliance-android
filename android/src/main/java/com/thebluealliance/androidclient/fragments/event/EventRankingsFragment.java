package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.event.PopulateEventRankings;
import com.thebluealliance.androidclient.listitems.ListElement;

/**
 *
 * Fragment that displays the rankings for an FRC event.
 *
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author Nathan Walters
 *
 * File created by phil on 4/22/14.
 */
public class EventRankingsFragment extends Fragment {

    private String eventKey;
    private static final String KEY = "eventKey";

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;

    private PopulateEventRankings mTask;

    /**
     * Creates new rankings fragment for an event
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
        // Reload key if returning from another activity/fragment
        if (getArguments() != null) {
            eventKey = getArguments().getString(KEY, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Setup views & listener
        View v = inflater.inflate(R.layout.list_fragment_with_spinner, null);
        mListView = (ListView) v.findViewById(R.id.list);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);

        // Either reload data if returning from another fragment/activity
        // Or get data if viewing fragment for the first time.
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mTask = new PopulateEventRankings(this);
            mTask.execute(eventKey);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String teamKey = ((ListElement) ((ListViewAdapter) adapterView.getAdapter()).getItem(position)).getKey();
                startActivity(TeamAtEventActivity.newInstance(getActivity(), eventKey, teamKey));
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save the data if moving away from fragment.
        mTask.cancel(false);
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }
}
