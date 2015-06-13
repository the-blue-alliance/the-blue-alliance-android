package com.thebluealliance.androidclient.test.datafeed;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class APIv2TeamTest extends AbstractAPIv2Test {
    
    @Test
    public void testFetchTeamPage(){
        mApi.fetchTeamPage(0, null).subscribe(teams -> {
            assertTrue(teams.size() > 0);
        });
    }

    @Test
    public void testFetchTeam(){
        mApi.fetchTeam("frc1124", null).subscribe(team -> {
            assertEquals(team.getKey(), "frc1124");
        });
    }

    @Test
    public void testFetchTeamEvents(){
        mApi.fetchTeamEvents("frc1124", 2014, null).subscribe(events -> {
            assertEquals(events.size(), 4);
        });
    }

    @Test
    public void testFetchTeamAtEventAwards(){
        mApi.fetchTeamAtEventAwards("frc1124", "2014cthar", null).subscribe(awards -> {
            assertEquals(awards.size(), 1);
        });
    }

    @Test
    public void testFetchTeamAtEventMatches(){
        mApi.fetchTeamAtEventMatches("frc254", "2014cmp", null).subscribe(matches -> {
            assertEquals(matches.size(), 5);
        });
    }

    @Test
    public void testFetchTeamYearsParticipated(){
        mApi.fetchTeamYearsParticipated("frc1124", null).subscribe(years -> {
            assertTrue(years.size() > 0);
            assertEquals((int)years.get(0), 2003);
        });
    }

    @Test
    public void testFetchTeamMediaInYear(){
        mApi.fetchTeamMediaInYear("frc1124", 2014, null).subscribe(media -> {
            assertEquals(media.size(), 1);
        });
    }
}
