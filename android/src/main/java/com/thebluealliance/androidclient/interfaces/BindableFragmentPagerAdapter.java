package com.thebluealliance.androidclient.interfaces;

/**
 * Created by Nathan on 8/10/2015.
 */
public interface BindableFragmentPagerAdapter {

    void bindFragmentAtPosition(int position);
    int getCount();
}
