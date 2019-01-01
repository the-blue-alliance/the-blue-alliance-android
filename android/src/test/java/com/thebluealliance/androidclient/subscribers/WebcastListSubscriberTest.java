package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.WebcastListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.renderers.EventRenderer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(DefaultTestRunner.class)
public class WebcastListSubscriberTest {

    @Mock APICache mCache;

    WebcastListSubscriber mSubscriber;
    EventRenderer mRenderer;
    List<Event> mEvents;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mRenderer = new EventRenderer(mCache);
        mSubscriber = new WebcastListSubscriber(mRenderer);
        mEvents = ModelMaker.getMultiModelList(Event.class, "2015necmp");
    }

    @Test
    public void testNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mEvents);
    }

    @Test
    public void testParsedData()  {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        List<WebcastListElement> expected = mRenderer.renderWebcasts(mEvents.get(0));

        assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++) {
            assertEquals(expected.get(i), data.get(i));
        }
    }
}