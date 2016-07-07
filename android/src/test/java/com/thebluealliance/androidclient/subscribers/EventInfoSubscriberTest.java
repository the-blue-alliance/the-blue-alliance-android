package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventInfo;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventInfoSubscriberTest extends TestCase {

    private EventInfoSubscriber mSubscriber;
    private EventInfo mEventInfo;
    private Event mEvent;

    @Before
    public void setUp() throws Exception{
        mSubscriber = new EventInfoSubscriber();
        mEvent = ModelMaker.getModel(Event.class, "2015necmp");
        JsonArray rankings = ModelMaker.getModel(JsonArray.class, "2015necmp_rankings");
        JsonObject stats = ModelMaker.getModel(JsonObject.class, "2015necmp_stats");
        mEvent.setRankings(rankings);
        mEvent.setStats(stats);
        mEventInfo = new EventInfo();
        mEventInfo.event = mEvent;
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mEventInfo);
    }

    @Test
    public void testParse() throws BasicModel.FieldNotDefinedException {
        EventInfoBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mEventInfo);

        assertEquals(data.eventKey, mEvent.getKey());
        assertEquals(data.nameString, mEvent.getName());
        assertEquals(data.actionBarTitle, mEvent.getShortName());
        assertEquals(data.actionBarSubtitle, String.valueOf(mEvent.getYear()));
        assertEquals(data.venueString, mEvent.getVenue());
        assertEquals(data.locationString, mEvent.getLocation());
        assertEquals(data.eventWebsite, mEvent.getWebsite());
        assertEquals(data.dateString, mEvent.getDateString());
        assertEquals(data.isLive, mEvent.isHappeningNow());
        assertEquals(data.webcasts, mEvent.getWebcasts());
    }
}