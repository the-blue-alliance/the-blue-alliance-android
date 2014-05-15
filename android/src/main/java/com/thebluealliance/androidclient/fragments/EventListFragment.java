package com.thebluealliance.androidclient.fragments;

import android.content.Intent;
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
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateEventList;

/**
 * File created by phil on 4/20/14.
 */
public class EventListFragment extends Fragment {

    public static final String YEAR = "YEAR";
    public static final String WEEK = "WEEK";
    public static final String TEAM_KEY = "TEAM_KEY";

    private int mYear;
    private int mWeek;
    private String mTeamKey;

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;

    private PopulateEventList mTask;

    public static EventListFragment newInstance(int year, int week, String teamKey) {
        EventListFragment f = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        args.putInt(WEEK, week);
        args.putString(TEAM_KEY, teamKey);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYear = getArguments().getInt(YEAR, -1);
        mWeek = getArguments().getInt(WEEK, -1);
        mTeamKey = getArguments().getString(TEAM_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_fragment_with_spinner, null);
        mListView = (ListView) v.findViewById(R.id.list);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mTask = new PopulateEventList(this, mYear, mWeek, mTeamKey);
            mTask.execute();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                String eventKey = ((ListViewAdapter) parent.getAdapter()).getKey(position);
                intent.putExtra("eventKey", eventKey);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTask.cancel(false);
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }
}
