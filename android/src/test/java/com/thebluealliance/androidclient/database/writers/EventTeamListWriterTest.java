package com.thebluealliance.androidclient.database.writers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.models.EventTeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EventTeamListWriterTest {

    @Mock Database mDb;
    @Mock EventTeamsTable mTable;
    @Mock Gson mGson;

    private List<EventTeam> mEventTeams;
    private EventTeamListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockEventTeamsTable(mDb);
        mEventTeams = new ArrayList<>();
        mEventTeams.add(new EventTeam());
        mWriter = new EventTeamListWriter(mDb);
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mEventTeams, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (EventTeam eventTeam : mEventTeams) {
            verify(db).insert(Database.TABLE_EVENTTEAMS, null, eventTeam.getParams(mGson));
        }
    }
}