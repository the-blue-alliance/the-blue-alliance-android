package com.thebluealliance.androidclient.database.writers;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Team;

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
public class TeamWriterTest {

    @Mock Database mDb;
    @Mock TeamsTable mTable;
    @Mock Gson mGson;

    private Team mTeam;
    private TeamWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockTeamsTable(mDb);
        mTeam = ModelMaker.getModel(Team.class, "frc1124");
        mWriter = new TeamWriter(mDb);
    }

    @Test
    public void testTeamListWriter() {
        mWriter.write(mTeam, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        verify(db).insert(Database.TABLE_TEAMS, null, mTeam.getParams(mGson));
    }
}