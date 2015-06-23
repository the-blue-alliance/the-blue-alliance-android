package com.thebluealliance.androidclient.fragments;

import android.support.v4.app.Fragment;

import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.DataConsumer;

import javax.inject.Inject;

/**
 * Easy abstraction of datafeed bindings
 */
public abstract class DatafeedFragment<T, V> extends Fragment implements DataConsumer<T> {

    @Inject protected CacheableDatafeed mDatafeed;

}
