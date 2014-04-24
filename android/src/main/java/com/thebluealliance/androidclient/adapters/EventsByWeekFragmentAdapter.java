package com.thebluealliance.androidclient.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import com.thebluealliance.androidclient.activities.ViewTeam;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.fragments.TeamInfoFragment;

/**
 * Created by Nathan on 4/22/2014.
 */
public class EventsByWeekFragmentAdapter extends FragmentPagerAdapter {

    //TODO: don't hardcode this, use value from database
    private int mCount = 6;
    private int mYear;

    public EventsByWeekFragmentAdapter(FragmentManager fm, int year) {
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
        Log.d("EventsByWeekFragmentAdapter", "position " + position);
        return new EventListFragment(EventListFragment.EVENT_LIST_FOR_YEAR_WEEK, mYear, (position + 1));
    }
}
