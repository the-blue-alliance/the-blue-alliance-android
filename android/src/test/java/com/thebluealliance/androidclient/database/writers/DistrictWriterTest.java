package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.District;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class DistrictWriterTest {

    @Mock Database mDb;
    @Mock DistrictsTable mTable;

    private District mDistrict;
    private DistrictWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockDistrictsTable(mDb);
        mDistrict = ModelMaker.getModel(District.class, "district_ne");
        mWriter = new DistrictWriter(mDb);
    }

    @Test
    public void testDistrictWriter() {
        mWriter.write(mDistrict);

        SQLiteDatabase db = mDb.getWritableDatabase();
        verify(db).insert(Database.TABLE_DISTRICTS, null, mDistrict.getParams());
    }

}