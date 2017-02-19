package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.datafeed.maps.TeamStatsExtractor;
import com.thebluealliance.androidclient.viewmodels.LabelValueViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.res.Resources;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TeamStatsSubscriberTest {

    @Mock Resources mResources;

    TeamStatsSubscriber mSubscriber;
    JsonElement mStats;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mResources.getString(anyInt())).thenReturn("Stat");

        mSubscriber = new TeamStatsSubscriber(mResources);
        TeamStatsExtractor extractor = new TeamStatsExtractor("frc195");
        mStats = ModelMaker.getModel(JsonObject.class, "2015necmp_oprs");
        System.out.println(mStats.toString());
        mStats = extractor.call(mStats);
        System.out.println(mStats.toString());
    }

    @Test
    public void testNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testJsonNull()  {
        DatafeedTestDriver.parseJsonNull(mSubscriber);
    }
    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mStats);
    }

    @Test
    public void testParsedData()  {
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mStats);

        assertEquals(3, data.size());
        LabelValueViewModel opr = getItem(0, data);
        LabelValueViewModel dpr = getItem(1, data);
        LabelValueViewModel ccwm = getItem(2, data);

        assertEquals("87.96", opr.getValue().toString());
        assertEquals("50.89", dpr.getValue().toString());
        assertEquals("37.07", ccwm.getValue().toString());
    }

    private static LabelValueViewModel getItem(int position, List<Object> data) {
        return (LabelValueViewModel) data.get(position);
    }
}