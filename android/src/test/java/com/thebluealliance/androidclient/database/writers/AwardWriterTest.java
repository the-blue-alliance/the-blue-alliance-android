package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTestBase;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.AwardsTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Award;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class AwardWriterTest extends RobolectricPowerMockTestBase {

    Database mDb;
    BriteDatabase mBriteDb;
    AwardsTable mAwardsTable;

    private Award mAward;
    private AwardWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mAwardsTable = DatabaseMocker.mockAwardsTable(mDb, mBriteDb);
        mAward = ModelMaker.getModel(Award.class, "award_team");
        mWriter = new AwardWriter(mDb, mBriteDb);
    }

    @Test
    public void testAwardWriter() {
        mWriter.write(mAward);

        verify(mBriteDb).insert(Database.TABLE_AWARDS, mAward.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
    }
}