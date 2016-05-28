package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.DistrictTeam;

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
public class DistrictTeamListWriterTest {

    @Mock Database mDb;
    @Mock DistrictTeamsTable mTable;

    private List<DistrictTeam> mDistrictTeams;
    private DistrictTeamListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockDistrictTeamsTable(mDb);
        mDistrictTeams = ModelMaker.getModelList(DistrictTeam.class, "2015ne_rankings");
        mWriter = new DistrictTeamListWriter(mDb);
    }

    @Test
    public void testDistrictTeamListWriter() {
        mWriter.write(mDistrictTeams);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (DistrictTeam districtTeam : mDistrictTeams) {
            verify(db).insert(Database.TABLE_DISTRICTTEAMS, null, districtTeam.getParams());
        }
    }
}