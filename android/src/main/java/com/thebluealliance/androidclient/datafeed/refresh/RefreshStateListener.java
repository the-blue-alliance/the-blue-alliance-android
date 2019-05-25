package com.thebluealliance.androidclient.datafeed.refresh;

import androidx.annotation.UiThread;

/**
 * Used to listen for refreshing state changes.
 */
public interface RefreshStateListener {

    @UiThread
    void onRefreshStateChanged(boolean isRefreshing);
}
