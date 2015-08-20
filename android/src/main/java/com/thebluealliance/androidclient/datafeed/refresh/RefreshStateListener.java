package com.thebluealliance.androidclient.datafeed.refresh;

/**
 * Used to listen for refreshing state changes.
 */
public interface RefreshStateListener {
    void onRefreshStateChanged(boolean isRefreshing);
}
