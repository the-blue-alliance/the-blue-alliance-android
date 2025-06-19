package com.thebluealliance.androidclient.database.tables;

import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictTeamKey;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.models.DistrictRanking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(AndroidJUnit4.class)
public class DistrictTeamsTableTest {
    private DistrictTeamsTable mTable;
    private List<DistrictRanking> mDistrictTeams;
    private Gson mGson;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_DISTRICTTEAMS);
        mGson = TBAAndroidModule.getGson();
        mTable = spy(new DistrictTeamsTable(db, mGson));
        AddDistrictTeamKey keyAdder = new AddDistrictTeamKey("2015ne");
        mDistrictTeams = ModelMaker.getModelList(DistrictRanking.class, "2015ne_rankings");
        keyAdder.call(mDistrictTeams);
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mDistrictTeams.get(0), mGson);
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mDistrictTeams));
    }

    @Test
    public void testUpdate() {
        DistrictRanking result = DbTableTestDriver.testUpdate(mTable,
                                                              mDistrictTeams.get(0),
                                                       dt -> dt.setRank(1124),
                                                              mGson);
        assertNotNull(result);
        assertEquals(1124, result.getRank().intValue());
    }

    @Test
    public void testDelete() {
        DbTableTestDriver.testDelete(mTable,
                                     mDistrictTeams,
                                     DistrictTeamsTable.TEAM_KEY + " = ?",
                                     new String[]{"frc1124"},
                                     1);
    }
}