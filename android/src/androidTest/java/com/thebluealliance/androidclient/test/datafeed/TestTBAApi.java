package com.thebluealliance.androidclient.test.datafeed;

import android.test.suitebuilder.annotation.LargeTest;

import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.models.Event;

import junit.framework.TestCase;

import java.util.Date;

/**
 * File created by phil on 5/8/14.
 */
public class TestTBAApi extends TestCase {

    @LargeTest
    public void testParseEvent(){
        String eventJson = "{\"key\": \"2014ctgro\", \"end_date\": \"2014-03-09\", \"name\": \"Groton District Event\", \"short_name\": \"Groton\", \"facebook_eid\": null, \"official\": true, \"location\": \"Groton, CT, USA\", \"event_code\": \"ctgro\", \"year\": 2014, \"event_type_string\": \"District\", \"start_date\": \"2014-03-08\", \"event_type\": 1}";
        Event event = JSONManager.getGson().fromJson(eventJson, Event.class);

        //now, assert that all the properties are there
        assertEquals(event.getEventKey(),"2014ctgro");
        assertEquals(event.getStartDate(), new Date(114,2,8));
        assertEquals(event.getEndDate(), new Date(114,2,9));
        assertEquals(event.getEventName(), "Groton District Event");
        assertEquals(event.getShortName(), "Groton");
        assertEquals(event.isOfficial(), true);
        assertEquals(event.getLocation(), "Groton, CT, USA");
        assertEquals(event.getEventType(), Event.TYPE.DISTRICT);
    }
}
