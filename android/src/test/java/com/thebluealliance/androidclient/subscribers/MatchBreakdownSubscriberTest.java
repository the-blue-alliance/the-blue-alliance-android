package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.binders.MatchBreakdownBinder;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(DefaultTestRunner.class)
public class MatchBreakdownSubscriberTest {

    @Mock AppConfig mConfig;

    private Gson mGson;
    private MatchBreakdownSubscriber mSubscriber;
    private Match mMatch2014;
    private Match mMatch2015;
    private Match mMatch2016;
    private Match mMatch2017;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mGson = HttpModule.getGson();
        mSubscriber = new MatchBreakdownSubscriber(mGson, mConfig);
        mMatch2014 = ModelMaker.getModel(Match.class, "2014necmp_qf2m1");
        mMatch2015 = ModelMaker.getModel(Match.class, "2015necmp_qm1");
        mMatch2016 = ModelMaker.getModel(Match.class, "2016ctwat_qm6");
        mMatch2017 = ModelMaker.getModel(Match.class, "2017week0_qm7");
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
        assertEquals(getExpected(mMatch2015), data);
    }

    @Test
    public void testParsedData2016()  {
        MatchBreakdownBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mMatch2016);
        assertEquals(getExpected(mMatch2016), data);
    }

    @Test
    public void testParsedData2017() {
        when(mConfig.getBoolean(MatchBreakdownSubscriber.SHOW_2017_KEY)).thenReturn(true);
        MatchBreakdownBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mMatch2017);
        assertEquals(getExpected(mMatch2017), data);
    }

    @Test
    public void testParsedData2017KillSwitch() {
        when(mConfig.getBoolean(MatchBreakdownSubscriber.SHOW_2017_KEY)).thenReturn(false);
        MatchBreakdownBinder.Model data = DatafeedTestDriver.getParsedData(mSubscriber, mMatch2017);
        assertNull(data);
    }

    private MatchBreakdownBinder.Model getExpected(Match match) {
        return  new MatchBreakdownBinder.Model(match.getType(),
                                               match.getYear(),
                                               match.getWinningAlliance(),
                                               match.getAlliances(),
                                               mGson.fromJson(match.getScoreBreakdown(), JsonObject.class));
    }
}