package com.thebluealliance.androidclient.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.fragments.TeamListFragment;

public class TeamListFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final int TEAMS_PER_TAB = 1000;

    private int mCount;

    public TeamListFragmentPagerAdapter(FragmentManager fm, int largestTeamNumber) {
        super(fm);
        mCount = (largestTeamNumber / TEAMS_PER_TAB) + 1;
        TbaLogger.d("LARGEST TEAM: " + largestTeamNumber);
        TbaLogger.d("USING " + mCount + " PAGES");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "1-999";
            default:
                return (position * 1000) + "-" + ((position * 1000) + 999);
        }

    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        return TeamListFragment.newInstance(position * 1000);
    }
}
