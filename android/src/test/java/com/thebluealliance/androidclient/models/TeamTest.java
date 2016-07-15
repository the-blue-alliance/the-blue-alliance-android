package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TeamTest {

    private Team mTeam;

    @Before
    public void readJson() {
        mTeam = ModelMaker.getModel(Team.class, "frc1124");
    }

    @Test
    public void testTeamModel() throws BasicModel.FieldNotDefinedException {
        assertNotNull(mTeam);
        assertEquals("frc1124", mTeam.getKey());
        assertEquals("Avon Public Schools/UTC & AVON HIGH SCHOOL", mTeam.getFullName());
        assertEquals("UberBots", mTeam.getNickname());
        assertEquals("http://www.uberbots.org", mTeam.getWebsite());
        assertEquals("Avon, Connecticut, USA", mTeam.getLocation());
        assertEquals(1124, (int)mTeam.getNumber());
    }
}