package com.thebluealliance.androidclient.database.writers;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.DistrictTeamsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.models.DistrictRanking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class DistrictTeamWriterTest {

    @Mock Database mDb;
    @Mock DistrictTeamsTable mTable;

    private DistrictRanking mDistrictTeam;
    private DistrictTeamWriter mWriter;
    private Gson mGson;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mGson = TBAAndroidModule.getGson();
        mTable = DatabaseMocker.mockDistrictTeamsTable(mDb);
        mDistrictTeam = ModelMaker.getModelList(DistrictRanking.class, "2015ne_rankings").get(0);
        mWriter = new DistrictTeamWriter(mDb);
    }

    @Test
    public void testDistrictTeamWriter() {
        mWriter.write(mDistrictTeam);

        SQLiteDatabase db = mDb.getWritableDatabase();
        verify(db).insert(Database.TABLE_DISTRICTTEAMS, null, mDistrictTeam.getParams(mGson));
    }
}