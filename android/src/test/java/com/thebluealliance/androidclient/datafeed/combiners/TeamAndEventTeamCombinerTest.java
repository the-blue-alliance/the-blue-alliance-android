package com.thebluealliance.androidclient.datafeed.combiners;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.writers.EventTeamAndTeamListWriter;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(DefaultTestRunner.class)
public class TeamAndEventTeamCombinerTest {

    private List<Team> mTeams;
    private TeamAndEventTeamCombiner mCombiner;

    @Before
    public void setUp() {
        mTeams = ModelMaker.getModelList(Team.class, "2015necmp_teams");
        mCombiner = new TeamAndEventTeamCombiner("2015necmp");
    }

    @Test
    public void testTeamAndEventTeamCombiner()  {
        EventTeamAndTeamListWriter.EventTeamAndTeam result = mCombiner.call(mTeams);

        assertNotNull(result);
        assertEquals(mTeams, result.teams);
        assertNotNull(result.eventTeams);
        assertEquals(mTeams.size(), result.eventTeams.size());
        for (int i = 0; i < mTeams.size(); i++) {
            Team team = mTeams.get(i);
            EventTeam eventTeam = result.eventTeams.get(i);
            assertNotNull(eventTeam);
            assertEquals(eventTeam.getKey(), EventTeamHelper.generateKey("2015necmp", team.getKey()));
            assertEquals(eventTeam.getEventKey(), "2015necmp");
            assertEquals(eventTeam.getTeamKey(), team.getKey());
            assertEquals(eventTeam.getYear(), (Integer)2015);
        }
    }
}