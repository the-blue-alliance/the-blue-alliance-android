package com.thebluealliance.androidclient.datafeed.maps;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
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
    public void testWeekEventsExtractor() throws BasicModel.FieldNotDefinedException {
        List<Event> events = mExtractor.call(mEvents);

        assertNotNull(events);
        assertEquals(events.size(), 21);
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            assertEquals(event.getCompetitionWeek(), mWeek);
        }
    }
}