package com.thebluealliance.androidclient.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.gameday.GamedayWebcastsFragment;

public class GamedayFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final String[] TITLES = {"Webcasts"};
    public static final int TAB_WEBCASTS = 1;

    public GamedayFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
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
            /*case TAB_TICKER:
                fragment = GamedayTickerFragment.newInstance();
                break;
            */
            case TAB_WEBCASTS:
                fragment = GamedayWebcastsFragment.newInstance();
                break;
        }
        return fragment;
    }
}
