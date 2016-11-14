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

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class DistrictListWriterTest {

    @Mock Database mDb;
    @Mock DistrictsTable mDistrictsTable;

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
            verify(db).insert(Database.TABLE_DISTRICTS, null, district.getParams());
        }
    }

}