package com.thebluealliance.androidclient.test.notifications;

import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScheduleUpdatedNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by phil on 2/7/15.
 */
@RunWith(RobolectricTestRunner.class)
public class ScheduleUpdatedNotificationTest {
    BaseNotification notification;
    String messageData = "{\"event_name\": \"New England FRC Region Championship\", " +
            "\"first_match_time\": 1397330280, " +
            "\"event_key\": \"2014necmp\"}";
    
    @Before
    public void setNotification(){
        notification = new ScheduleUpdatedNotification(messageData);
    }
    
    @Test
    public void testParseJson(){
        notification.parseMessageData();
    }
}
