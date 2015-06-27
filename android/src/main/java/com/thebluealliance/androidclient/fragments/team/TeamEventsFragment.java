package com.thebluealliance.androidclient.fragments.team;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.binders.ListviewBinder;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.fragments.DatafeedFragment;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;

public class TeamEventsFragment
  extends DatafeedFragment<List<Event>, ListViewAdapter, EventListSubscriber, ListviewBinder> {
    public static final String YEAR = "YEAR";
    public static final String TEAM_KEY = "TEAM_KEY";

    private int mYear;
    private String mTeamKey;

    private Parcelable mListState;
    private ListViewAdapter mAdapter;
    private ListView mListView;

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
        if (mYear == -1) {
            // default to current year
            mYear = Utilities.getCurrentYear();
        }
        mTeamKey = getArguments().getString(TEAM_KEY);
        mSubscriber.setRenderMode(EventListSubscriber.MODE_TEAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view_with_spinner, null);

        mListView = (ListView) v.findViewById(R.id.list);
        mBinder.mListView = mListView;
        mBinder.mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
            mBinder.mProgressBar.setVisibility(View.GONE);
        }
        mListView.setOnItemClickListener((parent, view, position, id) -> {
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
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListView != null) {
            mAdapter = (ListViewAdapter) mListView.getAdapter();
            mListState = mListView.onSaveInstanceState();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    //TODO kill eventbus
    public void onEvent(YearChangedEvent event) {
        mYear = event.getYear();
    }


    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Event>> getObservable() {
        return mDatafeed.fetchTeamEvents(mTeamKey, mYear);
    }
}
