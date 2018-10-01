package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.database.writers.YearsParticipatedWriter;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class YearsParticipatedInfoMapTest {

    private String mTeamKey;
    private YearsParticipatedInfoMap mMap;
    private List<Integer> mYearsParticipated;

    @Before
    public void setUp() {
        JsonArray yearsJson = ModelMaker.getModel(JsonArray.class, "frc1124_years_participated");
        mYearsParticipated = new ArrayList<>();
        for (int i = 0; i < yearsJson.size(); i++) {
            mYearsParticipated.add(yearsJson.get(i).getAsInt());
        }
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