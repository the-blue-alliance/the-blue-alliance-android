package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.DatafeedTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.res.Resources;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
public class TeamAtDistrictBreakdownSubscriberTest {

    @Rule public PowerMockRule rule = new PowerMockRule();

    @Mock Database mDb;
    @Mock Resources mResources;

    Gson mGson;
    TeamAtDistrictBreakdownSubscriber mSubscriber;
    DistrictTeam mDistrictTeam;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        BriteDatabase briteDb = PowerMockito.mock(BriteDatabase.class);
        DatabaseMocker.mockEventsTable(mDb, briteDb);
        when(mResources.getString(anyInt())).thenReturn("String");

        mGson = HttpModule.getGson();
        mSubscriber = new TeamAtDistrictBreakdownSubscriber(mResources, mDb, mGson);
        mDistrictTeam = ModelMaker.getModelList(DistrictTeam.class, "2015ne_rankings").get(0);
    }

    @Test
    public void testParseNullData() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.parseNullData(mSubscriber);
    }

    @Test
    public void testSimpleParsing() throws BasicModel.FieldNotDefinedException {
        DatafeedTestDriver.testSimpleParsing(mSubscriber, mDistrictTeam);
    }

}