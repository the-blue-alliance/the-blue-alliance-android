package com.thebluealliance.androidclient.fragments;

import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;

import javax.inject.Inject;

/**
 * Easy abstraction of datafeed bindings
 */
public abstract class DatafeedFragment<T, V> extends Fragment {

    @Inject protected CacheableDatafeed mDatafeed;

}
