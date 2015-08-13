package com.thebluealliance.androidclient.datafeed;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class APIv2EventTest extends AbstractAPIv2Test {

    @Test
    public void testFetchEventsInYear(){
        mApi.fetchEventsInYear(2014).subscribe(events -> {
            assertTrue(events.body().size() > 0);
        });
    }

    @Test
    public void testFetchEvent(){
        mApi.fetchEvent("2014cthar").subscribe(event -> {
            assertEquals(event.body().getKey(), "2014cthar");
        });
    }

    @Test
    public void testFetchEventTeams(){
        mApi.fetchEventTeams("2014cthar").subscribe(teams -> {
            assertTrue(teams.body().size() > 0);
        });
    }

    @Test
    public void testFetchEventRankings(){
        mApi.fetchEventRankings("2014cthar").subscribe(rankings -> {
            assertTrue(rankings.body().size() > 0);
            assertTrue(rankings.body().get(0).isJsonArray());
            assertEquals(rankings.body().get(0).getAsJsonArray().get(0).getAsString(), "Rank");
        });
    }

    @Test
    public void testFetchEventMatches(){
        mApi.fetchEventMatches("2014cthar").subscribe(matches -> {
            assertTrue(matches.body().size() > 0);
        });
    }

    @Test
    public void testFetchEventStats(){
        mApi.fetchEventStats("2014cthar").subscribe(stats -> {
            assertTrue(stats.body().has("oprs"));
            assertTrue(stats.body().has("dprs"));
            assertTrue(stats.body().has("ccwms"));
        });
    }

    @Test
    public void testFetchEventAwards(){
        mApi.fetchEventAwards("2014cthar").subscribe(awards -> {
            assertTrue(awards.body().size() > 0);
        });
    }

    @Test
    public void testFetchEventDistrictPoins(){
        mApi.fetchEventDistrictPoints("2014cthar").subscribe(points -> {
            assertTrue(points.body().has("points"));
            assertTrue(points.body().has("tiebreakers"));
        });
    }
}
