package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class YearsParticipatedInfoMapTest {

    private String mTeamKey;
    private YearsParticipatedInfoMap mMap;
    private JsonArray mYearsParticipated;

    @Before
    public void setUp() {
        mYearsParticipated = ModelMaker.getModel(JsonArray.class, "frc1124_years_participated");
        mTeamKey = "frc1124";
        mMap = new YearsParticipatedInfoMap(mTeamKey);
    }

    @Test
    public void testYearsParticipatedMap() {
        YearsParticipatedWriter.YearsParticipatedInfo info = mMap.call(mYearsParticipated);

        assertNotNull(info);
        assertEquals(info.teamKey, mTeamKey);
        assertEquals(info.yearsParticipated, mYearsParticipated);
    }
}