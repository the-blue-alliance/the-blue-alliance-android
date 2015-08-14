package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.EventsByWeekFragmentPagerAdapter;
import com.thebluealliance.androidclient.binders.EventTabBinder;
import com.thebluealliance.androidclient.helpers.FragmentBinder;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventWeekTab;
import com.thebluealliance.androidclient.subscribers.EventTabSubscriber;
import com.thebluealliance.androidclient.views.SlidingTabs;

import java.util.Calendar;
import java.util.List;

import rx.Observable;

public class EventsByWeekFragment
        extends DatafeedFragment<List<Event>, List<EventWeekTab>, EventTabSubscriber, EventTabBinder> {

    private static final String YEAR = "YEAR", TAB = "tab";

    private int mYear;
    private EventsByWeekFragmentPagerAdapter mFragmentAdapter;
    private Parcelable mPagerState, mAdapterState;
    private int mSelectedTab;
    private ViewPager mViewPager;
    private SlidingTabs mTabs;
    private FragmentBinder mFragmentBinder;

    public static EventsByWeekFragment newInstance(int year) {
        EventsByWeekFragment f = new EventsByWeekFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(Constants.LOG_TAG, "EventsByWeekFragment created!");
        mYear = Calendar.getInstance().get(Calendar.YEAR);
        if (getArguments() != null && getArguments().containsKey(YEAR)) {
            // Default to the current year if no year is provided in the arguments
            mYear = getArguments().getInt(YEAR);
        }
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectedTab = savedInstanceState.getInt(TAB, -1);
        } else {
            mSelectedTab = -1;
        }
        mBinder.setFragment(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFragmentAdapter != null) {
            mFragmentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_event_list_fragment_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.event_pager);
        // Make this ridiculously big
        mViewPager.setOffscreenPageLimit(50);
        mTabs = (SlidingTabs) view.findViewById(R.id.event_pager_tabs);
        ViewCompat.setElevation(mTabs, getResources().getDimension(R.dimen.toolbar_elevation));
        mViewPager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));
        mFragmentBinder = new FragmentBinder();
        mViewPager.addOnPageChangeListener(mFragmentBinder);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mViewPager != null) {
            mPagerState = mViewPager.onSaveInstanceState();
            mSelectedTab = mViewPager.getCurrentItem();
        }
        if (mFragmentAdapter != null) {
            mAdapterState = mFragmentAdapter.saveState();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mViewPager != null) {
            outState.putInt(TAB, mViewPager.getCurrentItem());
        }
    }

    public void updateLabels(List<EventWeekTab> labels) {
        if (getView() != null) {
            getView().findViewById(R.id.tabs_progress).setVisibility(View.GONE);
        }
        mFragmentAdapter = new EventsByWeekFragmentPagerAdapter(getChildFragmentManager(), mYear, labels);
        mFragmentBinder.setAdapter(mFragmentAdapter);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.removeOnPageChangeListener(null);
        mTabs.setViewPager(mViewPager);
        if (mPagerState != null) {
            mViewPager.onRestoreInstanceState(mPagerState);
            mFragmentAdapter.restoreState(mAdapterState, ClassLoader.getSystemClassLoader());
        } else {
            setPagerWeek();
        }
        if (mSelectedTab != -1) {
            mViewPager.setCurrentItem(mSelectedTab);
        }
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Event>> getObservable() {
        return mDatafeed.fetchEventsInYear(mYear);
    }

    /**
     * Set the default selected pager tab
     * If the user isn't viewing this year's events, default to Week 1
     * Otherwise, default to the current week (or the first tab, if past the last week)
     */
    private void setPagerWeek() {
        int currentWeek = Utilities.getCurrentCompWeek();
        int currentYear = Utilities.getCurrentYear();
        int week1Index = getIndexForWeek(1);
        int currentIndex = getIndexForWeek(currentWeek);
        int weekCount = mViewPager.getAdapter().getCount();

        if (currentYear != mYear && week1Index > -1) {
            mViewPager.setCurrentItem(week1Index);
        } else if (currentIndex < weekCount && currentIndex > -1) {
            mViewPager.setCurrentItem(currentIndex);
        } else {
            mViewPager.setCurrentItem(0);
        }
    }

    /**
     * Finds the index in the adapter of the given week.
     * If the week is skipped over, return the next week (assumes sorted adapter items)
     *
     * @return Adapter index containing the week, -1 if not found
     */
    private int getIndexForWeek(int week) {
        Preconditions.checkState(
          mViewPager.getAdapter() instanceof EventsByWeekFragmentPagerAdapter,
          "EventsByWeekFragment must use EventsByWeekFragmentPagerAdapter");
        List<EventWeekTab> tabs = ((EventsByWeekFragmentPagerAdapter) mViewPager.getAdapter())
                .getTabs();
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).getWeek() >= week) {
                return i;
            }
        }
        return -1;
    }
}
