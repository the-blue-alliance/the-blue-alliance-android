package com.thebluealliance.androidclient.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.fragments.EventListFragment;
import com.thebluealliance.androidclient.fragments.team.TeamInfoFragment;

/**
 * Created by Nathan on 4/22/2014.
 */
public class ViewTeamFragmentPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Info", "Events", "Media"};

    private String mTeamKey;

    public ViewTeamFragmentPagerAdapter(FragmentManager fm, String teamKey) {
        super(fm);
        mTeamKey = teamKey;
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
                Fragment f = new TeamInfoFragment();
                Bundle args = new Bundle();
                args.putString(ViewTeamActivity.TEAM_KEY, mTeamKey);
                f.setArguments(args);
                return f;
            case 1: // events
                return EventListFragment.newInstance(2014, -1, "frc254");
            case 2: // media
            default:
                return new Fragment();
        }

    }
}
