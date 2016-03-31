package com.thebluealliance.androidclient;


import com.thebluealliance.androidclient.activities.DatafeedActivity;
import com.thebluealliance.androidclient.datafeed.CacheableDatafeed;
import com.thebluealliance.androidclient.datafeed.refresh.RefreshController;
import com.thebluealliance.androidclient.datafeed.status.TBAStatusController;
import com.thebluealliance.androidclient.di.DaggerMockFragmentComponent;
import com.thebluealliance.androidclient.di.MockClickListenerModule;
import com.thebluealliance.androidclient.di.MockFragmentComponent;
import com.thebluealliance.androidclient.di.MockSubscriberModule;
import com.thebluealliance.androidclient.di.components.FragmentComponent;

import org.greenrobot.eventbus.EventBus;

import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;

public class BaseTestActivity extends DatafeedActivity {

    private MockFragmentComponent mMockComponent;

    @Inject CacheableDatafeed mDatafeed;

    @Override
    public void inject() {
        getComponent();
        mMockComponent.inject(this);
    }

    @Override
    public FragmentComponent getComponent() {
        if (mMockComponent == null) {
            TestTbaAndroid application = (TestTbaAndroid) getApplication();
            mMockComponent = DaggerMockFragmentComponent.builder()
                    .mockApplicationComponent(application.getMockComponent())
                    .mockDatafeedModule(application.getMockDatafeedModule())
                    .mockBinderModule(application.getMockBinderModule())
                    .mockDatabaseWriterModule(application.getMockDatabaseWriterModule())
                    .mockSubscriberModule(new MockSubscriberModule())
                    .mockClickListenerModule(new MockClickListenerModule())
                    .build();
        }
        return mMockComponent;
    }

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
