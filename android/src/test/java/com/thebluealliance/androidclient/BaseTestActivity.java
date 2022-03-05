package com.thebluealliance.androidclient;


import androidx.annotation.VisibleForTesting;

import com.thebluealliance.androidclient.activities.DatafeedActivity;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BaseTestActivity extends DatafeedActivity {

    @Inject CacheableDatafeed mDatafeed;

    @VisibleForTesting
    public RefreshController getRefreshController() {
        return mRefreshController;
    }

    @VisibleForTesting
    public TBAStatusController getStatusController() {
        return mStatusController;
    }

    @VisibleForTesting
    public EventBus getEventBus() {
        return mEventBus;
    }

    @VisibleForTesting
    public CacheableDatafeed getDatafeed() {
        return mDatafeed;
    }
}
