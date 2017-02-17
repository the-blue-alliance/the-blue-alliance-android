package com.thebluealliance.androidclient.database.tables;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(DefaultTestRunner.class)
public class EventsTableTest {
    private EventsTable mTable;
    private List<Event> mEvents;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_EVENTS);
        db.execSQL(Database.CREATE_SEARCH_EVENTS);
        DistrictsTable districtsTable = spy(new DistrictsTable(db));
        mTable = spy(new EventsTable(db, districtsTable));
        mEvents = ModelMaker.getModelList(Event.class, "2015_events");
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mEvents.get(0));
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mEvents));
    }

    @Test
    public void testUpdate() {
        Event result = DbTableTestDriver.testUpdate(mTable,
                                                       mEvents.get(0),
                                                       event -> event.setName("Test"));
        assertNotNull(result);
        assertEquals("Test", result.getName());
    }

    @Test
    public void testLastModified() {
        DbTableTestDriver.testLastModified(mTable, mEvents);
    }

    @Test
    public void testDelete() {
        DbTableTestDriver.testDelete(mTable,
                                     mEvents,
                                     EventsTable.KEY + " = ?",
                                     new String[]{"2015cthar"},
                                     1);
    }
}