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
        switch (position) {
            case TAB_TICKER:
                return EventTickerFragment.newInstance(mEventKey);
            case TAB_INFO:
                return EventInfoFragment.newInstance(mEventKey);
            case TAB_TEAMS:
                return EventTeamsFragment.newInstance(mEventKey);
            case TAB_RANKINGS:
                return EventRankingsFragment.newInstance(mEventKey);
            case TAB_MATCHES:
                return EventMatchesFragment.newInstance(mEventKey);
            case TAB_ALLIANCES:
                return EventAlliancesFragment.newInstance(mEventKey);
            case TAB_DISTRICT_POINTS:
                return EventDistrictPointsFragment.newInstance(mEventKey);
            case TAB_STATS:
                return EventStatsFragment.newInstance(mEventKey);
            case TAB_AWARDS:
                return EventAwardsFragment.newInstance(mEventKey);
            default:
                return new Fragment();
        }
    }
}
