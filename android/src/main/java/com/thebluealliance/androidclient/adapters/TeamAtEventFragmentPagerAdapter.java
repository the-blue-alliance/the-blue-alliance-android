package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventMatchesFragment;
import com.thebluealliance.androidclient.fragments.team.TeamMediaFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventStatsFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventSummaryFragment;
import com.thebluealliance.androidclient.helpers.EventHelper;

public class TeamAtEventFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final String[] TITLES = {"Summary", "Matches", "Media", "Stats", "Awards"};
    public static final int TAB_SUMMARY = 0,
            TAB_MATCHES = 1,
            TAB_MEDIA = 2,
            TAB_STATS = 3,
            TAB_AWARDS = 4;

    private String mTeamKey, mEventKey;

    public TeamAtEventFragmentPagerAdapter(FragmentManager fm, String teamKey, String eventKey) {
        super(fm);
        mTeamKey = teamKey;
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
            case TAB_SUMMARY:
                return TeamAtEventSummaryFragment.newInstance(mTeamKey, mEventKey);
            case TAB_MATCHES:
                return EventMatchesFragment.newInstance(mEventKey, mTeamKey);
            case TAB_MEDIA:
                return TeamMediaFragment.newInstance(mTeamKey, EventHelper.getYear(mEventKey));
            case TAB_STATS:
                return TeamAtEventStatsFragment.newInstance(mTeamKey, mEventKey);
            case TAB_AWARDS:
                return EventAwardsFragment.newInstance(mEventKey, mTeamKey);
            default:
                return new Fragment();
        }
    }

}
