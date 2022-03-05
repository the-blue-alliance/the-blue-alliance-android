package com.thebluealliance.androidclient.subscribers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.res.Resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.eventbus.EventStatsEvent;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.StatsListElement;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.LooperMode;

import java.util.List;

@LooperMode(LooperMode.Mode.PAUSED)
@RunWith(AndroidJUnit4.class)
public class StatsListSubscriberTest {

    @Mock public Database mDb;
    @Mock public Resources mResources;
    @Mock public EventBus mEventBus;

    private StatsListSubscriber mSubscriber;
    private JsonObject mStats;
    private JsonElement mInsights;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockTeamsTable(mDb);
        when(mResources.getString(R.string.stats_format, 87.96, 50.89, 37.07))
          .thenReturn("Stats");

        mSubscriber = new StatsListSubscriber(mResources, mDb, mEventBus);
        mStats = ModelMaker.getModel(JsonObject.class, "2016nyny_oprs_apiv3");
        mInsights = ModelMaker.getModel(JsonObject.class, "2016nyny_insights_apiv3");
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleBinding()  {
        StatsListSubscriber.Model model = new StatsListSubscriber.Model(mStats, mInsights);
        DatafeedTestDriver.testSimpleParsing(mSubscriber, model);
        verify(mEventBus).post(eq(new EventStatsEvent("1. Team 3419 - <b>41.77</b><br>2. Team 578 - <b>38.19</b><br>3. Team 1635 - <b>34.83</b><br>4. Team 1797 - <b>34.65</b><br>5. Team 5806 - <b>34.63</b>")));
    }

    @Test
    public void testNoInsights() {
        StatsListSubscriber.Model model = new StatsListSubscriber.Model(mStats, null);
        DatafeedTestDriver.testSimpleParsing(mSubscriber, model);
    }

    @Test
    public void testParsedData()  {
        StatsListSubscriber.Model model = new StatsListSubscriber.Model(mStats, mInsights);
        List<ListItem> data = DatafeedTestDriver.getParsedData(mSubscriber, model);

        assertEquals(66, data.size());
        assertTrue(data.get(0) instanceof StatsListElement);

        StatsListElement actual = ((StatsListElement)data.get(0));
        assertEquals(actual.teamNumber, "3419");
        assertEquals(actual.teamName, "Team 3419");
        assertEquals(actual.opr, 41.76934455450079, 0);
        assertEquals(actual.dpr, 18.164742518609433, 0);
        assertEquals(actual.ccwm, 23.60460203589137, 0);
    }
}