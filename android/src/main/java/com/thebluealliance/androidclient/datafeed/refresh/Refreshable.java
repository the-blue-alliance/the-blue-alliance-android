package com.thebluealliance.androidclient.datafeed.refresh;

import com.thebluealliance.androidclient.datafeed.refresh.RefreshController.RefreshType;

/**
 * An interface for an object that can be considered refreshable (it can be signaled to start
 * refreshing its content).
 */
public interface Refreshable {

    /**
     * Indicates that this object should start refreshing its content.
     */
    void onRefreshStart(@RefreshType int refreshType);
}
