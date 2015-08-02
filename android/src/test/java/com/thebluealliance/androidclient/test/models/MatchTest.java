package com.thebluealliance.androidclient.test.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Ignore
public class MatchTest {
    Match mMatch;

    @Before
    public void readJsonData(){
        BufferedReader matchReader;
        Gson gson = JSONHelper.getGson();
        String basePath = new File("").getAbsolutePath();
        try {
            matchReader = new BufferedReader(
                new FileReader(basePath + "/android/src/test/java/com/thebluealliance/" +
                    "androidclient/test/models/data/match_2014cmp_f1m1.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }

        mMatch = gson.fromJson(matchReader, Match.class);
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
}
