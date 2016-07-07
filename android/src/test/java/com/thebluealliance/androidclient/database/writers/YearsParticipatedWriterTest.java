package com.thebluealliance.androidclient.database.writers;

import com.google.api.client.util.Data;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTest;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.models.BasicModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class YearsParticipatedWriterTest extends RobolectricPowerMockTest {

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