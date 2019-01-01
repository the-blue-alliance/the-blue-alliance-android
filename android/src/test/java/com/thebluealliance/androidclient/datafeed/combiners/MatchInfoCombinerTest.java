package com.thebluealliance.androidclient.datafeed.combiners;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.subscribers.MatchInfoSubscriber;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(DefaultTestRunner.class)
public class MatchInfoCombinerTest {

    private MatchInfoCombiner mCombiner;
    private Match mMatch;
    private Event mEvent;

    @Before
    public void setUp() {
        mMatch = ModelMaker.getModel(Match.class, "2015necmp_qm1");
        mEvent = ModelMaker.getModel(Event.class, "2015necmp");
        mCombiner = new MatchInfoCombiner();
    }

    @Test
    public void testMatchInfoCombiner() {
        MatchInfoSubscriber.Model model = mCombiner.call(mMatch, mEvent);

        assertNotNull(model);
        assertEquals(model.event, mEvent);
        assertEquals(model.match, mMatch);
    }
}