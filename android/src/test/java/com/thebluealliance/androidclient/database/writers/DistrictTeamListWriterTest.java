package com.thebluealliance.androidclient.database.writers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;

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

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DistrictTeamListWriterTest {

    @Mock Database mDb;
    @Mock DistrictTeamsTable mTable;

    private List<DistrictRanking> mDistrictTeams;
    private DistrictTeamListWriter mWriter;
    private Gson mGson;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockDistrictTeamsTable(mDb);
        mDistrictTeams = ModelMaker.getModelList(DistrictRanking.class, "2015ne_rankings");
        mWriter = new DistrictTeamListWriter(mDb);
        mGson = TBAAndroidModule.getGson();
    }

    @Test
    public void testDistrictTeamListWriter() {
        mWriter.write(mDistrictTeams, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (DistrictRanking districtTeam : mDistrictTeams) {
            verify(db).insert(Database.TABLE_DISTRICTTEAMS, null, districtTeam.getParams(mGson));
        }
    }
}