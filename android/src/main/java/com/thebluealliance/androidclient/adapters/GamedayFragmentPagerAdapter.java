package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.gameday.GamedayTickerFragment;

/**
 * Created by phil on 3/26/15.
 */
public class GamedayFragmentPagerAdapter extends FragmentPagerAdapter {

    public final String[] TITLES = {"Live Ticker", "Webcasts"};
    public static final int TAB_TICKER = 0,
            TAB_WEBCASTS = 1;

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
            case TAB_TICKER: //event info
                fragment = GamedayTickerFragment.newInstance();
                break;
            case TAB_WEBCASTS: //teams
                fragment = new Fragment();
                break;
        }
        return fragment;
    }
}
