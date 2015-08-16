package com.thebluealliance.androidclient.notifications;

import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.UpcomingMatchNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by phil on 2/7/15.
 */
@RunWith(RobolectricTestRunner.class)
public class UpcomingMatchNotificationTest {
    BaseNotification notification;
    String messageData = "{ \"event_name\" : \"New England FRC Region Championship\",\n" +
            "  \"match_key\" : \"2014necmp_f1m1\",\n" +
            "  \"predicted_time\" : 1397330280,\n" +
            "  \"scheduled_time\" : 1397330280,\n" +
            "  \"team_keys\" : [ \"frc195\",\n" +
            "      \"frc558\",\n" +
            "      \"frc5122\",\n" +
            "      \"frc177\",\n" +
            "      \"frc230\",\n" +
            "      \"frc4055\"\n" +
            "    ]\n" +
            "}";
    
    @Before
    public void setupNotification(){
        notification = new UpcomingMatchNotification(messageData);
    }
    
    @Test
    public void testParseJson(){
        notification.parseMessageData();
    }
}
