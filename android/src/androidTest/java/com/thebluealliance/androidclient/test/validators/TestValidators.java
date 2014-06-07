package com.thebluealliance.androidclient.test.validators;

import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Team;

import junit.framework.TestCase;

/**
 * File created by phil on 5/31/14.
 */
public class TestValidators extends TestCase {

    public void testTeamKeyValidator(){
        assertEquals(Team.validateTeamKey("frc1124"), true);
        assertEquals(Team.validateTeamKey("frc1"), true);

        assertEquals(Team.validateTeamKey("ftc12"), false);
        assertEquals(Team.validateTeamKey("frc11243"), false);
        assertEquals(Team.validateTeamKey("frcfrc1124"), false);
    }

    public void testEventKeyValidator(){
        assertEquals(Event.validateEventKey("2014ctgro"), true);
        assertEquals(Event.validateEventKey("1992cmp"), true);

        assertEquals(Event.validateEventKey("cmp2004"), false);
        assertEquals(Event.validateEventKey("203cmp"), false);
        assertEquals(Event.validateEventKey("2003"), false);
        assertEquals(Event.validateEventKey("2008cm_p"), false);
    }

    public void testMatchKeyValidator(){
        assertEquals(Match.validateMatchKey("2014ctgro_qm1"), true);
        assertEquals(Match.validateMatchKey("2014cthar_qm100"), true);
        assertEquals(Match.validateMatchKey("2014onto_qf1m1"), true);
        assertEquals(Match.validateMatchKey("2014necmp_sf2m3"), true);
        assertEquals(Match.validateMatchKey("2014scmb_f1m2"), true);
        assertEquals(Match.validateMatchKey("2007bc_ef1m1"), true);

        assertEquals(Match.validateMatchKey("2014ctgro_q1"), false);
        assertEquals(Match.validateMatchKey("2014ctgro_s2m1"), false);
    }

}
