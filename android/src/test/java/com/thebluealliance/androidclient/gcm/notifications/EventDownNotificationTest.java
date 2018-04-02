package com.thebluealliance.androidclient.gcm.notifications;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

@RunWith(DefaultTestRunner.class)
public class EventDownNotificationTest {

    private static final String KEY = "DOWN";

    private EventDownNotification mNotification;
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        JsonObject data = ModelMaker.getModel(JsonObject.class, "notification_event_down");
        mNotification = new EventDownNotification(data.toString());
    }

    @Test
    public void testParseData() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "TestChannel");
        mNotification.parseMessageData();
        mNotification.buildStoredNotification(context, builder, null);

        assertEquals(context.getString(R.string.notification_event_down, KEY), mNotification.getTitle());
        assertEquals(mNotification.getTitle(), builder.mContentTitle);
        assertEquals(context.getString(R.string.notification_event_down_content, "Down Event"), mNotification.getMessage());
    }
}