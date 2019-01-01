package com.thebluealliance.androidclient.database.writers;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.models.EventTeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(DefaultTestRunner.class)
public class EventTeamWriterTest {

    @Mock Database mDb;
    @Mock EventTeamsTable mTable;
    @Mock Gson mGson;

    private EventTeam mEventTeam;
    private EventTeamWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockEventTeamsTable(mDb);
        mEventTeam = new EventTeam();
        mWriter = new EventTeamWriter(mDb);
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mEventTeam, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        verify(db).insert(Database.TABLE_EVENTTEAMS, null, mEventTeam.getParams(mGson));
    }
}