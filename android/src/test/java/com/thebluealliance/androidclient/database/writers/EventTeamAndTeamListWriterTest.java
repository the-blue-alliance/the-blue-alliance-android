package com.thebluealliance.androidclient.database.writers;

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
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class EventTeamAndTeamListWriterTest {

    @Mock Database mDb;
    @Mock EventTeamsTable mTable;
    @Mock TeamsTable mTeamsTable;
    @Mock TeamListWriter mTeamListWriter;
    @Mock EventTeamListWriter mEventTeamListWriter;

    private EventTeamAndTeam mData;
    private EventTeamAndTeamListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTeamListWriter = mock(TeamListWriter.class);
        mEventTeamListWriter = mock(EventTeamListWriter.class);
        mTable = DatabaseMocker.mockEventTeamsTable(mDb);
        mTeamsTable = DatabaseMocker.mockTeamsTable(mDb);
        List<Team> teams = ModelMaker.getModelList(Team.class, "2015necmp_teams");
        mData = new TeamAndEventTeamCombiner("2015necmp").call(teams);
        mWriter = new EventTeamAndTeamListWriter(mDb, mEventTeamListWriter, mTeamListWriter);
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mData, 0L);

        verify(mTeamListWriter).write(eq(mData.teams), anyLong());
        verify(mEventTeamListWriter).write(eq(mData.eventTeams), anyLong());
    }

}