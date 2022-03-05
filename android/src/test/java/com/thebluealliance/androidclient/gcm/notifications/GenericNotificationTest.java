package com.thebluealliance.androidclient.gcm.notifications;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
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