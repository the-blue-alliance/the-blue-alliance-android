package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.eventbus.EventRankingsEvent;
import com.thebluealliance.androidclient.models.RankingResponseObject;

import org.greenrobot.eventbus.EventBus;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class RankingsListSubscriberTest {

    @Mock Database mDb;
    @Mock EventBus mEventBus;
    @Mock Resources mResources;

    private RankingsListSubscriber mSubscriber;
    private RankingResponseObject mRankings;
    // Includes a team with a number like "####B"
    private RankingResponseObject mRankingsMultiTeam;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockTeamsTable(mDb);

        mSubscriber = new RankingsListSubscriber(mDb, mEventBus, mResources);
        mRankings = ModelMaker.getModel(RankingResponseObject.class, "2015necmp_rankings_apiv3");
        mRankingsMultiTeam = ModelMaker.getModel(RankingResponseObject.class, "2015ohri_rankings_apiv3");
        when(mResources.getString(anyInt())).thenReturn("Thing");
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mRankings);
        verify(mEventBus).post(any(EventRankingsEvent.class));
    }

    @Test
    public void testParsedData()  {
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mRankings);
        assertEquals(60, data.size());
    }

    @Test
    public void testParsedDataWithMultiTeam()  {
        List<Object> data = DatafeedTestDriver.getParsedData(mSubscriber, mRankingsMultiTeam);
        assertEquals(30, data.size());
    }
}