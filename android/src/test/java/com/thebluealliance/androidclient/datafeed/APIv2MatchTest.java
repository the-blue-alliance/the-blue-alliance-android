package com.thebluealliance.androidclient.datafeed;

import static junit.framework.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Ignore
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class APIv2MatchTest extends AbstractAPIv2Test {
    @Test
    public void testFetchMatch() {
        mApi.fetchMatch("2014cmp_f1m1", null).subscribe(match -> {
            assertEquals(match.body().getKey(), "2014cmp_f1m1");
        });
    }

}
