package com.thebluealliance.androidclient.datafeed;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class APIv2TeamTest extends AbstractAPIv2Test {
    
    @Test
    public void testFetchTeamPage(){
        mApi.fetchTeamPage(0, null).subscribe(teams -> {
            assertTrue(teams.body().size() > 0);
        });
    }

    @Test
    public void testFetchTeam(){
        mApi.fetchTeam("frc1124", null).subscribe(team -> {
            assertEquals(team.body().getKey(), "frc1124");
        });
    }

    @Test
    public void testFetchTeamEvents(){
        mApi.fetchTeamEvents("frc1124", 2014, null).subscribe(events -> {
            assertEquals(events.body().size(), 4);
        });
    }

    @Test
    public void testFetchTeamAtEventAwards(){
        mApi.fetchTeamAtEventAwards("frc1124", "2014cthar", null).subscribe(awards -> {
            assertEquals(awards.body().size(), 1);
        });
    }

    @Test
    public void testFetchTeamAtEventMatches(){
        mApi.fetchTeamAtEventMatches("frc254", "2014cmp", null).subscribe(matches -> {
            assertEquals(matches.body().size(), 5);
        });
    }

    @Test
    public void testFetchTeamYearsParticipated(){
        mApi.fetchTeamYearsParticipated("frc1124", null).subscribe(years -> {
            assertTrue(years.body().size() > 0);
            assertEquals(years.body().get(0).getAsInt(), 2003);
        });
    }

    @Test
    public void testFetchTeamMediaInYear(){
        mApi.fetchTeamMediaInYear("frc1124", 2014, null).subscribe(media -> {
            assertEquals(media.body().size(), 1);
        });
    }
}
