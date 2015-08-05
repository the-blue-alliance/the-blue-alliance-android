package com.thebluealliance.androidclient.test.datafeed;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class APIv2EventTest extends AbstractAPIv2Test {

    @Test
    public void testFetchEventsInYear(){
        mApi.fetchEventsInYear(2014).subscribe(events -> {
            assertTrue(events.size() > 0); 
        });
    }

    @Test
    public void testFetchEvent(){
        mApi.fetchEvent("2014cthar").subscribe(event -> {
            assertEquals(event.getKey(), "2014cthar");
        });
    }

    @Test
    public void testFetchEventTeams(){
        mApi.fetchEventTeams("2014cthar").subscribe(teams -> {
            assertTrue(teams.size() > 0);
        });
    }

    @Test
    public void testFetchEventRankings(){
        mApi.fetchEventRankings("2014cthar").subscribe(rankings -> {
            assertTrue(rankings.size() > 0);
            assertTrue(rankings.get(0).isJsonArray());
            assertEquals(rankings.get(0).getAsJsonArray().get(0).getAsString(), "Rank");
        });
    }

    @Test
    public void testFetchEventMatches(){
        mApi.fetchEventMatches("2014cthar").subscribe(matches -> {
            assertTrue(matches.size() > 0);
        });
    }

    @Test
    public void testFetchEventStats(){
        mApi.fetchEventStats("2014cthar").subscribe(stats -> {
            assertTrue(stats.has("oprs"));
            assertTrue(stats.has("dprs"));
            assertTrue(stats.has("ccwms"));
        });
    }

    @Test
    public void testFetchEventAwards(){
        mApi.fetchEventAwards("2014cthar").subscribe(awards -> {
            assertTrue(awards.size() > 0);
        });
    }

    @Test
    public void testFetchEventDistrictPoins(){
        mApi.fetchEventDistrictPoints("2014cthar").subscribe(points -> {
            assertTrue(points.has("points"));
            assertTrue(points.has("tiebreakers"));
        });
    }
}
