package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class EventListWriterTest  {

    @Mock Database mDb;
    @Mock EventsTable mTable;

    private List<Event> mEvents;
    private EventListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockEventsTable(mDb);
        mEvents = ModelMaker.getModelList(Event.class, "2015_events");
        mWriter = new EventListWriter(mDb);
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mEvents, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (Event event : mEvents) {
            verify(db).insert(Database.TABLE_EVENTS, null, event.getParams());
        }
    }
}