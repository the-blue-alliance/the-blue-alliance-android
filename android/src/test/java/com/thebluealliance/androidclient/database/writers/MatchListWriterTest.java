package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTestBase;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.MatchesTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class MatchListWriterTest extends RobolectricPowerMockTestBase {

    Database mDb;
    BriteDatabase mBriteDb;
    MatchesTable mTable;

    private List<Match> mMatches;
    private MatchListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mTable = DatabaseMocker.mockMatchesTable(mDb, mBriteDb);
        mMatches = ModelMaker.getMultiModelList(
          Match.class,
          "2015necmp_qm1",
          "2015necmp_qf1m1",
          "2015necmp_sf1m1",
          "2015necmp_f1m1");
        mWriter = new MatchListWriter(mDb, mBriteDb);
    }

    @Test
    public void testMatchListWriter() {
        mWriter.write(mMatches);

        for (Match match : mMatches) {
            verify(mBriteDb).insert(Database.TABLE_MATCHES, match.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
        }
    }
}