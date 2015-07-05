package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.TeamListFragment;

/**
 * Created by Nathan on 4/22/2014.
 */
public class TeamListFragmentPagerAdapter extends FragmentPagerAdapter {

    //TODO: don't hardcode this, use value from database
    private int mCount = 6;

    public TeamListFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "1-999";
            default:
                return (position * 1000) + "-" + ((position * 1000) + 999);
        }

    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        return TeamListFragment.newInstance(position * 1000);
    }
}
