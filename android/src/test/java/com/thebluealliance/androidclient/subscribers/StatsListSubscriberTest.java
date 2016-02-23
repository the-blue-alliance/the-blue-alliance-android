package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.eventbus.EventStatsEvent;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.StatsListElement;
import com.thebluealliance.androidclient.models.BasicModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.res.Resources;

import java.util.List;

import de.greenrobot.event.EventBus;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class StatsListSubscriberTest {

    @Mock public Database mDb;
    @Mock public Resources mResources;
    @Mock public EventBus mEventBus;

    StatsListSubscriber mSubscriber;
    JsonObject mStats;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockTeamsTable(mDb);
        when(mResources.getString(R.string.stats_format, 87.96, 50.89, 37.07))
          .thenReturn("Stats");

        mSubscriber = new StatsListSubscriber(mResources, mDb, mEventBus);
        mStats = ModelMaker.getModel(JsonObject.class, "2015necmp_stats");
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleBinding() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mStats);
        verify(mEventBus).post(eq(new EventStatsEvent("1. Team 195 - <b>87.96</b>")));
    }

    @Test
    public void testParsedData() throws BasicModel.FieldNotDefinedException {
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, mStats);
        StatsListElement expected =
          new StatsListElement("frc195", "195", "Team 195", "Stats",
            87.957372917501459, 50.887943082425011, 37.06942983507642);

        assertEquals(1, data.size());
        assertEquals(expected, data.get(0));
    }
}