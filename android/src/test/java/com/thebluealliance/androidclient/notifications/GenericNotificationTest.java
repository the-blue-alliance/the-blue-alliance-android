package com.thebluealliance.androidclient.notifications;

import android.content.Context;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.gcm.notifications.GenericNotification;
import com.thebluealliance.androidclient.gcm.notifications.NotificationTypes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GenericNotificationTest {
    private GenericNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        Context context = mock(Context.class, RETURNS_DEEP_STUBS);
        mData = ModelMaker.getModel(JsonObject.class, "notification_ping");
        mNotification = new GenericNotification(context, NotificationTypes.BROADCAST, mData.toString());
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();

        assertEquals(mData.get(GenericNotification.TITLE).getAsString(), mNotification.getTitle());
        assertEquals(mData.get(GenericNotification.TEXT).getAsString(), mNotification.getMessage());
        assertNotNull(mNotification.getContentIntent());
    }
}