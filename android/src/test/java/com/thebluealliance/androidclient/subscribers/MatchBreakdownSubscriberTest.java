package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.binders.MatchBreakdownBinder;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MatchBreakdownSubscriberTest {

    private MatchBreakdownSubscriber mSubscriber;
    private Match mMatch2014;
    private Match mMatch2015;
    private Match mMatch2016;

    @Before
    public void setUp() {
        mSubscriber = new MatchBreakdownSubscriber();
        mMatch2014 = ModelMaker.getModel(Match.class, "2014necmp_qf2m1");
        mMatch2015 = ModelMaker.getModel(Match.class, "2015necmp_qm1");
        mMatch2016 = ModelMaker.getModel(Match.class, "2016ctwat_qm6");
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mMatch2016);
    }

    @Test
    public void testParsedData2014() {
        MatchBreakdownBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mMatch2014);

        // Shouldn't be able to parse, no detailed scores pre-2015
        assertNull(data);
    }

    @Test
    public void testParsedData2015()  {
        MatchBreakdownBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mMatch2015);
        assertEquals(new MatchBreakdownBinder.Model(mMatch2015.getType(),
                                                    mMatch2015.getYear(),
                                                    mMatch2015.getAlliancesJson(),
                                                    mMatch2015.getScoreBreakdownJson()), data);
    }

    @Test
    public void testParsedData2016()  {
        MatchBreakdownBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mMatch2016);
        assertEquals(new MatchBreakdownBinder.Model(mMatch2016.getType(),
                                                    mMatch2016.getYear(),
                                                    mMatch2016.getAlliancesJson(),
                                                    mMatch2016.getScoreBreakdownJson()), data);
    }
}