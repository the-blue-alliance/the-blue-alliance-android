package com.thebluealliance.androidclient.notifications;

import com.thebluealliance.androidclient.gcm.notifications.AwardsPostedNotification;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by phil on 2/7/15.
 */
@RunWith(RobolectricTestRunner.class)
public class AwardsPostedNotificationTest {
    BaseNotification notification;
    String messageData = "{ \"awards\" : [ " +
            "      { \"award_type\" : 0,\n" +
            "        \"event_key\" : \"2014necmp\",\n" +
            "        \"name\" : \"Regional Chairman's Award\",\n" +
            "        \"recipient_list\" : [ { \"awardee\" : null,\n" +
            "              \"team_number\" : 2067\n" +
            "            },\n" +
            "            { \"awardee\" : null,\n" +
            "              \"team_number\" : 78\n" +
            "            },\n" +
            "            { \"awardee\" : null,\n" +
            "              \"team_number\" : 811\n" +
            "            },\n" +
            "            { \"awardee\" : null,\n" +
            "              \"team_number\" : 2648\n" +
            "            }\n" +
            "          ],\n" +
            "        \"year\" : 2014\n" +
            "      },\n" +
            "      { \"award_type\" : 30,\n" +
            "        \"event_key\" : \"2014necmp\",\n" +
            "        \"name\" : \"Team Spirit Award sponsored by Chrysler\",\n" +
            "        \"recipient_list\" : [ { \"awardee\" : null,\n" +
            "              \"team_number\" : 228\n" +
            "            } ],\n" +
            "        \"year\" : 2014\n" +
            "      }\n" +
            "    ],\n" +
            "  \"event_key\" : \"2014necmp\",\n" +
            "  \"event_name\" : \"New England FRC Region Championship\"\n" +
            "}";
    
    @Before
    public void setupNotification(){
        notification = new AwardsPostedNotification(messageData);
    }
    
    @Test
    public void testParseJson(){
        notification.parseMessageData();
    }
}