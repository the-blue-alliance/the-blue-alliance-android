package com.thebluealliance.androidclient.database.writers;

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

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TeamListWriterTest {

    @Mock Database mDb;
    @Mock TeamsTable mTable;

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
            verify(db).insert(Database.TABLE_TEAMS, null, team.getParams());
        }
    }
}