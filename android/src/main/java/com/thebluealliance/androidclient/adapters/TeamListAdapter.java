package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.TeamListFragment;

/**
 * Created by Nathan on 4/22/2014.
 */
public class TeamListAdapter extends FragmentPagerAdapter {

    //TODO: don't hardcode this, use value from database
    private int mCount = 6;

    public TeamListAdapter(FragmentManager fm) {
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

    // This is necessary to recreate all fragments when we call notifyDataSetChanged()
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        return new TeamListFragment(position * 1000, (position * 1000) + 999);
    }
}
