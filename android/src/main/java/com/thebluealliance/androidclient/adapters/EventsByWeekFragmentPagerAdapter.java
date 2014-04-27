package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.EventListFragment;

/**
 * Created by Nathan on 4/22/2014.
 */
public class EventsByWeekFragmentPagerAdapter extends FragmentPagerAdapter {

    //TODO: don't hardcode this, use value from database
    private int mCount = 6;
    private int mYear;

    public EventsByWeekFragmentPagerAdapter(FragmentManager fm, int year) {
        super(fm);
        mYear = year;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Week " + (position + 1);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        return new EventListFragment(EventListFragment.EVENT_LIST_FOR_YEAR_WEEK, mYear, (position + 1));
    }
}
