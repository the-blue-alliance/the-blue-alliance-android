package com.thebluealliance.androidclient.datafeed.maps;

import com.google.gson.JsonArray;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.helpers.TeamHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TeamRankExtractorTest {

    private String mSearchTeamKey;
    private TeamRankExtractor mExtractor;
    private JsonArray mRanks;

    @Before
    public void setUp() {
        mRanks = ModelMaker.getModel(JsonArray.class, "2015necmp_rankings");
        mSearchTeamKey = "frc1519";
        mExtractor = new TeamRankExtractor(mSearchTeamKey);
    }

    @Test
    public void testTeamRankExtractor() {
        JsonArray teamRank = mExtractor.call(mRanks);
        int teamNumber = TeamHelper.getTeamNumber(mSearchTeamKey);

        assertNotNull(teamRank);
        assertEquals(teamRank.size(), 2);
        assertTrue(teamRank.get(0).isJsonArray());
        assertEquals(teamRank.get(0), mRanks.get(0));
        assertTrue(teamRank.get(1).isJsonArray());
        assertEquals(teamRank.get(1).getAsJsonArray().get(1).getAsInt(), teamNumber);
    }
}