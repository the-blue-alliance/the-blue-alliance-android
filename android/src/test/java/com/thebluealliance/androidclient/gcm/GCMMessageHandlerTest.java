package com.thebluealliance.androidclient.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;

import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GCMMessageHandlerTest {

    private NotificationManager mNotificationManager;

    @Before
    public void setUp() {
        Context applicationContext = ApplicationProvider.getApplicationContext();
        mNotificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Test
    public void testPostNotification() {
        Intent intent = buildIntent();
        GCMMessageHandlerWithMocks service = Robolectric.setupService(GCMMessageHandlerWithMocks.class);
        service.onCreate();
        service.onHandleWork(intent);

        List<Notification> notification = Shadows.shadowOf(mNotificationManager).getAllNotifications();
        assertEquals(1, notification.size());
    }


    private Intent buildIntent() {
        JsonObject notificationData = ModelMaker.getModel(JsonObject.class, "notification_upcoming_match");
        Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");
        intent.putExtra("notification_type", "upcoming_match");
        intent.putExtra("message_data", notificationData.toString());
        return intent;
    }
}
