package com.thebluealliance.androidclient.database.writers;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.models.EventTeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
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
        mWriter.write(mEventTeam);

        SQLiteDatabase db = mDb.getWritableDatabase();
        verify(db).insert(Database.TABLE_EVENTTEAMS, null, mEventTeam.getParams(mGson));
    }
}