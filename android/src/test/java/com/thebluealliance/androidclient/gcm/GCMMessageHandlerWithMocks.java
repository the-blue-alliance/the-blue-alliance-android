package com.thebluealliance.androidclient.gcm;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.models.StoredNotification;

import java.util.List;

import javax.annotation.Nullable;

public class GCMMessageHandlerWithMocks extends GCMMessageHandler {

    /*
     * A version of the service that does its DI using mocks
     */

    private BaseNotification mLastNotification;

    @Override
    protected void notify(Context c, BaseNotification notification, Notification built, List<StoredNotification> activeNotifications) {
        super.notify(c, notification, built, activeNotifications);
        mLastNotification = notification;
    }

    @Override
    public boolean handleIntentOnMainThread(@NonNull Intent intent) {
        return true;
    }

    @Nullable
    public BaseNotification getLastNotification() {
        return mLastNotification;
    }

    public Database getDatabase() {
        return mDb;
    }
}
