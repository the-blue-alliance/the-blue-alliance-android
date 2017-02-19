package com.thebluealliance.androidclient.database.writers;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.DistrictRanking;

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
public class DistrictTeamWriterTest {

    @Mock Database mDb;
    @Mock DistrictTeamsTable mTable;

    private DistrictRanking mDistrictTeam;
    private DistrictTeamWriter mWriter;
    private Gson mGson;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mGson = HttpModule.getGson();
        mTable = DatabaseMocker.mockDistrictTeamsTable(mDb);
        mDistrictTeam = ModelMaker.getModelList(DistrictRanking.class, "2015ne_rankings").get(0);
        mWriter = new DistrictTeamWriter(mDb);
    }

    @Test
    public void testDistrictTeamWriter() {
        mWriter.write(mDistrictTeam, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        verify(db).insert(Database.TABLE_DISTRICTTEAMS, null, mDistrictTeam.getParams(mGson));
    }
}