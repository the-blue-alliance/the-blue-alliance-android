package com.thebluealliance.androidclient.test.datafeed;

import android.test.suitebuilder.annotation.MediumTest;

import com.thebluealliance.androidclient.datafeed.JSONManager;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import junit.framework.TestCase;

import java.util.Date;

/**
 * File created by phil on 5/8/14.
 */
public class TestTBAApi extends TestCase {

    @MediumTest
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

    @MediumTest
    public void testParseTeam(){
        //TODO implement once https://github.com/the-blue-alliance/the-blue-alliance/issues/1012 happens
    }

    @MediumTest
    public void testParseMatch(){
        String matchJson = "{\"comp_level\": \"f\", \"match_number\": 1, \"videos\": [{\"type\": \"youtube\", \"key\": \"ci6LicTg5rk\"}], \"time_string\": \"3:36 PM\", \"set_number\": 1, \"key\": \"2014ctgro_f1m1\", \"time\": \"1394393760\", \"alliances\": {\"blue\": {\"score\": 113, \"teams\": [\"frc1991\", \"frc230\", \"frc1699\"]}, \"red\": {\"score\": 120, \"teams\": [\"frc236\", \"frc237\", \"frc2064\"]}}, \"event_key\": \"2014ctgro\"}";
        Match match = JSONManager.getGson().fromJson(matchJson, Match.class);

        assertEquals(match.getKey(), "2014ctgro_f1m1");
        assertEquals(match.getEventKey(), "2014ctgro");
        assertEquals(match.getMatchNumber(), 1);
        assertEquals(match.getSetNumber(), 1);
        assertEquals(match.getType(), Match.TYPE.FINAL);
        assertEquals(match.getAlliances(), JSONManager.getasJsonObject("{\"blue\": {\"score\": 113, \"teams\": [\"frc1991\", \"frc230\", \"frc1699\"]}, \"red\": {\"score\": 120, \"teams\": [\"frc236\", \"frc237\", \"frc2064\"]}}"));
        assertEquals(match.getTimeString(), "3:36 PM");
        assertEquals(match.getTime(), new Date(1394393760));
        assertEquals(match.getVideos(), JSONManager.getasJsonArray("[{\"type\": \"youtube\", \"key\": \"ci6LicTg5rk\"}]"));
    }

}
