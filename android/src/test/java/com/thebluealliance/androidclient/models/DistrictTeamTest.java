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
    public void testDistrictTeamModel() throws BasicModel.FieldNotDefinedException {
        assertNotNull(mDistrictTeam);
        assertEquals("frc1124", mDistrictTeam.getTeamKey());
        assertEquals((Integer)26, mDistrictTeam.getRank());
        assertEquals("2015ctwat", mDistrictTeam.getEvent1Key());
        assertEquals((Integer)26, mDistrictTeam.getEvent1Points());
        assertEquals("2015manda", mDistrictTeam.getEvent2Key());
        assertEquals((Integer)44, mDistrictTeam.getEvent2Points());
        assertEquals("2015necmp", mDistrictTeam.getCmpKey());
        assertEquals((Integer)87, mDistrictTeam.getCmpPoints());
        assertEquals((Integer)157, mDistrictTeam.getTotalPoints());
    }
}