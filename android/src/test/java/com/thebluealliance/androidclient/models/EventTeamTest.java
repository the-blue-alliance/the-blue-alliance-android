package com.thebluealliance.androidclient.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
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