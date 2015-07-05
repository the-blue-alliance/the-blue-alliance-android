package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.EventListFragment;

import java.util.List;

public class EventsByWeekFragmentPagerAdapter extends FragmentPagerAdapter {

    private int mCount;
    private int mYear;
    private List<String> thisYearsWeekLabels;


    public EventsByWeekFragmentPagerAdapter(
      FragmentManager fm,
      int year,
      List<String> labels) {
        super(fm);
        thisYearsWeekLabels = labels;
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
        return thisYearsWeekLabels.get(position);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        return EventListFragment.newInstance(
          mYear,
          position,
          getPageTitle(position).toString());
    }

    public List<String> getLabels() {
        return thisYearsWeekLabels;
    }
}
