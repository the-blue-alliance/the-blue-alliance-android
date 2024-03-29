package com.thebluealliance.androidclient.datafeed.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TeamStatsExtractorTest {

    private JsonElement mAllStats;
    private TeamStatsExtractor mExtractor;

    @Before
    public void setUp() {
        String searchTeamKey = "frc195";
        mAllStats = ModelMaker.getModel(JsonElement.class, "2015necmp_oprs");
        mExtractor = new TeamStatsExtractor(searchTeamKey);
    }

    @Test
    public void testTeamStatsExtractor() {
        JsonElement stats = mExtractor.call(mAllStats);

        assertNotNull(stats);
        assertTrue(stats.isJsonObject());

        JsonObject statsObject = stats.getAsJsonObject();
        assertTrue(statsObject.has("opr"));
        assertTrue(statsObject.has("dpr"));
        assertTrue(statsObject.has("ccwm"));
        assertEquals(statsObject.get("opr").getAsDouble(), 87.957372917501459, .01);
        assertEquals(statsObject.get("dpr").getAsDouble(), 50.887943082425011, .01);
        assertEquals(statsObject.get("ccwm").getAsDouble(), 37.06942983507642, .01);
    }
}