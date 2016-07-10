package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTestBase;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.DistrictTeam;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class DistrictTeamWriterTest extends RobolectricPowerMockTestBase {

    Database mDb;
    BriteDatabase mBriteDb;
    DistrictTeamsTable mTable;

    private DistrictTeam mDistrictTeam;
    private DistrictTeamWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mTable = DatabaseMocker.mockDistrictTeamsTable(mDb, mBriteDb);
        mDistrictTeam = ModelMaker.getModelList(DistrictTeam.class, "2015ne_rankings").get(0);
        mWriter = new DistrictTeamWriter(mDb, mBriteDb);
    }

    @Test
    public void testDistrictTeamWriter() {
        mWriter.write(mDistrictTeam);

        verify(mBriteDb).insert(Database.TABLE_DISTRICTTEAMS, mDistrictTeam.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
    }
}