package com.thebluealliance.androidclient.database.writers;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class TeamListWriterTest {

    @Mock Database mDb;
    @Mock TeamsTable mTable;
    @Mock Gson mGson;

    private List<Team> mTeams;
    private TeamListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockTeamsTable(mDb);
        mTeams = ModelMaker.getModelList(Team.class, "2015necmp_teams");
        mWriter = new TeamListWriter(mDb);
    }

    @Test
    public void testTeamListWriter() {
        mWriter.write(mTeams);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (Team team : mTeams) {
            verify(db).insert(Database.TABLE_TEAMS, null, team.getParams(mGson));
        }
    }
}