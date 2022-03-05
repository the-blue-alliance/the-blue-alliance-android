package com.thebluealliance.androidclient.database.writers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.District;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DistrictListWriterTest {

    @Mock Database mDb;
    @Mock DistrictsTable mDistrictsTable;
    @Mock Gson mGson;

    private List<District> mDistricts;
    private DistrictListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mDistrictsTable = DatabaseMocker.mockDistrictsTable(mDb);
        mDistricts = ModelMaker.getModelList(District.class, "2015_districts");
        mWriter = new DistrictListWriter(mDb);
    }

    @Test
    public void testDistrictListWriter() {
        mWriter.write(mDistricts, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (District district : mDistricts) {
            verify(db).insert(Database.TABLE_DISTRICTS, null, district.getParams(mGson));
        }
    }

}