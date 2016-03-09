package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MatchBreakdownSubscriberTest {

    MatchBreakdownSubscriber mSubscriber;
    Match mMatch;

    @Before
    public void setUp() {
        mSubscriber = new MatchBreakdownSubscriber();
        mMatch = ModelMaker.getModel(Match.class, "2016ctwat_qm6");
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsin() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mMatch);
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        JsonObject data = DatafeedTestDriver.getParsedData(mSubscriber, mMatch);
        assertEquals(mMatch.getBreakdown(), data);
    }

}