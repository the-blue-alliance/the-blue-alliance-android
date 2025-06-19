package com.thebluealliance.androidclient.database.writers;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.model.DistrictDbModel;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import thebluealliance.api.model.District;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class DistrictWriterTest {

    @Mock Database mDb;
    @Mock DistrictsTable mTable;
    @Mock Gson mGson;

    private District mDistrict;
    private DistrictWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockDistrictsTable(mDb);
        mDistrict = ModelMaker.getModelList(District.class, "2015_districts").get(3);
        mWriter = new DistrictWriter(mDb);
    }

    @Test
    public void testDistrictWriter() {
        mWriter.write(mDistrict, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        DistrictDbModel dbDistrict = DistrictDbModel.fromDistrict(mDistrict);
        verify(db).insert(Database.TABLE_DISTRICTS, null, dbDistrict.getParams(mGson));
    }

}