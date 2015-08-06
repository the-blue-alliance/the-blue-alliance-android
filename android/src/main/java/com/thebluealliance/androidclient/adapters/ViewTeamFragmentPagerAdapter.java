package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.team.TeamEventsFragment;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;
import com.thebluealliance.androidclient.fragments.team.TeamMediaFragment;

public class ViewTeamFragmentPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Info", "Events", "Media"};

    private String mTeamKey;
    private int mYear;

    public ViewTeamFragmentPagerAdapter(FragmentManager fm, String teamKey, int year) {
        super(fm);
        mTeamKey = teamKey;
        mYear = year;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // info
                // This is the info page
                return TeamInfoFragment.newInstance(mTeamKey);
            case 1: // events
                return TeamEventsFragment.newInstance(mTeamKey, mYear);
            case 2: // media
            default:
                return TeamMediaFragment.newInstance(mTeamKey, mYear);
        }

    }
}
