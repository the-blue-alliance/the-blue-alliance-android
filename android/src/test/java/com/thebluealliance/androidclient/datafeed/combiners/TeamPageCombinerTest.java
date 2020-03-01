package com.thebluealliance.androidclient.datafeed.combiners;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class TeamPageCombinerTest {

    private List<Team> teams1;
    private List<Team> teams2;
    private TeamPageCombiner mCombiner;

    @Before
    public void setUp() {
        teams1 = ModelMaker.getModelList(Team.class, "2015necmp_teams");
        teams2 = ModelMaker.getModelList(Team.class, "2015necmp_teams");
        mCombiner = new TeamPageCombiner();
    }

    @Test
    public void testTeamPageCombiner() {
        ImmutableList<Team> initialTeams = ImmutableList.copyOf(teams1);
        List<Team> combinedTeams = mCombiner.call(teams1, teams2);

        assertNotNull(combinedTeams);
        assertEquals(combinedTeams.size(), initialTeams.size() + teams2.size());
        for (int i = 0; i < initialTeams.size(); i++) {
            assertEquals(combinedTeams.get(i), initialTeams.get(i));
        }
        for (int i = 0; i < teams2.size(); i++) {
            assertEquals(combinedTeams.get(initialTeams.size() + i), teams2.get(i));
        }
    }
}