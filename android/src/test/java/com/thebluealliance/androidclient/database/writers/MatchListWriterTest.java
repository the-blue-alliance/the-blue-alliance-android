package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Match;

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
public class MatchListWriterTest {

    @Mock Database mDb;
    @Mock MatchesTable mTable;

    private List<Match> mMatches;
    private MatchListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockMatchesTable(mDb);
        mMatches = ModelMaker.getMultiModelList(
          Match.class,
          "2015necmp_qm1",
          "2015necmp_qf1m1",
          "2015necmp_sf1m1",
          "2015necmp_f1m1");
        mWriter = new MatchListWriter(mDb);
    }

    @Test
    public void testMatchListWriter() {
        mWriter.write(mMatches);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (Match match : mMatches) {
            verify(db).insert(Database.TABLE_MATCHES, null, match.getParams());
        }
    }
}