package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTest;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.DistrictsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.District;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class DistrictWriterTest extends RobolectricPowerMockTest {

    Database mDb;
    BriteDatabase mBriteDb;
    DistrictsTable mTable;

    private District mDistrict;
    private DistrictWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mTable = DatabaseMocker.mockDistrictsTable(mDb, mBriteDb);
        mDistrict = ModelMaker.getModel(District.class, "district_ne");
        mWriter = new DistrictWriter(mDb, mBriteDb);
    }

    @Test
    public void testDistrictWriter() {
        mWriter.write(mDistrict);

        verify(mBriteDb).insert(Database.TABLE_DISTRICTS, mDistrict.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
    }

}