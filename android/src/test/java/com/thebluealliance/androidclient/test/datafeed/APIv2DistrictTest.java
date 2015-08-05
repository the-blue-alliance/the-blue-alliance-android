package com.thebluealliance.androidclient.test.datafeed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class APIv2DistrictTest extends AbstractAPIv2Test {

    @Test
    public void testFetchDistrictListObservable() {
        mApi.fetchDistrictList(2014).subscribe(districts -> {
            assertEquals(districts.size(), 4);
            assertEquals(districts.get(0).getKey(), "fim");
        });
    }

    @Test
    public void testFetchDistrictEventsObservable() {
        mApi.fetchDistrictEvents("ne", 2014).subscribe(events -> {
            assertTrue(events.size() > 0);
        });
    }

    @Test
    public void testFetchDistrictRankingsObservable() {
        mApi.fetchDistrictRankings("ne", 2014).subscribe(rankings -> {
            assertTrue(rankings.size() > 0);
        });
    }
}
