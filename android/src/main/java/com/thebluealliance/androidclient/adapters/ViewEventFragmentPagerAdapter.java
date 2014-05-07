package com.thebluealliance.androidclient.adapters;

import android.os.Bundle;
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
        Bundle info = new Bundle();
        info.putString("eventKey",mEventKey);
        switch (position) {
            default:
            case 0: //event info
                fragment = new EventInfoFragment();
                break;
            case 1: //teams
                fragment = new EventTeamsFragment();
                break;
            case 2: //rankings
                fragment = new EventRankingsFragment();
                break;
            case 3: //results
                fragment = new EventResultsFragment();
                break;
            case 4: //stats
                fragment = new EventStatsFragment();
                break;
            case 5: //awards
                fragment = new EventAwardsFragment();
                break;
        }
        fragment.setArguments(info);
        return fragment;
    }
}
