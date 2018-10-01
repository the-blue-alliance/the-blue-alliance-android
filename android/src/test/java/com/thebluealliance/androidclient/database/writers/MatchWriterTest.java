package com.thebluealliance.androidclient.database.writers;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Ignore
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MatchWriterTest {

    @Mock Database mDb;
    @Mock MatchesTable mTable;

    private Match mMatch;
    private MatchWriter mWriter;
    private Gson mGson;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockMatchesTable(mDb);
        mMatch = ModelMaker.getModel(Match.class, "2015necmp_qf1m1");
        mWriter = new MatchWriter(mDb);
        mGson = HttpModule.getGson();
    }

    @Test
    public void testMatchListWriter() {
        mWriter.write(mMatch, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        verify(db).insert(Database.TABLE_MATCHES, null, mMatch.getParams(mGson));
    }
}