package com.thebluealliance.androidclient.database.writers;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.models.Award;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class AwardWriterTest  {

    @Mock Database mDb;
    @Mock AwardsTable mAwardsTable;

    private Award mAward;
    private AwardWriter mWriter;
    private Gson mGson;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mAwardsTable = DatabaseMocker.mockAwardsTable(mDb);
        mAward = ModelMaker.getModel(Award.class, "award_team");
        mWriter = new AwardWriter(mDb);
        mGson = TBAAndroidModule.getGson();
    }

    @Test
    public void testAwardWriter() {
        mWriter.write(mAward, 0L);

        SQLiteDatabase db =  mDb.getWritableDatabase();
        verify(db).insert(Database.TABLE_AWARDS, null, mAward.getParams(mGson));
    }
}