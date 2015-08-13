package com.thebluealliance.androidclient.adapters;

import android.support.v4.view.ViewPager;

import javax.inject.Inject;

public class FragmentBindController implements ViewPager.OnPageChangeListener {

    DatafeedFragmentPagerAdapter mAdapter;

    @Inject
    public FragmentBindController(DatafeedFragmentPagerAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mAdapter.bindAtPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
