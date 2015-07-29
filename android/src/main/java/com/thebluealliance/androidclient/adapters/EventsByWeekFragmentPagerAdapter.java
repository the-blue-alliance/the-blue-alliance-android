package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.models.EventWeekTab;

import java.util.ArrayList;
import java.util.List;

public class EventsByWeekFragmentPagerAdapter extends FragmentPagerAdapter {

    private int mCount;
    private int mYear;
    private List<EventWeekTab> mThisYearsWeekTabs;
    private List<String> mLabels;

    public EventsByWeekFragmentPagerAdapter(
      FragmentManager fm,
      int year,
      List<EventWeekTab> labels) {
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
        /*String label = thisYearsWeekLabels.get(position);
        if (mCurrent.equals(label)) {
            return "Current Week";
        } else {
            return label;
        }*/
        return mThisYearsWeekTabs.get(position).getLabel();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        return EventListFragment.newInstance(
          mYear,
          mThisYearsWeekTabs.get(position).getWeek(),
          getPageTitle(position).toString());
    }

    public List<String> getLabels() {
        return mLabels;
    }

    public List<EventWeekTab> getTabs() {
        return mThisYearsWeekTabs;
    }
}
