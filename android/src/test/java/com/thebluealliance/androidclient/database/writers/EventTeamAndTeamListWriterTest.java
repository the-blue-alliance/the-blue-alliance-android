package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTestBase;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.EventTeamsTable;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.database.writers.EventTeamAndTeamListWriter.EventTeamAndTeam;
import com.thebluealliance.androidclient.datafeed.combiners.TeamAndEventTeamCombiner;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class EventTeamAndTeamListWriterTest extends RobolectricPowerMockTestBase {

    Database mDb;
    BriteDatabase mBriteDb;
    EventTeamsTable mTable;
    TeamsTable mTeamsTable;
    TeamListWriter mTeamListWriter;
    EventTeamListWriter mEventTeamListWriter;

    private EventTeamAndTeam mData;
    private EventTeamAndTeamListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mTeamListWriter = mock(TeamListWriter.class);
        mEventTeamListWriter = mock(EventTeamListWriter.class);
        mTable = DatabaseMocker.mockEventTeamsTable(mDb, mBriteDb);
        mTeamsTable = DatabaseMocker.mockTeamsTable(mDb, mBriteDb);
        List<Team> teams = ModelMaker.getModelList(Team.class, "2015necmp_teams");
        mData = new TeamAndEventTeamCombiner("2015necmp").call(teams);
        mWriter = new EventTeamAndTeamListWriter(mDb, mBriteDb, mEventTeamListWriter, mTeamListWriter);
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mData);

        verify(mTeamListWriter).write(mData.teams);
        verify(mEventTeamListWriter).write(mData.eventTeams);
    }

}