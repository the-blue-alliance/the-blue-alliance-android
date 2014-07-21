package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.adapters.EventsByWeekFragmentPagerAdapter;
import com.thebluealliance.androidclient.background.BuildEventWeekTabs;
import com.thebluealliance.androidclient.helpers.EventHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class EventsByWeekFragment extends RefreshableHostFragment {

    private int mYear;
    private EventsByWeekFragmentPagerAdapter pagerAdapter;
    private static final String YEAR = "YEAR";
    private BuildEventWeekTabs task;
    private Parcelable adapterState;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        if(pagerAdapter != null){
            pagerAdapter.notifyDataSetChanged();
        }
    }

    private ViewPager mViewPager;
    private PagerSlidingTabStrip mTabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_event_list_fragment_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.event_pager);
        // Make this ridiculously big
        mViewPager.setOffscreenPageLimit(50);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.event_pager_tabs);
        mViewPager.setPageMargin(Utilities.getPixelsFromDp(getActivity(), 16));
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(Constants.LOG_TAG, "ON PAUSE");
        if(mViewPager != null){
            Log.d(Constants.LOG_TAG, "SAVING instance state");
            adapterState = mViewPager.onSaveInstanceState();
        }
    }

    @Override
    public void onRefreshStart() {
        task = new BuildEventWeekTabs(this);
        task.execute(mYear);
    }

    public void updateLabels(ArrayList<String> labels){
        getView().findViewById(R.id.tabs_progress).setVisibility(View.GONE);
        pagerAdapter = new EventsByWeekFragmentPagerAdapter(this, getChildFragmentManager(), mYear, mTabs, mViewPager, labels);
        mViewPager.setAdapter(pagerAdapter);
        mTabs.setViewPager(mViewPager);
        if(adapterState != null){
            pagerAdapter.restoreState(adapterState, ClassLoader.getSystemClassLoader());
        }else{
            setPagerWeek();
        }
    }

    private void setPagerWeek(){
        int currentWeek = Utilities.getCurrentCompWeek();
        int currentYear = Utilities.getCurrentYear();
        //set the currently selected tab to the current week or week 1
        int week1Index = pagerAdapter.getLabels().indexOf(String.format(EventHelper.REGIONAL_LABEL, 1));
        if (currentYear != mYear) {
            mViewPager.setCurrentItem(week1Index);
        } else {
            mViewPager.setCurrentItem((currentWeek > Utilities.getCmpWeek(mYear) + 1)
                    ? Math.min(mViewPager.getAdapter().getCount(), week1Index)
                    : currentWeek);
            /** Explanation for above lines:
             * If the current week is past CMP, then
             * show week 1 (which is either index 1 or 2, which we'll get from the adapter by finding its label
             * Else, we display the current week
             */
        }
    }

    @Override
    public void onRefreshStop() {
        if(task != null){
            task.cancel(false);
        }
    }
}
