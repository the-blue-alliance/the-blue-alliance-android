package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.DefaultTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(DefaultTestRunner.class)
public class EventTeamTest {

    private EventTeam mEventTeam;

    @Before
    public void readJson() {
        mEventTeam = new EventTeam();
        mEventTeam.setTeamKey("frc1124");
        mEventTeam.setEventKey("2015necmp");
        mEventTeam.setKey("2015necmp_frc1124");
        mEventTeam.setYear(2015);
    }

    @Test
    public void testModel()  {
        assertNotNull(mEventTeam);
        assertEquals("frc1124", mEventTeam.getTeamKey());
        assertEquals("2015necmp", mEventTeam.getEventKey());
        assertEquals("2015necmp_frc1124", mEventTeam.getKey());
        assertEquals(2015, mEventTeam.getYear().intValue());
    }
}