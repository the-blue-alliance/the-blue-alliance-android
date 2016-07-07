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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class DistrictListWriterTest extends RobolectricPowerMockTest{

    Database mDb;
    BriteDatabase mBriteDb;
    DistrictsTable mDistrictsTable;

    private List<District> mDistricts;
    private DistrictListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mDistrictsTable = DatabaseMocker.mockDistrictsTable(mDb, mBriteDb);
        mDistricts = ModelMaker.getModelList(District.class, "2015_districts");
        mWriter = new DistrictListWriter(mDb, mBriteDb);
    }

    @Test
    public void testDistrictListWriter() {
        mWriter.write(mDistricts);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (District district : mDistricts) {
            //verify(db).insert(Database.TABLE_DISTRICTS, null, district.getParams());
            verify(mBriteDb).insert(Database.TABLE_DISTRICTS, district.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

}