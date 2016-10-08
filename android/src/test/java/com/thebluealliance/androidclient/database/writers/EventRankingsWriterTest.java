package com.thebluealliance.androidclient.database.writers;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class EventRankingsWriterTest {

    @Mock Database mDb;
    @Mock EventsTable mTable;
    @Mock EventWriter mEventWriter;

    private Event mEvent;
    private JsonArray mRankings;
    private KeyAndJson mData;
    private EventRankingsWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mEventWriter = mock(EventWriter.class);
        mTable = DatabaseMocker.mockEventsTable(mDb);
        mEvent = ModelMaker.getModel(Event.class, "2015necmp");
        mRankings = ModelMaker.getModel(JsonArray.class, "2015necmp_rankings");
        mData = new KeyAndJson("2015necmp", mRankings);
        mWriter = new EventRankingsWriter(mDb, mEventWriter);
    }

    @Test
    public void testEventRankingsWriter()  {
        mWriter.write(mData);
    }
}