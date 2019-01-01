package com.thebluealliance.androidclient.validators;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(DefaultTestRunner.class)
public class ValidatorTest {

    @org.junit.Test
    public void testTeamKeyValidator(){
        assertEquals(TeamHelper.validateTeamKey("frc1124"), true);
        assertEquals(TeamHelper.validateTeamKey("frc1"), true);

        assertEquals(TeamHelper.validateTeamKey("ftc12"), false);
        assertEquals(TeamHelper.validateTeamKey("frc11243"), false);
        assertEquals(TeamHelper.validateTeamKey("frcfrc1124"), false);
    }

    @org.junit.Test
    public void testEventKeyValidator(){
        assertEquals(EventHelper.validateEventKey("2014ctgro"), true);
        assertEquals(EventHelper.validateEventKey("1992cmp"), true);

        assertEquals(EventHelper.validateEventKey("cmp2004"), false);
        assertEquals(EventHelper.validateEventKey("203cmp"), false);
        assertEquals(EventHelper.validateEventKey("2003"), false);
        assertEquals(EventHelper.validateEventKey("2008cm_p"), false);
    }

    @org.junit.Test
    public void testMatchKeyValidator(){
        assertEquals(MatchHelper.validateMatchKey("2014ctgro_qm1"), true);
        assertEquals(MatchHelper.validateMatchKey("2014cthar_qm100"), true);
        assertEquals(MatchHelper.validateMatchKey("2014onto_qf1m1"), true);
        assertEquals(MatchHelper.validateMatchKey("2014necmp_sf2m3"), true);
        assertEquals(MatchHelper.validateMatchKey("2014scmb_f1m2"), true);
        assertEquals(MatchHelper.validateMatchKey("2007bc_ef1m1"), true);

        assertEquals(MatchHelper.validateMatchKey("2014ctgro_q1"), false);
        assertEquals(MatchHelper.validateMatchKey("2014ctgro_s2m1"), false);
    }

}
