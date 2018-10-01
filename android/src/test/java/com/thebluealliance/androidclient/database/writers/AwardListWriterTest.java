package com.thebluealliance.androidclient.database.writers;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Award;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Ignore
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class AwardListWriterTest {

    @Mock Database mDb;
    @Mock AwardsTable mAwardsTable;

    private List<Award> mAwards;
    private AwardListWriter mWriter;
    private Gson mGson;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mAwardsTable = DatabaseMocker.mockAwardsTable(mDb);
        mAwards = ModelMaker.getModelList(Award.class, "2015necmp_awards");
        mWriter = new AwardListWriter(mDb);
        mGson = HttpModule.getGson();
    }

    @Test
    public void testAwardListWriter() {
        mWriter.write(mAwards, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (Award award : mAwards) {
            verify(db).insert(Database.TABLE_AWARDS, null, award.getParams(mGson));
        }
    }
}