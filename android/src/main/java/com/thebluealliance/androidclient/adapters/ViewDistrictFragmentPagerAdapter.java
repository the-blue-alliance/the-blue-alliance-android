package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by phil on 7/10/14.
 */
public class ViewDistrictFragmentPagerAdapter extends FragmentPagerAdapter{

    public final String[] TITLES = {"Info", "Events", "Points"};

    private String mDistrictKey;

    public ViewDistrictFragmentPagerAdapter(FragmentManager fm, String districtKey) {
        super(fm);
        mDistrictKey = districtKey;
    }
    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
