package com.thebluealliance.androidclient;

import com.thebluealliance.androidclient.di.DaggerMockApplicationComponent;
import com.thebluealliance.androidclient.di.DaggerMockDatafeedComponent;
import com.thebluealliance.androidclient.di.MockApplicationComponent;
import com.thebluealliance.androidclient.di.MockBinderModule;
import com.thebluealliance.androidclient.di.MockDatabaseWriterModule;
import com.thebluealliance.androidclient.di.MockDatafeedComponent;
import com.thebluealliance.androidclient.di.MockDatafeedModule;
import com.thebluealliance.androidclient.di.MockTbaAndroidModule;

public class TestTbaAndroid extends TBAAndroid {

    private MockApplicationComponent mComponent;
    private MockTbaAndroidModule mModule;
    private MockDatafeedModule mDatafeedModule;
    private MockBinderModule mBinderModule;
    private MockDatabaseWriterModule mDatabaseWriterModule;

    @Override
    public void onCreate() {
        setShouldBindStetho(false);
        super.onCreate();
    }

    public MockTbaAndroidModule getMockModule() {
        if (mModule == null) {
            mModule = new MockTbaAndroidModule();
        }
        return mModule;
    }

    public MockDatafeedModule getMockDatafeedModule() {
        if (mDatafeedModule == null) {
            mDatafeedModule = new MockDatafeedModule();
        }
        return mDatafeedModule;
    }

    public MockBinderModule getMockBinderModule() {
        if (mBinderModule == null) {
            mBinderModule = new MockBinderModule();
        }
        return mBinderModule;
    }

    public MockDatabaseWriterModule getMockDatabaseWriterModule() {
        if (mDatabaseWriterModule == null) {
            mDatabaseWriterModule = new MockDatabaseWriterModule();
        }
        return mDatabaseWriterModule;
    }

    public MockApplicationComponent getMockComponent() {
        if (mComponent == null) {
            mComponent = DaggerMockApplicationComponent.builder()
                    .mockTbaAndroidModule(getMockModule())
                    .build();
        }
        return mComponent;
    }

    private MockDatafeedComponent getMockDatafeedComponenet() {
        return DaggerMockDatafeedComponent.builder()
                .mockApplicationComponent(getMockComponent())
                .mockDatafeedModule(getMockDatafeedModule())
                .build();
    }
}
