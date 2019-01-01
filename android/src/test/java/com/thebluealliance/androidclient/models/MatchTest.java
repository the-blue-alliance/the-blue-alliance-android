package com.thebluealliance.androidclient.models;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.api.model.IMatchAlliance;
import com.thebluealliance.api.model.IMatchAlliancesContainer;
import com.thebluealliance.api.model.IMatchVideo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(DefaultTestRunner.class)
public class MatchTest {

    private Match mMatch;
    private Match mCleanMatch;

    @Before
    public void readJsonData(){
        mMatch = ModelMaker.getModel(Match.class, "2014cmp_f1m1");
        mCleanMatch = new Match();
    }

    @Test
    public void testMatchModel()  {
        assertNotNull(mMatch);
        assertEquals(mMatch.getKey(), "2014cmp_f1m1");
        assertEquals(mMatch.getMatchNumber().intValue(), 1);
        assertEquals(mMatch.getSetNumber().intValue(), 1);
        assertEquals(mMatch.getEventKey(), "2014cmp");
        assertNotNull(mMatch.getTime());
        assertEquals(mMatch.getTime().intValue(), 1398551880);
        assertNotNull(mMatch.getVideos());
        assertNotNull(mMatch.getAlliances());

        List<IMatchVideo> videos = mMatch.getVideos();
        assertEquals(videos.size(), 2);
        IMatchVideo video1 = videos.get(0);
        assertEquals(video1.getType(), "youtube");
        assertEquals(video1.getKey(), "jdJutaggCMk");

        IMatchAlliancesContainer alliances = mMatch.getAlliances();
        IMatchAlliance blueAlliance = alliances.getBlue();
        assertEquals(blueAlliance.getScore().intValue(), 361);
        List<String> blueTeams = blueAlliance.getTeamKeys();
        assertEquals(blueTeams.size(), 3);
        assertEquals(blueTeams.get(0), "frc469");
    }
}
