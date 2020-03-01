package com.thebluealliance.androidclient.database.tables;

import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictKeys;
import com.thebluealliance.androidclient.models.District;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(AndroidJUnit4.class)
public class DistrictsTableTest {
    private Gson mGson;
    private DistrictsTable mTable;
    private List<District> mDistricts;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_DISTRICTS);
        mGson = HttpModule.getGson();
        mTable = spy(new DistrictsTable(db, mGson));
        AddDistrictKeys keyAdder = new AddDistrictKeys(2015);
        mDistricts = ModelMaker.getModelList(District.class, "2015_districts");
        keyAdder.call(mDistricts);
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mDistricts.get(0), mGson);
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mDistricts));
    }

    @Test
    public void testUpdate() {
        District result = DbTableTestDriver.testUpdate(mTable,
                                                       mDistricts.get(0),
                                                       district -> district.setDisplayName("Test Dist"),
                                                       mGson);
        assertNotNull(result);
        assertEquals("Test Dist", result.getDisplayName());
    }

    @Test
    public void testLastModified() {
        DbTableTestDriver.testLastModified(mTable, mDistricts);
    }

    @Test
    public void testDelete() {
        DbTableTestDriver.testDelete(mTable,
                                     mDistricts,
                                     DistrictsTable.NAME+ " = ?",
                                     new String[]{"Michigan"},
                                     1);
    }
}