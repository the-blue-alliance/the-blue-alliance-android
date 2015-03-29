package com.thebluealliance.androidclient.test.datafeed;

import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class APIv2TeamTest extends AbstractAPIv2Test{

    @Test
    public void testFetchTeamPage(){
        List<Team> teams = api.fetchTeamPage(0, null);
        assertTrue(teams.size() > 0);
    }

    @Test
    public void testFetchTeam() throws BasicModel.FieldNotDefinedException {
        Team team = api.fetchTeam("frc1124", null);
        assertEquals(team.getTeamKey(), "frc1124");
    }

    @Test
    public void testFetchTeamEvents(){
        List<Event> events = api.fetchTeamEvents("frc1124", 2014, null);
        assertEquals(events.size(), 4);
    }

    @Test
    public void testFetchTeamAtEventAwards(){
        List<Award> awards = api.fetchTeamAtEventAwards("frc1124", "2014cthar", null);
        assertEquals(awards.size(), 1);
    }

    @Test
    public void testFetchTeamAtEventMatches(){
        List<Match> matches = api.fetchTeamAtEventMatches("frc254", "2014cmp", null);
        assertEquals(matches.size(), 5);
    }

    @Test
    public void testFetchTeamYearsParticipated(){
        List<Integer> years = api.fetchTeamYearsParticipated("frc1124", null);
        assertTrue(years.size() > 0);
        assertEquals((int)years.get(0), 2003);
    }

    @Test
    public void testFetchTeamMediaInYear(){
        List<Media> media = api.fetchTeamMediaInYear("frc1124", 2014, null);
        assertEquals(media.size(), 1);
    }

}
