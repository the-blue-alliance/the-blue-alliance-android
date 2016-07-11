package com.thebluealliance.androidclient.datafeed;

import com.thebluealliance.androidclient.models.BasicModel;

import android.support.annotation.Nullable;

/**
 * An interface that a {@link rx.Subscriber} in the package
 * {@link com.thebluealliance.androidclient.subscribers} also implements in order to provide
 * access to the data fetched so that it can be bound to views
 * @param <T> Type of the data to display (e.g. a model or
 * {@link com.thebluealliance.androidclient.adapters.ListViewAdapter}
 */
public interface APISubscriber<T> {
    /**
     * Parse data from the API and construct the values to return, if necessary
     * For example, {@link com.thebluealliance.androidclient.subscribers.BaseAPISubscriber} will
     * not call this method unless the data is valid (not-null), but this is not a hard guarantee
     * and should be handled
     * Usually creates a {@link com.thebluealliance.androidclient.adapters.ListViewAdapter}
     */
    void parseData() throws BasicModel.FieldNotDefinedException;

    /**
     * Callback for displaying data to be viewed
     * @return The data to be bound to the view
     */
    @Nullable T getBoundData();
}
