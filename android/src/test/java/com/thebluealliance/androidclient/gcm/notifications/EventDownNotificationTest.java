package com.thebluealliance.androidclient.gcm.notifications;

import android.content.Context;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

@Ignore
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
        mNotification.parseMessageData();
        mNotification.buildNotification(context, null);

        assertEquals(context.getString(R.string.notification_event_down, KEY), mNotification.getTitle());
        assertEquals(context.getString(R.string.notification_event_down_content, "Down Event"), mNotification.getMessage());
    }
}