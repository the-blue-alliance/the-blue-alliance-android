package com.thebluealliance.androidclient.test.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class MatchHelperTest {
    private static final String ALLIANCES_2009 = "{" +
            "\"red\":  {\"score\": 81, \"teams\": [\"frc68\", \"frc217\", \"frc247\"]}," +
            "\"blue\": {\"score\": 98, \"teams\": [\"frc111\", \"frc971\", \"frc67\"]}" +
            "}";
    private JsonObject alliances;

    @Before
    public void setUp() {
        alliances = new JsonParser().parse(ALLIANCES_2009).getAsJsonObject();
    }

    private static String[] jsonToStringArray(JsonArray array) {
        String[] result = new String[array.size()];
        int i = 0;

        for (JsonElement e : array) {
            result[i++] = e.getAsString();
        }

        return result;
    }

    @org.junit.Test
    public void testGetAlliance() {
        JsonObject redAlliance = Match.getRedAlliance(alliances);
        assertEquals(81, redAlliance.get("score").getAsInt());

        JsonObject blueAlliance = Match.getBlueAlliance(alliances);
        assertEquals(3, blueAlliance.getAsJsonArray("teams").size());
        assertEquals("frc971", blueAlliance.getAsJsonArray("teams").get(1).getAsString());
    }

    @org.junit.Test
    public void testGetScore() {
        assertEquals(81, Match.getRedScore(alliances));
        assertEquals(98, Match.getBlueScore(alliances));
    }

    @org.junit.Test
    public void testGetTeams() {
        assertArrayEquals(new String[]{"frc68", "frc217", "frc247"},
                jsonToStringArray(Match.getRedTeams(alliances)));
        assertArrayEquals(new String[]{"frc111", "frc971", "frc67"},
                jsonToStringArray(Match.getBlueTeams(alliances)));
    }

    @org.junit.Test
    public void testHasTeam() {
        JsonArray blueTeams = Match.getBlueTeams(alliances);
        assertTrue(Match.hasTeam(blueTeams, "frc111"));
        assertTrue(Match.hasTeam(blueTeams, "frc971"));
        assertTrue(Match.hasTeam(blueTeams, "frc67"));

        assertFalse(Match.hasTeam(blueTeams, "frc1111"));
        assertFalse(Match.hasTeam(blueTeams, "frc11"));
        assertFalse(Match.hasTeam(blueTeams, "frc1"));
        assertFalse(Match.hasTeam(blueTeams, "frc9"));
        assertFalse(Match.hasTeam(blueTeams, "1"));
        assertFalse(Match.hasTeam(blueTeams, ""));
    }
}
