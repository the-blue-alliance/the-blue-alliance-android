package com.thebluealliance.androidclient.test.datafeed;

import com.thebluealliance.androidclient.datafeed.APIHelper;
import com.thebluealliance.androidclient.datafeed.APIv2;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Class that hits TBA endpoints and tests that we can get data back
 * This class is ignored by default, so we aren't always hitting the server from automated tests
 * Change the 'shouldRun' variable to true if you'd like to run it.
 * Created by phil on 3/28/15.
 */
@RunWith(RobolectricTestRunner.class)
public class APIv2TeamTest {

    APIv2 api;

    @Before
    public void setupAPIClient(){
        boolean shouldRun = false; //TODO move to configuration file

        APIv2.APIv2RequestInterceptor.isFromJunit = true;
        api = APIHelper.getAPI();
        assumeTrue(shouldRun);
    }

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
