package com.thebluealliance.androidclient.gcm.notifications;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

@RunWith(AndroidJUnit4.class)
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
        mNotification.parseMessageData();
        mNotification.buildNotification(context, null);

        assertEquals(context.getString(R.string.notification_event_down, KEY), mNotification.getTitle());
        assertEquals(context.getString(R.string.notification_event_down_content, "Down Event"), mNotification.getMessage());
    }
}