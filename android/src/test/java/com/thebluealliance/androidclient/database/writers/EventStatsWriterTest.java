package com.thebluealliance.androidclient.database.writers;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.datafeed.KeyAndJson;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.BasicModel;
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
public class EventStatsWriterTest {

    @Mock Database mDb;
    @Mock EventsTable mTable;
    @Mock EventWriter mEventWriter;

    private Event mEvent;
    private JsonObject mStats;
    private KeyAndJson mData;
    private EventStatsWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mEventWriter = mock(EventWriter.class);
        mTable = DatabaseMocker.mockEventsTable(mDb);
        mEvent = ModelMaker.getModel(Event.class, "2015necmp");
        mStats = ModelMaker.getModel(JsonObject.class, "2015necmp_stats");
        mData = new KeyAndJson("2015necmp", mStats);
        mWriter = new EventStatsWriter(mDb, mEventWriter);
    }

    @Test
    public void testEventStatsWriter() throws BasicModel.FieldNotDefinedException {
        mWriter.write(mData);
    }
}