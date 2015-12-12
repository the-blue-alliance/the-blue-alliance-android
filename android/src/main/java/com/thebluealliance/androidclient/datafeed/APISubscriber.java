package com.thebluealliance.androidclient.datafeed;

import android.support.annotation.Nullable;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.models.BasicModel;

/**
 * An interface that a {@link rx.Subscriber} in the package
 * {@link com.thebluealliance.androidclient.subscribers} also implements in order to provide
 * access to the data fetched so that it can be bound to views
 *
 * @param <T> Type of the data to display (e.g. a model or {@link ListViewAdapter}
 */
public interface APISubscriber<T> {
    /**
     * Parse data from the API and construct the values to return, if necessary
     * Usually creates a {@link ListViewAdapter}
     */
    void parseData() throws BasicModel.FieldNotDefinedException;

    /**
     * Callback for displaying data to be viewed
     *
     * @return The data to be bound to the view
     */
    @Nullable
    T getBoundData();
}
