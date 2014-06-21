package com.thebluealliance.androidclient.fragments.team;

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
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.background.PopulateEventList;
import com.thebluealliance.androidclient.interfaces.OnYearChangedListener;
import com.thebluealliance.androidclient.interfaces.RefreshListener;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListElement;

/**
 * Created by Nathan on 6/20/2014.
 */
public class TeamEventsFragment extends Fragment implements RefreshListener, OnYearChangedListener {
    public static final String YEAR = "YEAR";
    public static final String TEAM_KEY = "TEAM_KEY";

    private ViewTeamActivity parent;

    private int mYear;
    private String mTeamKey;

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;
    private ProgressBar mProgressBar;

    private PopulateEventList mTask;

    public static TeamEventsFragment newInstance(String teamKey, int year) {
        TeamEventsFragment f = new TeamEventsFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        args.putString(TEAM_KEY, teamKey);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYear = getArguments().getInt(YEAR, -1);
        mTeamKey = getArguments().getString(TEAM_KEY);
        if (!(getActivity() instanceof ViewTeamActivity)) {
            throw new IllegalArgumentException("TeamEventsFragment must be hosted by a ViewTeamActivity!");
        } else {
            parent = (ViewTeamActivity) getActivity();
        }

        parent.registerRefreshableActivityListener(this);
        parent.addOnYearChangedListener(this);
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
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!(parent.getAdapter() instanceof ListViewAdapter)) {
                    //safety check. Shouldn't ever be tripped unless someone messed up in code somewhere
                    Log.w(Constants.LOG_TAG, "Someone done goofed. A ListView adapter doesn't extend ListViewAdapter. Try again...");
                    return;
                }
                Object item = ((ListViewAdapter) parent.getAdapter()).getItem(position);
                if (item != null && item instanceof EventListElement) {
                    String eventKey = ((ListElement) item).getKey();
                    startActivity(TeamAtEventActivity.newInstance(getActivity(), eventKey, mTeamKey));
                } else {
                    Log.d(Constants.LOG_TAG, "ListHeader clicked. Ignore...");
                }
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(false);
        }
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mYear != -1) {
            parent.startRefresh(this);
        }
    }

    @Override
    public void onRefreshStart() {
        mTask = new PopulateEventList(this, mYear, "", mTeamKey, true);
        mTask.execute();
        View view = getView();
        if (view != null) {
            // Indicate loading; the task will hide the progressbar and show the content when loading is complete
            view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            view.findViewById(R.id.list).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefreshStop() {
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parent.deregisterRefreshableActivityListener(this);
        parent.removeOnYearChangedListener(this);
    }

    @Override
    public void onYearChanged(int newYear) {
        mYear = newYear;
        onRefreshStart();
    }
}
