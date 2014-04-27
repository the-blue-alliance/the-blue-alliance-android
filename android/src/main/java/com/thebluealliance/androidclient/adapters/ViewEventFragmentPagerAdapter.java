package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.fragments.event.EventRankingsFragment;
import com.thebluealliance.androidclient.fragments.event.EventResultsFragment;
import com.thebluealliance.androidclient.fragments.event.EventStatsFragment;
import com.thebluealliance.androidclient.fragments.event.EventTeamsFragment;

/**
 * Created by Nathan on 4/22/2014.
 */
public class ViewEventFragmentPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Info", "Teams", "Rankings", "Results", "Stats", "Awards"};

    private String mEventKey;

    public ViewEventFragmentPagerAdapter(FragmentManager fm, String eventKey) {
        super(fm);
        mEventKey = eventKey;
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
            default:
            case 0: //event info
                return new EventInfoFragment();
            case 1: //teams
                return new EventTeamsFragment();
            case 2: //rankings
                return new EventRankingsFragment();
            case 3: //results
                return new EventResultsFragment();
            case 4: //stats
                return new EventStatsFragment();
            case 5: //awards
                return new EventAwardsFragment();
        }
    }
}
