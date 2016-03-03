package com.thebluealliance.androidclient;

import com.thebluealliance.androidclient.di.DaggerMockFragmentComponent;
import com.thebluealliance.androidclient.di.MockClickListenerModule;
import com.thebluealliance.androidclient.di.MockSubscriberModule;
import com.thebluealliance.androidclient.di.components.FragmentComponent;
import com.thebluealliance.androidclient.di.components.HasFragmentComponent;

import android.support.v4.app.FragmentActivity;

public class BaseTestActivity extends FragmentActivity implements HasFragmentComponent {

    @Override
    public FragmentComponent getComponent() {
        TestTbaAndroid application = (TestTbaAndroid) getApplication();
        return DaggerMockFragmentComponent.builder()
                .mockApplicationComponent(application.getMockComponent())
                .mockDatafeedModule(application.getMockDatafeedModule())
                .mockBinderModule(application.getMockBinderModule())
                .mockDatabaseWriterModule(application.getMockDatabaseWriterModule())
                .mockSubscriberModule(new MockSubscriberModule())
                .mockClickListenerModule(new MockClickListenerModule())
                .build();
    }
}
