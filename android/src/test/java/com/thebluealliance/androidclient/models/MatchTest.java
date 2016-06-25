package com.thebluealliance.androidclient.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MatchTest {
    Match mMatch;

    @Before
    public void readJsonData(){
        mMatch = ModelMaker.getModel(Match.class, "2014cmp_f1m1");
    }

    @Test
    public void testMatchModel() throws BasicModel.FieldNotDefinedException {
        assertNotNull(mMatch);
        assertEquals(mMatch.getKey(), "2014cmp_f1m1");
        assertEquals(mMatch.getMatchNumber(), 1);
        assertEquals(mMatch.getSetNumber(), 1);
        assertEquals(mMatch.getEventKey(), "2014cmp");
        assertEquals(mMatch.getTimeString(), "5:38 PM");
        assertEquals(mMatch.getTime().getTime(), 1398551880);
        assertNotNull(mMatch.getVideos());
        assertNotNull(mMatch.getAlliances());

        JsonArray videos = mMatch.getVideos();
        assertEquals(videos.size(), 2);
        assertTrue(videos.get(0).isJsonObject());
        JsonObject video1 = videos.get(0).getAsJsonObject();
        assertEquals(video1.get("type").getAsString(), "youtube");
        assertEquals(video1.get("key").getAsString(), "jdJutaggCMk");

        JsonObject alliances = mMatch.getAlliances().getAsJsonObject();
        assertTrue(alliances.has("blue") && alliances.get("blue").isJsonObject());
        assertTrue(alliances.has("red") && alliances.get("red").isJsonObject());
        JsonObject blueAlliance = alliances.get("blue").getAsJsonObject();
        assertTrue(blueAlliance.has("score") && blueAlliance.has("teams"));
        assertEquals(blueAlliance.get("score").getAsInt(), 361);
        assertTrue(blueAlliance.get("teams").isJsonArray());
        JsonArray blueTeams = blueAlliance.get("teams").getAsJsonArray();
        assertEquals(blueTeams.size(), 3);
        assertEquals(blueTeams.get(0).getAsString(), "frc469");
    }

    @Test
    public void testUtilities() throws BasicModel.FieldNotDefinedException {
        JsonArray teamsJson = Match.getRedTeams(mMatch.getAlliances());
        ArrayList<String> teamKeys = Match.teamKeys(teamsJson);
        assertEquals(Arrays.asList("frc1678", "frc1640", "frc1114"), teamKeys);

        ArrayList<String> teamNumbers = Match.teamNumbers(teamsJson);
        assertEquals(Arrays.asList("1678", "1640", "1114"), teamNumbers);

        JsonArray emptyJsonArray = new JsonArray();
        assertEquals(0, Match.teamKeys(emptyJsonArray).size());
        assertEquals(0, Match.teamNumbers(emptyJsonArray).size());
    }
}
