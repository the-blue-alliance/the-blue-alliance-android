package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.DistrictRanking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.res.Resources;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(DefaultTestRunner.class)
public class TeamAtDistrictBreakdownSubscriberTest {

    @Mock Database mDb;
    @Mock Resources mResources;

    Gson mGson;
    TeamAtDistrictBreakdownSubscriber mSubscriber;
    DistrictRanking mDistrictTeam;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        DatabaseMocker.mockEventsTable(mDb);
        when(mResources.getString(anyInt())).thenReturn("String");

        mGson = HttpModule.getGson();
        mSubscriber = new TeamAtDistrictBreakdownSubscriber(mResources, mDb, mGson);
        mDistrictTeam = ModelMaker.getModelList(DistrictRanking.class, "2015ne_rankings").get(0);
    }

    @Test
    public void testParseNullData()  {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing()  {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mDistrictTeam);
    }

}