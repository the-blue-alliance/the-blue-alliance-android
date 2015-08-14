package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.WebcastListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class WebcastListSubscriberTest {
    WebcastListSubscriber mSubscriber;
    List<Event> mEvents;

    @Before
    public void setUp() {
        mSubscriber = new WebcastListSubscriber();
        mEvents = ModelMaker.getMultiModelList(Event.class, "2015necmp");
    }

    @Test
    public void testNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mEvents);
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);
        List<WebcastListElement> expected = mEvents.get(0).renderWebcasts();

        assertEquals(expected.size(), data.size());
        for (int i = 0; i < data.size(); i++) {
            assertEquals(expected.get(i), data.get(i));
        }
    }
}