package com.thebluealliance.androidclient.fragments.event;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewMatchActivity;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.background.event.PopulateEventResults;
import com.thebluealliance.androidclient.models.Match;

/**
 * File created by phil on 4/22/14.
 */
public class EventResultsFragment extends Fragment {

    private String eventKey, teamKey;
    private static final String KEY = "eventKey", TEAM = "teamKey";

    private Parcelable mListState;
    private MatchListAdapter mAdapter;
    private ExpandableListView mListView;
    private int mFirstVisiblePosition;
    private ProgressBar mProgressBar;

    private PopulateEventResults mTask;

    public static EventResultsFragment newInstance(String eventKey, String teamKey){
        EventResultsFragment f = new EventResultsFragment();
        Bundle data = new Bundle();
        data.putString(KEY, eventKey);
        data.putString(TEAM, teamKey);
        f.setArguments(data);
        return f;
    }

    public static EventResultsFragment newInstance(String eventKey) {
        return newInstance(eventKey, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventKey = getArguments().getString(KEY, "");
            teamKey = getArguments().getString(TEAM, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_results, null);
        mListView = (ExpandableListView) v.findViewById(R.id.match_results);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mListView.setSelection(mFirstVisiblePosition);
            Log.d("onCreateView", "using existing adapter");
            mProgressBar.setVisibility(View.GONE);
        } else {
            mTask = new PopulateEventResults(this);
            mTask.execute(eventKey, teamKey);
            Log.d("onCreateView", "creating new adapter");
        }
        System.out.println("setting on click adapter");
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                String matchKey = (String)view.getTag();
                if(matchKey != null && Match.validateMatchKey(matchKey)){
                    startActivity(ViewMatchActivity.newInstance(getActivity(), matchKey));
                }
                return true;
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTask.cancel(false);
        if (mListView != null) {
            Log.d("onPause", "saving adapter");
            mAdapter = (MatchListAdapter) mListView.getExpandableListAdapter();
            mListState = mListView.onSaveInstanceState();
            mFirstVisiblePosition = mListView.getFirstVisiblePosition();
        }
    }
}
