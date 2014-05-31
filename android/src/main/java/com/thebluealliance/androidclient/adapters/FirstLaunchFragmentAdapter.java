package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.firstlaunch.LoadFinishedFragment;
import com.thebluealliance.androidclient.fragments.firstlaunch.LoadingFragment;
import com.thebluealliance.androidclient.fragments.firstlaunch.WelcomeFragment;

/**
 * Created by Nathan on 4/22/2014.
 */
public class FirstLaunchFragmentAdapter extends FragmentPagerAdapter {

    private int mCount = 3;

    public FirstLaunchFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new WelcomeFragment();
            case 1:
                return new LoadingFragment();
            case 2:
                return new LoadFinishedFragment();
            default:
                return new Fragment();
        }
    }
}
