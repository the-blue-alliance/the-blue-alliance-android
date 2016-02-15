package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.event.EventAlliancesFragment;
import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventDistrictPointsFragment;
import com.thebluealliance.androidclient.fragments.event.EventInfoFragment;
import com.thebluealliance.androidclient.fragments.event.EventMatchesFragment;
import com.thebluealliance.androidclient.fragments.event.EventRankingsFragment;
import com.thebluealliance.androidclient.fragments.event.EventStatsFragment;
import com.thebluealliance.androidclient.fragments.event.EventTeamsFragment;
import com.thebluealliance.androidclient.fragments.event.EventTickerFragment;

public class ViewEventFragmentPagerAdapter extends FragmentPagerAdapter {

    public final String[] TITLES = {"Ticker", "Info", "Teams", "Rankings", "Matches", "Alliances", "District Points", "Stats", "Awards"};
    public static final int TAB_TICKER = 0,
            TAB_INFO = 1,
            TAB_TEAMS = 2,
            TAB_RANKINGS = 3,
            TAB_MATCHES = 4,
            TAB_ALLIANCES = 5,
            TAB_DISTRICT_POINTS = 6,
            TAB_STATS = 7,
            TAB_AWARDS = 8;

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
            case TAB_TICKER: // live ticker
                fragment = EventTickerFragment.newInstance(mEventKey);
                break;
            case TAB_INFO: // event info
                fragment = EventInfoFragment.newInstance(mEventKey);
                break;
            case TAB_TEAMS: // teams
                fragment = EventTeamsFragment.newInstance(mEventKey);
                break;
            case TAB_RANKINGS: // rankings
                fragment = EventRankingsFragment.newInstance(mEventKey);
                break;
            case TAB_MATCHES: // results
                fragment = EventMatchesFragment.newInstance(mEventKey);
                break;
            case TAB_ALLIANCES: // alliances
                fragment = EventAlliancesFragment.newInstance(mEventKey);
                break;
            case TAB_DISTRICT_POINTS: // district points
                fragment = EventDistrictPointsFragment.newInstance(mEventKey);
                break;
            case TAB_STATS: // stats
                fragment = EventStatsFragment.newInstance(mEventKey);
                break;
            case TAB_AWARDS: // awards
                fragment = EventAwardsFragment.newInstance(mEventKey);
                break;
            default:
                fragment = new Fragment();
                break;
        }
        return fragment;
    }
}
