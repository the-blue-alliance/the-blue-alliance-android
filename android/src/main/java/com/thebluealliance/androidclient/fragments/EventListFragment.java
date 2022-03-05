package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;

import com.thebluealliance.androidclient.Interactions;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewEventActivity;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.itemviews.EventItemView;
import com.thebluealliance.androidclient.itemviews.ListSectionHeaderItemView;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ListSectionHeaderViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

@AndroidEntryPoint
public class EventListFragment extends RecyclerViewFragment<List<Event>, EventListSubscriber, RecyclerViewBinder> {

    public static final String YEAR = "YEAR";
    public static final String EVENT_KEYS = "EVENT_KEYS";
    public static final String SHOULD_BIND_IMMEDIATELY = "SHOULD_BIND_IMMEDIATELY";

    private int mYear;
    private List<String> mEventKeys;

    public static EventListFragment newInstance(int year, ArrayList<String> eventKeys, boolean shouldBindImmediately) {
        EventListFragment f = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        args.putStringArrayList(EVENT_KEYS, eventKeys);
        args.putBoolean(SHOULD_BIND_IMMEDIATELY, shouldBindImmediately);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mYear = getArguments().getInt(YEAR, -1);
        mEventKeys = getArguments().getStringArrayList(EVENT_KEYS);
        if (mEventKeys == null) {
            throw new IllegalArgumentException("EventListFragment must be constructed with event keys");
        }
        super.onCreate(savedInstanceState);

        setShouldBindImmediately(getArguments().getBoolean(SHOULD_BIND_IMMEDIATELY, true));
    }

    @Override
    protected Observable<List<Event>> getObservable(String tbaCacheHeader) {
       return mDatafeed.getCache().fetchEvents(mEventKeys);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventList_%1$d_%2$d", mYear, mEventKeys.hashCode());
    }

    public void bind() {
        if(mSubscriber != null) {
            mSubscriber.bindData();
        }
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_event_black_48dp, R.string.no_events_found);
    }

    @Override public void initializeAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(EventViewModel.class, EventItemView.class);
        creator.map(ListSectionHeaderViewModel.class, ListSectionHeaderItemView.class);

        creator.listener((actionId, item, position, view) -> {
            if (actionId == Interactions.EVENT_CLICKED && item instanceof EventViewModel) {
                EventViewModel event = (EventViewModel) item;
                startActivity(ViewEventActivity.newInstance(getContext(), event.getKey()));
            }
        });
    }
}
