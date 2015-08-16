package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class EventListSubscriberTest {

    private EventListSubscriber mSubscriber;
    private List<Event> mEvents;
    private List<ListItem> mExpected;

    @Before
    public void setUp() {
        mSubscriber = new EventListSubscriber();
        mEvents = ModelMaker.getModelList(Event.class, "2015_events");
        mExpected = new ArrayList<>();
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mEvents);
    }

    @Test
    public void testParseWeek() throws BasicModel.FieldNotDefinedException {
        mSubscriber.setRenderMode(EventListSubscriber.MODE_WEEK);
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        EventHelper.renderEventListForWeek(mEvents, mExpected);

        assertListsEqual(data);
    }

    @Test
    public void testParseTeam() throws BasicModel.FieldNotDefinedException {
        mSubscriber.setRenderMode(EventListSubscriber.MODE_TEAM);
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        EventHelper.renderEventListForWeek(mEvents, mExpected);

        assertListsEqual(data);
    }

    @Test
    public void testParseDistrict() throws BasicModel.FieldNotDefinedException {
        mSubscriber.setRenderMode(EventListSubscriber.MODE_DISTRICT);
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        EventHelper.renderEventListForDistrict(mEvents, mExpected);

        assertListsEqual(data);
    }

    private void assertListsEqual(List<ListItem> actual) {
        assertEquals(actual.size(), mExpected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertTrue(actual.get(i).equals(mExpected.get(i)));
        }
    }
}