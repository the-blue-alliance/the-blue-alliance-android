package com.thebluealliance.androidclient.database.tables;

import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Award;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(AndroidJUnit4.class)
public class AwardsTableTest {

    private AwardsTable mTable;
    private List<Award> mAwards;
    private Gson mGson;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_AWARDS);
        mGson = HttpModule.getGson();
        mTable = spy(new AwardsTable(db, mGson));
        mAwards = ModelMaker.getModelList(Award.class, "2015necmp_awards");
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mAwards.get(0), mGson);
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mAwards));
    }

    @Test
    public void testUpdate() {
        Award result = DbTableTestDriver.testUpdate(mTable,
                                     mAwards.get(0),
                                     award -> award.setName("This is an award"), mGson);
        assertNotNull(result);
        assertEquals("This is an award", result.getName());
    }

    @Test
    public void testLastModified() {
        DbTableTestDriver.testLastModified(mTable, mAwards);
    }

    @Test
    public void testDelete() {
        DbTableTestDriver.testDelete(mTable,
                                     mAwards,
                                     AwardsTable.ENUM + " = ?",
                                     new String[]{"0"},
                                     1);
    }

    @Test
    public void testGetTeamAtEventAwards() {
        mTable.add(ImmutableList.copyOf(mAwards), null);

        // Test for a team with awards
        List<Award> awards = mTable.getTeamAtEventAwards("125", "2015necmp");
        assertNotNull(awards);
        assertEquals(2, awards.size());

        // Test for a team with no awards
        awards = mTable.getTeamAtEventAwards("254", "2015necmp");
        assertNotNull(awards);
        assertEquals(0, awards.size());
    }

}