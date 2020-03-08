package com.thebluealliance.androidclient.gcm;

import com.thebluealliance.androidclient.TestTbaAndroid;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.di.DaggerMockNotificationComponent;
import com.thebluealliance.androidclient.di.MockRendererModule;

public class GCMMessageHandlerWithMocks extends GCMMessageHandler {

    /*
     * A version of the service that does its DI using mocks
     */

    @Override
    protected void inject() {
        TestTbaAndroid application = ((TestTbaAndroid) getApplication());
        DaggerMockNotificationComponent.builder()
                .mockApplicationComponent(application.getMockComponent())
                .mockDatafeedModule(application.getMockDatafeedModule())
                .mockRendererModule(new MockRendererModule())
                .mockAuthModule(application.getMockAuthModule())
                .mockGcmModule(application.getMockGcmModule())
                .build()
                .inject(this);
        initMocks();
    }

    public void initMocks() {
        DatabaseMocker.mockMatchesTable(mDb);
        DatabaseMocker.mockEventsTable(mDb);
        DatabaseMocker.mockAwardsTable(mDb);
        DatabaseMocker.mockFavoritesTable(mDb);
        DatabaseMocker.mockSubscriptionsTable(mDb);
        DatabaseMocker.mockNotificationsTable(mDb);
    }
}
