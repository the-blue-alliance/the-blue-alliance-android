package com.thebluealliance.androidclient.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MatchHelperTest {
    private static final String ALLIANCES_2009 = "{"
            + "\"red\":  {\"score\": 81, \"teams\": [\"frc68\", \"frc217\", \"frc247\"]},"
            + "\"blue\": {\"score\": 98, \"teams\": [\"frc111\", \"frc971\", \"frc67\"]}"
            + "}";
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

    @Test
    public void testGetAlliance() {
        JsonObject redAlliance = Match.getRedAlliance(alliances);
        assertEquals(81, redAlliance.get("score").getAsInt());

        JsonObject blueAlliance = Match.getBlueAlliance(alliances);
        assertEquals(3, blueAlliance.getAsJsonArray("teams").size());
        assertEquals("frc971", blueAlliance.getAsJsonArray("teams").get(1).getAsString());
    }

    @Test
    public void testGetScore() {
        assertEquals(81, Match.getRedScore(alliances));
        assertEquals(98, Match.getBlueScore(alliances));
    }

    @Test
    public void testGetTeams() {
        assertArrayEquals(new String[]{"frc68", "frc217", "frc247"},
          jsonToStringArray(Match.getRedTeams(alliances)));
        assertArrayEquals(new String[]{"frc111", "frc971", "frc67"},
          jsonToStringArray(Match.getBlueTeams(alliances)));
    }

    @Test
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

    @Test
    public void testEventKeyFromMatchKey() {
        assertEquals(MatchHelper.getEventKeyFromMatchKey("2015cthar_f1m1"), "2015cthar");
        assertEquals(MatchHelper.getEventKeyFromMatchKey("2015ctwat_qm41"), "2015ctwat");
        assertEquals(MatchHelper.getEventKeyFromMatchKey("2012ct_sf2m3"), "2012ct");

        assertEquals(MatchHelper.getEventKeyFromMatchKey("20asdfn_afds"), "20asdfn_afds");
    }

    @Test
    public void testGetNextMatchPlayed()  {
        List<Match> matches;
        Match nextMatch;

        // Empty cases
        assertNull(MatchHelper.getNextMatchPlayed(null));
        assertNull(MatchHelper.getNextMatchPlayed(new ArrayList<>()));

        // No matches played
        matches = mockMatchList(10);
        nextMatch = MatchHelper.getNextMatchPlayed(matches);
        assertEquals(nextMatch, matches.get(0));

        // One block of matches played
        markMatchesPlayed(matches, 0, 4);
        nextMatch = MatchHelper.getNextMatchPlayed(matches);
        assertEquals(nextMatch, matches.get(4));

        // Data gap
        markMatchesPlayed(matches, 6, 8);
        nextMatch = MatchHelper.getNextMatchPlayed(matches);
        assertEquals(nextMatch, matches.get(8));

        // All matches played
        markMatchesPlayed(matches, 0, 10);
        nextMatch = MatchHelper.getNextMatchPlayed(matches);
        assertNull(nextMatch);
    }

    @Test
    public void testGetLastMatchPlayed()  {
        List<Match> matches;
        Match lastMatch;

        // Empty cases
        assertNull(MatchHelper.getLastMatchPlayed(null));
        assertNull(MatchHelper.getLastMatchPlayed(new ArrayList<>()));

        // No matches played
        matches = mockMatchList(10);
        lastMatch = MatchHelper.getLastMatchPlayed(matches);
        assertNull(lastMatch);

        // One block of matches played
        markMatchesPlayed(matches, 0, 4);
        lastMatch = MatchHelper.getLastMatchPlayed(matches);
        assertEquals(lastMatch, matches.get(3));

        // Data gap
        markMatchesPlayed(matches, 6, 8);
        lastMatch = MatchHelper.getLastMatchPlayed(matches);
        assertEquals(lastMatch, matches.get(7));

        // All matches played
        markMatchesPlayed(matches, 0, 10);
        lastMatch = MatchHelper.getLastMatchPlayed(matches);
        assertEquals(lastMatch, matches.get(9));
    }

    private static @NonNull List<Match> mockMatchList(int len) {
        List<Match> matches = new ArrayList<>();
        for(int i = 0; i < len; i++) {
            matches.add(mock(Match.class));
        }
        return matches;
    }

    private static void markMatchesPlayed(List<Match> matches, int start, int end) {
        for (int i = start; i < matches.size() && i < end; i++) {
            Match match = matches.get(i);
            when(match.hasBeenPlayed()).thenReturn(true);
        }
    }
}
