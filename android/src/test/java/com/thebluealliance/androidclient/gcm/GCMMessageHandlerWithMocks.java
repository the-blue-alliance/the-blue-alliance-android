package com.thebluealliance.androidclient.gcm;

import android.app.Notification;
import android.content.Context;

import com.thebluealliance.androidclient.TestTbaAndroid;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.di.DaggerMockNotificationComponent;
import com.thebluealliance.androidclient.di.MockRendererModule;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

import javax.annotation.Nullable;

public class GCMMessageHandlerWithMocks extends GCMMessageHandler {

    /*
     * A version of the service that does its DI using mocks
     */

    private BaseNotification mLastNotification;

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

    @Override
    protected void notify(Context c, BaseNotification notification, Notification built) {
        super.notify(c, notification, built);
        mLastNotification = notification;
    }

    @Nullable
    public BaseNotification getLastNotification() {
        return mLastNotification;
    }
}
