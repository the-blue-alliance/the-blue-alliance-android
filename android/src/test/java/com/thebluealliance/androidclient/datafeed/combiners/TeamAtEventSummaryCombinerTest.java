package com.thebluealliance.androidclient.datafeed.combiners;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TeamAtEventSummaryCombinerTest {

    private Event mEvent;
    private JsonArray mRank;
    private TeamAtEventSummaryCombiner mCombiner;

    @Before
    public void setUp() {
        mEvent = ModelMaker.getModel(Event.class, "2015necmp");
        mRank = ModelMaker.getModel(JsonArray.class, "2015necmp_rankings");
        mCombiner = new TeamAtEventSummaryCombiner();
    }

    @Test
    public void testTeamAtEventSummaryCombiner() {
        TeamAtEventSummarySubscriber.Model model = mCombiner.call(mRank, mEvent);

        assertNotNull(model);
        assertEquals(model.event, mEvent);
        assertEquals(model.teamAtEventRank, mRank);
    }
}