package com.thebluealliance.androidclient.adapters;

import com.thebluealliance.androidclient.fragments.match.MatchBreakdownFragment;
import com.thebluealliance.androidclient.fragments.match.MatchInfoFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewMatchFragmentPagerAdapter extends FragmentPagerAdapter {

    public final String[] TITLES = {"Results", "Breakdown"};
    public static final int TAB_RESULT = 0,
            TAB_BREAKDOWN = 1;

    private String mMatchKey;

    public ViewMatchFragmentPagerAdapter(FragmentManager fm, String matchKey) {
        super(fm);
        mMatchKey = matchKey;
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
            case TAB_RESULT:
                fragment = MatchInfoFragment.newInstance(mMatchKey);
                break;
            case TAB_BREAKDOWN:
                fragment = MatchBreakdownFragment.newInstance(mMatchKey);
                break;
            default:
                fragment = new Fragment();
                break;
        }
        return fragment;
    }
}
