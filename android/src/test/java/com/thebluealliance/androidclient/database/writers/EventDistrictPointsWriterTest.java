package com.thebluealliance.androidclient.database.writers;

import com.google.gson.JsonObject;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTest;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class EventDistrictPointsWriterTest extends RobolectricPowerMockTest {

    Database mDb;
    BriteDatabase mBriteDb;
    EventsTable mTable;
    EventWriter mEventWriter;

    private Event mEvent;
    private JsonObject mPoints;
    private KeyAndJson mData;
    private EventStatsWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mEventWriter = mock(EventWriter.class);
        mTable = DatabaseMocker.mockEventsTable(mDb, mBriteDb);
        mEvent = ModelMaker.getModel(Event.class, "2015necmp");
        mPoints = ModelMaker.getModel(JsonObject.class, "2015necmp_points");
        mData = new KeyAndJson("2015necmp", mPoints);
        mWriter = new EventStatsWriter(mDb, mBriteDb, mEventWriter);
    }

    @Test
    public void testEventStatsWriter() throws BasicModel.FieldNotDefinedException {
        mWriter.write(mData);

        // TODO actually test stuff here
    }
}