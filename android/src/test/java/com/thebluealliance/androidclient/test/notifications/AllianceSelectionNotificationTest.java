package com.thebluealliance.androidclient.test.notifications;

import com.thebluealliance.androidclient.gcm.notifications.AllianceSelectionNotification;
import com.thebluealliance.androidclient.gcm.notifications.BaseNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by phil on 2/7/15.
 */
@RunWith(RobolectricTestRunner.class)
public class AllianceSelectionNotificationTest {
    
    BaseNotification notification;
    
    String messageData = 
            "{\"event\": " +
                "{\"key\": \"2014necmp\", \"website\": \"http://www.nefirst.org/\", \"official\": true, \"end_date\": \"2014-04-12\", " +
                    "\"name\": \"New England FRC Region Championship\", \"short_name\": \"New England\", \"facebook_eid\": null, " +
                    "\"event_district_string\": \"New England\", \"venue_address\": \"Boston University\\nAgganis Arena\\nBoston, MA 02215\\nUSA\", " +
                    "\"event_district\": 3, \"location\": \"Boston, MA, USA\", \"event_code\": \"necmp\", \"year\": 2014, \"webcast\": [], " +
                    "\"alliances\": [], \"event_type_string\": \"District Championship\", \"start_date\": \"2014-04-10\", \"event_type\": 2" +
                "}" +
            "}";
    
    @Before
    public void setupNotification(){
        notification = new AllianceSelectionNotification(messageData);
    }
    
    @Test
    public void testParseJson(){
        notification.parseMessageData();
    }
}
