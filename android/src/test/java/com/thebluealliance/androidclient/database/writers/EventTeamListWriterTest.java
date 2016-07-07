package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTest;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.models.EventTeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class EventTeamListWriterTest extends RobolectricPowerMockTest {

    Database mDb;
    BriteDatabase mBriteDb;
    EventTeamsTable mTable;

    private List<EventTeam> mEventTeams;
    private EventTeamListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mTable = DatabaseMocker.mockEventTeamsTable(mDb, mBriteDb);
        mEventTeams = new ArrayList<>();
        mEventTeams.add(new EventTeam());
        mWriter = new EventTeamListWriter(mDb, mBriteDb);
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mEventTeams);

        for (EventTeam eventTeam : mEventTeams) {
            verify(mBriteDb).insert(Database.TABLE_EVENTTEAMS, eventTeam.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
        }
    }
}