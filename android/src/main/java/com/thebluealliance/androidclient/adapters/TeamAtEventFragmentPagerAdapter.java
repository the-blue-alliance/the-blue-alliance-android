package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.event.EventAwardsFragment;
import com.thebluealliance.androidclient.fragments.event.EventResultsFragment;
import com.thebluealliance.androidclient.fragments.event.EventStatsFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventStatsFragment;
import com.thebluealliance.androidclient.fragments.teamAtEvent.TeamAtEventSummaryFragment;

/**
 * Created by phil on 7/16/14.
 */
public class TeamAtEventFragmentPagerAdapter extends FragmentPagerAdapter {

    public final String[] TITLES = {"Summary", "Matches", "Stats", "Awards"};

    private String teamKey, eventKey;

    public TeamAtEventFragmentPagerAdapter(FragmentManager fm, String teamKey, String eventKey){
        super(fm);
        this.teamKey = teamKey;
        this.eventKey = eventKey;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position){
            default:
            case 0: //summary
                fragment = TeamAtEventSummaryFragment.newInstance(teamKey, eventKey);
                break;
            case 1:
                //matches
                fragment = EventResultsFragment.newInstance(eventKey, teamKey);
                break;
            case 2: //stats
                fragment = TeamAtEventStatsFragment.newInstance(teamKey, eventKey);
                break;
            case 3: //awards
                fragment = EventAwardsFragment.newInstance(eventKey, teamKey);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
