package com.thebluealliance.androidclient.fragments.team;

import android.os.Bundle;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.eventbus.YearChangedEvent;
import com.thebluealliance.androidclient.fragments.RecyclerViewFragment;
import com.thebluealliance.androidclient.interfaces.HasYearParam;
import com.thebluealliance.androidclient.itemviews.EventItemView;
import com.thebluealliance.androidclient.itemviews.ListSectionHeaderItemView;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ListSectionHeaderViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

@AndroidEntryPoint
public class TeamEventsFragment extends RecyclerViewFragment<List<Event>, EventListSubscriber, RecyclerViewBinder> implements HasYearParam {
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

    @Override public void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(EventViewModel.class, EventItemView.class);
        creator.map(ListSectionHeaderViewModel.class, ListSectionHeaderItemView.class);

        creator.listener((actionId, item, position, view) -> {
            if (actionId == Interactions.EVENT_CLICKED && item instanceof EventViewModel) {
                EventViewModel event = (EventViewModel) item;
                startActivity(TeamAtEventActivity.newInstance(getContext(), event.getKey(), mTeamKey));
            }
        });
    }
}
