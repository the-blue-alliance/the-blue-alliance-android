package com.thebluealliance.androidclient.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DistrictTeamTest {
    private DistrictRanking mDistrictTeam;

    @Before
    public void readJson() {
        mDistrictTeam = ModelMaker.getModelList(DistrictRanking.class, "2015ne_rankings").get(25);
    }

    @Test
    public void testDistrictTeamModel()  {
        assertNotNull(mDistrictTeam);
        assertEquals("frc1124", mDistrictTeam.getTeamKey());
        assertNotNull(mDistrictTeam.getRank());
        assertEquals(26, mDistrictTeam.getRank().intValue());
        assertNotNull(mDistrictTeam.getEventPoints());
        assertEquals(mDistrictTeam.getEventPoints().size(), 3);
        assertEquals("2015ctwat", mDistrictTeam.getEventPoints().get(0).getEventKey());
        assertEquals(26, mDistrictTeam.getEventPoints().get(0).getTotal().intValue());
        assertEquals("2015manda", mDistrictTeam.getEventPoints().get(1).getEventKey());
        assertEquals(44, mDistrictTeam.getEventPoints().get(1).getTotal().intValue());
        assertEquals("2015necmp", mDistrictTeam.getEventPoints().get(2).getEventKey());
        assertEquals(87, mDistrictTeam.getEventPoints().get(2).getTotal().intValue());
        assertNotNull(mDistrictTeam.getPointTotal());
        assertEquals(157, mDistrictTeam.getPointTotal().intValue());
    }
}