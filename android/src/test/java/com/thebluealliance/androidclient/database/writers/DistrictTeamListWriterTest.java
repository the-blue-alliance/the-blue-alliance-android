package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTest;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.DistrictTeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class DistrictTeamListWriterTest extends RobolectricPowerMockTest {

    Database mDb;
    BriteDatabase mBriteDb;
    DistrictTeamsTable mTable;

    private List<DistrictTeam> mDistrictTeams;
    private DistrictTeamListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mTable = DatabaseMocker.mockDistrictTeamsTable(mDb, mBriteDb);
        mDistrictTeams = ModelMaker.getModelList(DistrictTeam.class, "2015ne_rankings");
        mWriter = new DistrictTeamListWriter(mDb, mBriteDb);
    }

    @Test
    public void testDistrictTeamListWriter() {
        mWriter.write(mDistrictTeams);

        for (DistrictTeam districtTeam : mDistrictTeams) {
            verify(mBriteDb).insert(Database.TABLE_DISTRICTTEAMS, districtTeam.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
        }
    }
}