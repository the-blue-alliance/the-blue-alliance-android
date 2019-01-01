package com.thebluealliance.androidclient.helpers;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.types.MatchType;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(DefaultTestRunner.class)
public class MatchTypeTest {

    @Test
    public void testNextPlayOrder() {
        assertEquals(MatchType.QUAL.next(), MatchType.OCTO);
        assertEquals(MatchType.OCTO.next(), MatchType.QUARTER);
        assertEquals(MatchType.QUARTER.next(), MatchType.SEMI);
        assertEquals(MatchType.SEMI.next(), MatchType.FINAL);
        assertEquals(MatchType.FINAL.next(), null);
    }

    @Test
    public void testPreviousPlayOrder() {
        assertEquals(MatchType.QUAL.previous(), null);
        assertEquals(MatchType.OCTO.previous(), MatchType.QUAL);
        assertEquals(MatchType.QUARTER.previous(), MatchType.OCTO);
        assertEquals(MatchType.SEMI.previous(), MatchType.QUARTER);
        assertEquals(MatchType.FINAL.previous(), MatchType.SEMI);
    }

    @Test
    public void testTypeFromShortStr() {
        assertEquals(MatchType.fromShortType("qm"), MatchType.QUAL);
        assertEquals(MatchType.fromShortType("qf"), MatchType.QUARTER);
        assertEquals(MatchType.fromShortType("sf"), MatchType.SEMI);
        assertEquals(MatchType.fromShortType("f"), MatchType.FINAL);

        assertEquals(MatchType.fromShortType("ef"), MatchType.OCTO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidShortStr() {
        MatchType.fromShortType("meow");
    }

    @Test
    public void testTypeFromKey() {
        assertEquals(MatchType.fromKey("2015cthar_qm1"), MatchType.QUAL);
        assertEquals(MatchType.fromKey("2015cthar_qf1m1"), MatchType.QUARTER);
        assertEquals(MatchType.fromKey("2015cthar_sf2m2"), MatchType.SEMI);
        assertEquals(MatchType.fromKey("2015cthar_f1m3"), MatchType.FINAL);
        assertEquals(MatchType.fromKey("2015micmp_ef1m12"), MatchType.OCTO);
        assertEquals(MatchType.fromKey("21rwewfjsd"), MatchType.NONE);
    }
}
