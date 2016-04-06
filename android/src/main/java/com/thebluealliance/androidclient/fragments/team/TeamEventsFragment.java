package com.thebluealliance.androidclient.fragments.team;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.interfaces.HasYearParam;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class TeamEventsFragment extends ListViewFragment<List<Event>, EventListSubscriber> implements HasYearParam {
    public static final String YEAR = "YEAR";
    public static final String TEAM_KEY = "TEAM_KEY";

    private int mYear;
    private String mTeamKey;

    @Inject EventBus mEventBus;

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
        mYear = getArguments().getInt(YEAR, -1);
        if (mYear == -1) {
            // default to current year
            mYear = Utilities.getCurrentYear();
        }
        mTeamKey = getArguments().getString(TEAM_KEY);
        super.onCreate(savedInstanceState);

        mSubscriber.setRenderMode(EventListSubscriber.MODE_TEAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
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
    public void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onYearChanged(YearChangedEvent event) {
        mYear = event.getYear();
        onRefreshStart(RefreshController.NOT_REQUESTED_BY_USER);
    }

    @Override
    public int getYear() {
        return mYear;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Event>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchTeamEvents(mTeamKey, mYear, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamEvents_%1$s_%2$d", mTeamKey, mYear);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_event_black_48dp, R.string.no_event_data);
    }
}
