package com.thebluealliance.androidclient.notifications;

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

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GenericNotificationTest {
    private GenericNotification mNotification;
    private JsonObject mData;

    @Before
    public void setUp() {
        mData = ModelMaker.getModel(JsonObject.class, "notification_ping");
        mNotification = new GenericNotification(NotificationTypes.BROADCAST, mData.toString());
    }

    @Test
    public void testParseData() {
        mNotification.parseMessageData();

        assertEquals(mData.get(GenericNotification.TITLE).getAsString(), mNotification.getTitle());
        assertEquals(mData.get(GenericNotification.DESC).getAsString(), mNotification.getMessage());
    }
}