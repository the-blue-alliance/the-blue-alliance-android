package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTestBase;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class TeamListWriterTest extends RobolectricPowerMockTestBase {

    Database mDb;
    BriteDatabase mBriteDb;
    TeamsTable mTable;

    private List<Team> mTeams;
    private TeamListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mTable = DatabaseMocker.mockTeamsTable(mDb, mBriteDb);
        mTeams = ModelMaker.getModelList(Team.class, "2015necmp_teams");
        mWriter = new TeamListWriter(mDb, mBriteDb);
    }

    @Test
    public void testTeamListWriter() {
        mWriter.write(mTeams);

        for (Team team : mTeams) {
            verify(mBriteDb).insert(Database.TABLE_TEAMS, team.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
        }
    }
}