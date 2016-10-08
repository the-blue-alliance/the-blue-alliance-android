package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
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
    JsonObject mStats;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mResources.getString(anyInt())).thenReturn("Stat");

        mSubscriber = new TeamStatsSubscriber(mResources);
        mStats = ModelMaker.getModel(JsonObject.class, "2015necmp_frc195_stats");
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

        assertEquals(new LabelValueViewModel("Stat", "87.96"), opr);
        assertEquals(new LabelValueViewModel("Stat", "50.89"), dpr);
        assertEquals(new LabelValueViewModel("Stat", "37.07"), ccwm);
    }

    private static LabelValueViewModel getItem(int position, List<Object> data) {
        return (LabelValueViewModel) data.get(position);
    }
}