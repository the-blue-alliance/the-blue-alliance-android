package com.thebluealliance.androidclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thebluealliance.androidclient.fragments.DatafeedFragment;

public abstract class DatafeedFragmentPagerAdapter extends FragmentPagerAdapter {

    private DatafeedFragment[] mFragments;

    public DatafeedFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new DatafeedFragment[getCount()];
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments[position] == null) {
            mFragments[position] = getDatafeedItem(position);
            mFragments[position].setShouldBindImmediately(false);
        }
        return mFragments[position];
    }

    /**
     * Binds parsed data to the view for a certain fragment
     * @param position Which fragment to bind (must have been "gotten" already)
     */
    public void bindAtPosition(int position) {
        DatafeedFragment fragment = mFragments[position];
        if (fragment != null) {
            fragment.bind();
        }
    }

    public abstract DatafeedFragment getDatafeedItem(int position);
}
