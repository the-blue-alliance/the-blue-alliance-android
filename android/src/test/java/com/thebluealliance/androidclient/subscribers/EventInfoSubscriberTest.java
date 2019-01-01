package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.binders.EventInfoBinder;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Event;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DefaultTestRunner.class)
public class EventInfoSubscriberTest extends TestCase {

    private EventInfoSubscriber mSubscriber;
    private Event mEvent;

    @Before
    public void setUp() throws Exception{
        mSubscriber = new EventInfoSubscriber();
        mEvent = ModelMaker.getModel(Event.class, "2015cthar");
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mEvent);
    }

    @Test
    public void testParse()  {
        EventInfoBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mEvent);

        assertEquals(data.eventKey, mEvent.getKey());
        assertEquals(data.nameString, mEvent.getName());
        assertEquals(data.actionBarTitle, mEvent.getShortName());
        assertEquals(data.actionBarSubtitle, String.valueOf(mEvent.getYear()));
        assertEquals(data.venueString, mEvent.getAddress());
        assertEquals(data.locationString, mEvent.getLocationName());
        assertEquals(data.eventWebsite, mEvent.getWebsite());
        assertEquals(data.dateString, mEvent.getDateString());
        assertEquals(data.isLive, mEvent.isHappeningNow());
        assertEquals(data.webcasts.toString(), mEvent.getWebcasts());
    }
}