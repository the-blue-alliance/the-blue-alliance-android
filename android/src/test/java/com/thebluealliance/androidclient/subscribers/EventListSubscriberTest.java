package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.renderers.EventRenderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class EventListSubscriberTest {

    @Mock APICache mCache;

    private EventListSubscriber mSubscriber;
    private Context mContext;
    private EventRenderer mRenderer;
    private List<Event> mEvents;
    private List<Object> mExpected;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mContext = mock(Context.class, RETURNS_DEEP_STUBS);

        mEvents = ModelMaker.getModelList(Event.class, "2015_events");
        mRenderer = new EventRenderer(mCache);
        mSubscriber = new EventListSubscriber(mContext);
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
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        EventHelper.renderEventListForWeek(mContext, mEvents, mExpected);

        assertListsEqual(data);
    }

    @Test
    public void testParseTeam() throws BasicModel.FieldNotDefinedException {
        mSubscriber.setRenderMode(EventListSubscriber.MODE_TEAM);
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        EventHelper.renderEventListForWeek(mContext, mEvents, mExpected);

        assertListsEqual(data);
    }

    @Test
    public void testParseDistrict() throws BasicModel.FieldNotDefinedException {
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