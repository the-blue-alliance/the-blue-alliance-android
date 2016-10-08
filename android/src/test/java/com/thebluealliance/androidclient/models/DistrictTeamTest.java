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
public class DistrictTeamTest {
    private DistrictTeam mDistrictTeam;

    @Before
    public void readJson() {
        mDistrictTeam = ModelMaker.getModelList(DistrictTeam.class, "2015ne_rankings").get(0);
    }

    @Test
    public void testDistrictTeamModel()  {
        assertNotNull(mDistrictTeam);
        assertEquals("frc1124", mDistrictTeam.getTeamKey());
        assertNotNull(mDistrictTeam.getRank());
        assertEquals(26, mDistrictTeam.getRank().intValue());
        assertEquals("2015ctwat", mDistrictTeam.getEvent1Key());
        assertNotNull(mDistrictTeam.getEvent1Points());
        assertEquals(26, mDistrictTeam.getEvent1Points().intValue());
        assertEquals("2015manda", mDistrictTeam.getEvent2Key());
        assertNotNull(mDistrictTeam.getEvent2Points());
        assertEquals(44, mDistrictTeam.getEvent2Points().intValue());
        assertEquals("2015necmp", mDistrictTeam.getCmpKey());
        assertNotNull(mDistrictTeam.getCmpPoints());
        assertEquals(87, mDistrictTeam.getCmpPoints().intValue());
        assertNotNull(mDistrictTeam.getTotalPoints());
        assertEquals(157, mDistrictTeam.getTotalPoints().intValue());
    }
}