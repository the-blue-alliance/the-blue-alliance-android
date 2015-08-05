package com.thebluealliance.androidclient.notifications;

import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;
import com.thebluealliance.androidclient.gcm.notifications.ScoreNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by phil on 2/7/15.
 */
@RunWith(RobolectricTestRunner.class)
public class ScoreNotificationTest {
    BaseNotification notification;
    String messageData = "{ \"event_name\" : \"New England FRC Region Championship\",\n" +
            "  \"match\" : { \"alliances\" : { \"blue\" : { \"score\" : 154,\n" +
            "              \"teams\" : [ \"frc177\",\n" +
            "                  \"frc230\",\n" +
            "                  \"frc4055\"\n" +
            "                ]\n" +
            "            },\n" +
            "          \"red\" : { \"score\" : 78,\n" +
            "              \"teams\" : [ \"frc195\",\n" +
            "                  \"frc558\",\n" +
            "                  \"frc5122\"\n" +
            "                ]\n" +
            "            }\n" +
            "        },\n" +
            "      \"comp_level\" : \"f\",\n" +
            "      \"event_key\" : \"2014necmp\",\n" +
            "      \"key\" : \"2014necmp_f1m1\",\n" +
            "      \"match_number\" : 1,\n" +
            "      \"score_breakdown\" : null,\n" +
            "      \"set_number\" : 1,\n" +
            "      \"time\" : \"1397330280\",\n" +
            "      \"time_string\" : \"3:18 PM\",\n" +
            "      \"videos\" : [  ]\n" +
            "    }\n" +
            "}";
    
    @Before
    public void setupNotification(){
        notification = new ScoreNotification(messageData);
    }
    
    @Test
    public void testParseJson(){
        notification.parseMessageData();
    }
}
