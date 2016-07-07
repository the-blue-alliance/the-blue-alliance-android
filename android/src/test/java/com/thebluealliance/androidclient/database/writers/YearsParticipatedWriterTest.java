package com.thebluealliance.androidclient.database.writers;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTestBase;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.models.BasicModel;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class YearsParticipatedWriterTest extends RobolectricPowerMockTestBase {

    Database mDb;
    BriteDatabase mBriteDb;
    TeamWriter mTeamWriter;

    private YearsParticipatedWriter.YearsParticipatedInfo mInfo;
    private YearsParticipatedWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        TeamsTable mTable = DatabaseMocker.mockTeamsTable(mDb, mBriteDb);
        mWriter = new YearsParticipatedWriter(mDb, mBriteDb, mTeamWriter);
        JsonArray mYears = new JsonArray();
        mYears.add(new JsonPrimitive(2015));
        mInfo = new YearsParticipatedWriter.YearsParticipatedInfo("frc1124", mYears);
//        when(mDb.getTeamsTable()).thenReturn(mTable);
    }

    @Test
    public void testTeamListWriter() throws BasicModel.FieldNotDefinedException {
        mWriter.write(mInfo);

        // TODO actually test stuff here
    }
}