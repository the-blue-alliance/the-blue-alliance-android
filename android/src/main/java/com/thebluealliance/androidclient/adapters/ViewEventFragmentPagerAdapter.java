package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.event.EventAlliancesFragment;
import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventDistrictPointsFragment;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.fragments.event.EventRankingsFragment;
import com.thebluealliance.androidclient.fragments.event.EventResultsFragment;
import com.thebluealliance.androidclient.fragments.event.EventStatsFragment;
import com.thebluealliance.androidclient.fragments.event.EventTeamsFragment;

/**
 * Created by Nathan on 4/22/2014.
 */
public class ViewEventFragmentPagerAdapter extends FragmentPagerAdapter {

    public final String[] TITLES = {"Info", "Teams", "Rankings", "Matches", "Alliances", "District Points", "Stats", "Awards"};

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
                fragment = EventInfoFragment.newInstance(mEventKey);
                break;
            case 1: //teams
                fragment = EventTeamsFragment.newInstance(mEventKey);
                break;
            case 2: //rankings
                fragment = EventRankingsFragment.newInstance(mEventKey);
                break;
            case 3: //results
                fragment = EventResultsFragment.newInstance(mEventKey);
                break;
            case 4: //alliances
                fragment = EventAlliancesFragment.newInstance(mEventKey);
                break;
            case 5: //district points
                fragment = EventDistrictPointsFragment.newInstance(mEventKey);
                break;
            case 6: //stats
                fragment = EventStatsFragment.newInstance(mEventKey);
                break;
            case 7: //awards
                fragment = EventAwardsFragment.newInstance(mEventKey);
                break;
        }
        return fragment;
    }
}
