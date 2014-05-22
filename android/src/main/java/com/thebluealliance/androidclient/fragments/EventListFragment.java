package com.thebluealliance.androidclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateEventList;
import com.thebluealliance.androidclient.datatypes.ListElement;

/**
 * File created by phil on 4/20/14.
 */
public class EventListFragment extends Fragment {

    public static final String YEAR = "YEAR";
    public static final String WEEK = "WEEK";
    public static final String TEAM_KEY = "TEAM_KEY";
    public static final String WEEK_HEADER = "HEADER";

    private int mYear;
    private int mWeek;
    private String mTeamKey, mHeader;

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;

    private PopulateEventList mTask;

    public static EventListFragment newInstance(int year, int week, String teamKey, String weekHeader) {
        EventListFragment f = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        args.putInt(WEEK, week);
        args.putString(TEAM_KEY, teamKey);
        args.putString(WEEK_HEADER, weekHeader);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYear = getArguments().getInt(YEAR, -1);
        mWeek = getArguments().getInt(WEEK, -1);
        mTeamKey = getArguments().getString(TEAM_KEY);
        mHeader = getArguments().getString(WEEK_HEADER);
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
            System.out.println("populate for week "+mWeek);
            mTask = new PopulateEventList(this, mYear, mHeader, mTeamKey);
            mTask.execute();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!(parent.getAdapter() instanceof ListViewAdapter)){
                    //safety check. Shouldn't ever be tripped unless someone messed up in code somewhere
                    Log.w(Constants.LOG_TAG, "Someone done goofed. A ListView adapter doesn't extend ListViewAdapter. Try again...");
                    return;
                }
                Object item = ((ListViewAdapter) parent.getAdapter()).getItem(position);
                if(item != null && item instanceof ListElement) {
                    // only open up the view event activity if the user actually clicks on a ListElement
                    // (as opposed to something inheriting from ListHeader, which shouldn't do anything on user click
                    Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                    String eventKey = ((ListElement) item).getKey();
                    intent.putExtra("eventKey", eventKey);
                    startActivity(intent);
                }else{
                    Log.d(Constants.LOG_TAG, "ListHeader clicked. Ignore...");
                }
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
