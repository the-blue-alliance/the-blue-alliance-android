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
        Fragment fragment;
        switch (position) {
            default:
            case 0: //event info
                fragment = EventInfoFragment.getInstance(mEventKey);
                break;
            case 1: //teams
                fragment = EventTeamsFragment.getInstance(mEventKey);
                break;
            case 2: //rankings
                fragment = EventRankingsFragment.getInstance(mEventKey);
                break;
            case 3: //results
                fragment = EventResultsFragment.getInstance(mEventKey);
                break;
            case 4: //stats
                fragment = EventStatsFragment.getInstance(mEventKey);
                break;
            case 5: //awards
                fragment = EventAwardsFragment.getInstance(mEventKey);
                break;
        }
        return fragment;
    }
}
