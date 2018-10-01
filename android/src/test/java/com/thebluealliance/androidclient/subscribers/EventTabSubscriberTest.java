package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventWeekTab;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class EventTabSubscriberTest {

    EventTabSubscriber mSubscriber;
    List<Event> mEvents;

    @Before
    public void setUp() {
        mSubscriber = new EventTabSubscriber();
        mEvents = ModelMaker.getModelList(Event.class, "2015_events");
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
    public void testParsedData()  {
        int[] weeks = {0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 15, 21, 25, 28, 32, 37};
        int[] months = {-1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 5, 6, 7, 8, 9, 10};
        String[] labels = {"Preseason Events", "Week 1", "Week 2", "Week 3", "Week 4", "Week 5",
          "Week 6", "Week 7", "Championship Event", "May Offseason Events", "Jun Offseason Events",
          "Jul Offseason Events", "Aug Offseason Events", "Sep Offseason Events",
          "Oct Offseason Events", "Nov Offseason Events"};
        List<EventWeekTab> tabs = DatafeedTestDriver.getParsedData(mSubscriber, mEvents);

        assertEquals(tabs.size(), 16);
        for (int i = 0; i < tabs.size(); i++) {
            EventWeekTab tab = tabs.get(i);
            assertEquals(String.format("Tab %1$d week fail", i), weeks[i], tab.getWeek());
            assertEquals(String.format("Tab %1$d month fail", i), months[i], tab.getMonth());
            assertEquals(String.format("Tab %1$d label fail", i), labels[i], tab.getLabel());
        }
    }
}