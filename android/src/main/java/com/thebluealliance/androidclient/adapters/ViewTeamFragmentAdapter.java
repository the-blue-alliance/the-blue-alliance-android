package com.thebluealliance.androidclient.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.activities.ViewTeam;
import com.thebluealliance.androidclient.fragments.TeamInfoFragment;

/**
 * Created by Nathan on 4/22/2014.
 */
public class ViewTeamFragmentAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Info", "Events", "Media"};

    private String mTeamKey;

    public ViewTeamFragmentAdapter(FragmentManager fm, String teamKey) {
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
        switch(position) {
            case 0:
                default:
                // This is the info page
                Fragment f = new TeamInfoFragment();
                Bundle args = new Bundle();
                args.putString(ViewTeam.TEAM_KEY, mTeamKey);
                f.setArguments(args);
                return f;
        }

    }
}
