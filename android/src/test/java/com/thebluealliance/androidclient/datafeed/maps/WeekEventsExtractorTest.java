package com.thebluealliance.androidclient.datafeed.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class WeekEventsExtractorTest {

    private int mWeek;
    private List<Event> mEvents;
    private WeekEventsExtractor mExtractor;

    @Before
    public void setUp() {
        mEvents = ModelMaker.getModelList(Event.class, "2015_events");
        mWeek = 5;
        mExtractor = new WeekEventsExtractor(mWeek);
    }

    @Test
    public void testWeekEventsExtractor()  {
        List<Event> events = mExtractor.call(mEvents);

        assertNotNull(events);
        assertEquals(events.size(), 21);
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            assertEquals(event.getWeek(), (Integer)mWeek);
        }
    }
}