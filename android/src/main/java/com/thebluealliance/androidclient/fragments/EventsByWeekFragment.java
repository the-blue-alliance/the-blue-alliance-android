package com.thebluealliance.androidclient.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.thebluealliance.androidclient.helpers.EventHelper;

import java.util.Calendar;
import java.util.Date;

public class EventsByWeekFragment extends Fragment {

    private int mYear;
    private static final String YEAR = "YEAR";

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

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_event_list_fragment_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.event_pager);
        final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.event_pager_tabs);
        final Context c = getActivity();
        mViewPager.setAdapter(new EventsByWeekFragmentPagerAdapter(c, getChildFragmentManager(), mYear));
        tabs.setViewPager(mViewPager);
        int currentWeek = EventHelper.competitionWeek(new Date());
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        //set the currently selected tab to the current week or week 1
        int week1Index = ((EventsByWeekFragmentPagerAdapter) mViewPager.getAdapter()).getLabels().indexOf(String.format(EventHelper.REGIONAL_LABEL, 1));
        if (currentYear != mYear) {
            mViewPager.setCurrentItem(week1Index);
        } else {
            mViewPager.setCurrentItem((currentWeek > Utilities.getCmpWeek(mYear) + 1)
                    ? Math.min(mViewPager.getAdapter().getCount(), week1Index)
                    : currentWeek);
            /** Explanation for above lines:
             * If the current week is past CMP,, then
             * show week 1 (which is either index 1 or 2, which we'll get from the adapter by finding its label
             * Else, we display the current week
             */
        }
        return view;
    }
}
