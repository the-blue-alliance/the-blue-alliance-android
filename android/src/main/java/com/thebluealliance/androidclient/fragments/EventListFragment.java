package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.RecyclerViewBinder;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.itemviews.EventItemView;
import com.thebluealliance.androidclient.itemviews.ListSectionHeaderItemView;
import com.thebluealliance.androidclient.listeners.EventClickListener;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.EventListRecyclerSubscriber;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;
import com.thebluealliance.androidclient.viewmodels.EventViewModel;
import com.thebluealliance.androidclient.viewmodels.ListSectionHeaderViewModel;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class EventListFragment extends RecyclerViewFragment<List<Event>, EventListRecyclerSubscriber, RecyclerViewBinder> {

    public static final String YEAR = "YEAR";
    public static final String WEEK = "WEEK";
    public static final String MONTH = "MONTH";
    public static final String WEEK_HEADER = "HEADER";
    public static final String SHOULD_BIND_IMMEDIATELY = "SHOULD_BIND_IMMEDIATELY";

    private int mYear;
    private int mWeek;
    private int mMonth;

    public static EventListFragment newInstance(int year, int week, int month, String weekHeader) {
        EventListFragment f = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        args.putInt(WEEK, week);
        args.putInt(MONTH, month);
        args.putString(WEEK_HEADER, weekHeader);
        f.setArguments(args);
        return f;
    }

    public static EventListFragment newInstance(int year, int week, int month, String weekHeader, boolean shouldBindImmediately) {
        EventListFragment f = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        args.putInt(WEEK, week);
        args.putInt(MONTH, month);
        args.putString(WEEK_HEADER, weekHeader);
        args.putBoolean(SHOULD_BIND_IMMEDIATELY, shouldBindImmediately);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mYear = getArguments().getInt(YEAR, -1);
        mWeek = getArguments().getInt(WEEK, -1);
        mMonth = getArguments().getInt(MONTH, -1);
        String header = getArguments().getString(WEEK_HEADER);

        if (mWeek == -1 && !(header == null || header.isEmpty())) {
            mWeek = EventHelper.weekNumFromLabel(mYear, header);
        }
        super.onCreate(savedInstanceState);

        setShouldBindImmediately(getArguments().getBoolean(SHOULD_BIND_IMMEDIATELY, true));
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Event>> getObservable(String tbaCacheHeader) {
        if (mMonth != -1) {
            return mDatafeed.getCache().fetchEventsInMonth(mYear, mMonth);
        } else {
            return mDatafeed.getCache().fetchEventsInWeek(mYear, mWeek);
        }
    }

    @Override
    protected String getRefreshTag() {
        return String.format("eventList_%1$d_%2$d_%3$d", mYear, mWeek, mMonth);
    }

    public void bind() {
        if(mSubscriber != null) {
            mSubscriber.bindData();
        }
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_event_black_48dp, R.string.no_events_found);
    }

    @Override public void initializeMaps(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(EventViewModel.class, EventItemView.class);
        creator.map(ListSectionHeaderViewModel.class, ListSectionHeaderItemView.class);
    }
}
