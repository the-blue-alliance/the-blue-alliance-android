package com.thebluealliance.androidclient.datafeed;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Ignore
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class APIv2DistrictTest extends AbstractAPIv2Test {

    @Test
    public void testFetchDistrictListObservable() {
        mApi.fetchDistrictList(2014, null).subscribe(districts -> {
            assertEquals(districts.body().size(), 4);
            assertEquals(districts.body().get(0).getKey(), "fim");
        });
    }

    @Test
    public void testFetchDistrictEventsObservable() {
        mApi.fetchDistrictEvents("ne", 2014, null).subscribe(events -> {
            assertTrue(events.body().size() > 0);
        });
    }

    @Test
    public void testFetchDistrictRankingsObservable() {
        mApi.fetchDistrictRankings("ne", 2014, null).subscribe(rankings -> {
            assertTrue(rankings.body().size() > 0);
        });
    }
}
