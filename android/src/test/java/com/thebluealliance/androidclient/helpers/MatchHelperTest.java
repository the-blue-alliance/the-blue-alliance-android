package com.thebluealliance.androidclient.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MatchHelperTest {
    private Match mMatch;

    @Before
    public void setUp() {
        mMatch = ModelMaker.getModel(Match.class, "2016nyny_qm1_apiv3");
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
    public void testGetScore() {
        assertEquals(43, Match.getRedScore(mMatch.getAlliances()).intValue());
        assertEquals(0, Match.getBlueScore(mMatch.getAlliances()).intValue());
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
