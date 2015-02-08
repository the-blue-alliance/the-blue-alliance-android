package com.thebluealliance.androidclient.test.notifications;

import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.CompLevelStartingNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by phil on 2/7/15.
 */
@RunWith(RobolectricTestRunner.class)
public class CompLevelStartingNotificationTest {
    BaseNotification notification;
    String messageData = "{\"event_name\": \"New England FRC Region Championship\", " +
            "\"comp_level\": \"f\", " +
            "\"event_key\": \"2014necmp\", " +
            "\"scheduled_time\": 1397330280}\n";
    
    @Before
    public void setupNotification(){
        notification = new CompLevelStartingNotification(messageData);
    }
    
    @Test
    public void testParseJson(){
        notification.parseMessageData();
    }
}
