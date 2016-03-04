package com.thebluealliance.androidclient.datafeed.refresh;

import android.support.annotation.UiThread;

/**
 * Used to listen for refreshing state changes.
 */
public interface RefreshStateListener {

    @UiThread
    void onRefreshStateChanged(boolean isRefreshing);
}
