package com.thebluealliance.androidclient.test.datafeed;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class APIv2MatchTest extends AbstractAPIv2Test {
    @Test
    public void testFetchMatch() {
        mApi.fetchMatch("2014cmp_f1m1", null).subscribe(match -> {
            assertEquals(match.getKey(), "2014cmp_f1m1");
        });
    }

}
