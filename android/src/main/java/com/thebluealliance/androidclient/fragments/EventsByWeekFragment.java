package com.thebluealliance.androidclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.EventsByWeekFragmentPagerAdapter;
import com.thebluealliance.androidclient.interfaces.ActionBarSpinnerListener;

public class EventsByWeekFragment extends Fragment implements ActionBarSpinnerListener {

    private int mYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_list_fragment_pager, container, false);
    }

    @Override
    public void actionBarSpinnerSelected(int position, String yearString) {
        int year = Integer.parseInt(yearString);
        // Only update the view when the year changes
        if (year == mYear) {
            return;
        }
        mYear = year;
        View view = getView();
        ViewPager pager = (ViewPager) view.findViewById(R.id.event_pager);
        pager.setAdapter(new EventsByWeekFragmentPagerAdapter(getChildFragmentManager(), mYear));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.event_pager_tabs);
        tabs.setViewPager(pager);
    }
}
