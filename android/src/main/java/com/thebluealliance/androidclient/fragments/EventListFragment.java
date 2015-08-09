package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listeners.EventClickListener;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.EventListSubscriber;

import java.util.List;

import rx.Observable;

public class EventListFragment extends ListViewFragment<List<Event>, EventListSubscriber> {

    public static final String YEAR = "YEAR";
    public static final String WEEK = "WEEK";
    public static final String MONTH = "MONTH";
    public static final String WEEK_HEADER = "HEADER";

    private int mYear;
    private int mWeek;
    private int mMonth;

    public static EventListFragment newInstance(
      int year,
      int week,
      int month,
      String weekHeader) {
        EventListFragment f = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        args.putInt(WEEK, week);
        args.putInt(MONTH, month);
        args.putString(WEEK_HEADER, weekHeader);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemClickListener(new EventClickListener(getActivity(), null));
        return v;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Event>> getObservable() {
        if (mMonth != -1) {
            return mDatafeed.getCache().fetchEventsInMonth(mYear, mMonth);
        } else {
            return mDatafeed.getCache().fetchEventsInWeek(mYear, mWeek);
        }
    }
}
