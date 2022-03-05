package com.thebluealliance.androidclient.subscribers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.List;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class EventListSubscriberTest {

    @Mock APICache mCache;

    private EventListSubscriber mSubscriber;
    private Context mContext;
    private List<Event> mEvents;
    private List<Object> mExpected;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mContext = RuntimeEnvironment.application;

        mEvents = ModelMaker.getModelList(Event.class, "2015_events");
        mSubscriber = new EventListSubscriber(mContext);
        mExpected = new ArrayList<>();
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mEvents);
    }

    @Test
    public void testParseWeek()  {
        mSubscriber.setRenderMode(EventListSubscriber.MODE_WEEK);
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        EventHelper.renderEventListForWeek(mContext, mEvents, mExpected);

        assertListsEqual(data);
    }

    @Test
    public void testParseTeam()  {
        mSubscriber.setRenderMode(EventListSubscriber.MODE_TEAM);
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        EventHelper.renderEventListForWeek(mContext, mEvents, mExpected);

        assertListsEqual(data);
    }

    @Test
    public void testParseDistrict()  {
        mSubscriber.setRenderMode(EventListSubscriber.MODE_DISTRICT);
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        EventHelper.renderEventListForDistrict(mContext, mEvents, mExpected);

        assertListsEqual(data);
    }

    private void assertListsEqual(List<Object> actual) {
        assertEquals(actual.size(), mExpected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertTrue(actual.get(i).equals(mExpected.get(i)));
        }
    }
}