package com.thebluealliance.androidclient.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.models.EventWeekTab;

import java.util.ArrayList;
import java.util.List;

public class EventsByWeekFragmentPagerAdapter extends BindableFragmentPagerAdapter {

    private int mCount;
    private int mYear;
    private List<EventWeekTab> mThisYearsWeekTabs;
    private List<String> mLabels;

    public EventsByWeekFragmentPagerAdapter(FragmentManager fm, int year, List<EventWeekTab> labels) {
        super(fm);
        mLabels = new ArrayList<>();
        mThisYearsWeekTabs = labels;
        for (int i = 0; i < mThisYearsWeekTabs.size(); i++) {
            mLabels.add(mThisYearsWeekTabs.get(i).getLabel());
        }
        mCount = labels.size();
        mYear = year;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mThisYearsWeekTabs.get(position).getLabel();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        EventWeekTab tab = mThisYearsWeekTabs.get(position);
        return EventListFragment.newInstance(mYear, tab.getEventKeys(), false);
    }

    public List<String> getLabels() {
        return mLabels;
    }

    public List<EventWeekTab> getTabs() {
        return mThisYearsWeekTabs;
    }
}