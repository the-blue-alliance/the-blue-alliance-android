package com.thebluealliance.androidclient.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TeamTest {

    private Team mTeam;

    @Before
    public void readJson() {
        mTeam = ModelMaker.getModel(Team.class, "frc1124");
    }

    @Test
    public void testTeamModel()  {
        assertNotNull(mTeam);
        assertEquals("frc1124", mTeam.getKey());
        assertEquals("Avon Public Schools/UTC/OFS Fitel/Walmart/Wittman Battenfeld/Simsbury Bank&Avon High School", mTeam.getName());
        assertEquals("UberBots", mTeam.getNickname());
        assertEquals("http://www.uberbots.org", mTeam.getWebsite());
        assertEquals("Avon, Connecticut, United States", mTeam.getLocation());
        assertNotNull(mTeam.getTeamNumber());
        assertEquals(1124, mTeam.getTeamNumber().intValue());
    }
}