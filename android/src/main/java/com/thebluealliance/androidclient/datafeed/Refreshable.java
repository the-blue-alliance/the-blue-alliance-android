package com.thebluealliance.androidclient.datafeed;

/**
 * An interface for an object that can be considered refreshable (it can be signaled to start
 * refreshing its content).
 * <p>
 * Created by Nathan on 6/4/2015.
 */
public interface Refreshable {

    /**
     * Indicates that this object should start refreshing its content.
     *
     * @param requestedByUser true if this refresh was explicitly requested by the user; for
     *                        instance, if they clicked a refresh button
     */
    void onRefreshStart(boolean requestedByUser);
}
