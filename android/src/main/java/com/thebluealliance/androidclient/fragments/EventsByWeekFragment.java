package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.EventsByWeekFragmentPagerAdapter;
import com.thebluealliance.androidclient.binders.EventTabBinder;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.EventTabSubscriber;
import com.thebluealliance.androidclient.views.SlidingTabs;

import java.util.Calendar;
import java.util.List;

import rx.Observable;

public class EventsByWeekFragment
  extends DatafeedFragment<List<Event>, List<String>, EventTabSubscriber, EventTabBinder> {

    private static final String YEAR = "YEAR", TAB = "tab";

    private int mYear;
    private EventsByWeekFragmentPagerAdapter mFragmentAdapter;
    private Parcelable mPagerState, mAdapterState;
    private int mSelectedTab;
    private ViewPager mViewPager;
    private SlidingTabs mTabs;


    public static EventsByWeekFragment newInstance(int year) {
        EventsByWeekFragment f = new EventsByWeekFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, year);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "EventsByWeekFragment created!");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (getArguments() != null) {
            // Default to the current year if no year is provided in the arguments
            mYear = getArguments().getInt(YEAR, currentYear);
        }
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
    public View onCreateView(
      LayoutInflater inflater,
      ViewGroup container,
      Bundle
        savedInstanceState) {
        final View view =
          inflater.inflate(R.layout.fragment_event_list_fragment_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.event_pager);
        // Make this ridiculously big
        mViewPager.setOffscreenPageLimit(50);
        mTabs = (SlidingTabs) view.findViewById(R.id.event_pager_tabs);
        ViewCompat.setElevation(mTabs, getResources().getDimension(R.dimen.toolbar_elevation));
        mViewPager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));

        Log.d(Constants.LOG_TAG, "EventByWeekFragment view created!");
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

    public void updateLabels(List<String> labels) {
        if (getView() != null) {
            getView().findViewById(R.id.tabs_progress).setVisibility(View.GONE);
        }
        mFragmentAdapter = new EventsByWeekFragmentPagerAdapter(
          getChildFragmentManager(),
          mYear,
          labels);
        mViewPager.setAdapter(mFragmentAdapter);
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

    private void setPagerWeek() {
        int currentWeek = Utilities.getCurrentCompWeek();
        int currentYear = Utilities.getCurrentYear();
        //set the currently selected tab to the current week or week 1
        int week1Index = mFragmentAdapter.getLabels()
          .indexOf(String.format(EventHelper.REGIONAL_LABEL, 1));
        if (currentYear != mYear) {
            mViewPager.setCurrentItem(week1Index);
        } else {
            mViewPager.setCurrentItem((currentWeek > Utilities.getCmpWeek(mYear) + 1)
                    ? Math.min(mViewPager.getAdapter().getCount(), week1Index)
                    : currentWeek);
            /** Explanation for above lines:
             * If the current week is past CMP, then
             * show week 1 (which is either index 1 or 2, which we'll find in the adapter
             * Else, we display the current week
             */
        }
    }
}
