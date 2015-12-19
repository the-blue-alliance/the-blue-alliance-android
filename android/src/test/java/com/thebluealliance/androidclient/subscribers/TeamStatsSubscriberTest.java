package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;

import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
    public void testNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testJsonNull() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseJsonNull(mSubscriber);
    }
    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mStats);
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mStats);

        assertEquals(3, data.size());
        LabelValueListItem opr = getItem(0, data);
        LabelValueListItem dpr = getItem(1, data);
        LabelValueListItem ccwm = getItem(2, data);

        assertEquals(new LabelValueListItem("Stat", "87.96"), opr);
        assertEquals(new LabelValueListItem("Stat", "50.89"), dpr);
        assertEquals(new LabelValueListItem("Stat", "37.07"), ccwm);
    }

    private static LabelValueListItem getItem(int position, List<ListItem> data) {
        return (LabelValueListItem) data.get(position);
    }
}