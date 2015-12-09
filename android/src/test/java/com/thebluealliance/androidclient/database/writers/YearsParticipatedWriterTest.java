package com.thebluealliance.androidclient.database.writers;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.TeamsTable;
import com.thebluealliance.androidclient.models.BasicModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.when;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class YearsParticipatedWriterTest {

    @Mock Database mDb;
    @Mock TeamWriter mTeamWriter;

    private YearsParticipatedWriter.YearsParticipatedInfo mInfo;
    private YearsParticipatedWriter mWriter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TeamsTable mTable = DatabaseMocker.mockTeamsTable(mDb);
        mWriter = new YearsParticipatedWriter(mDb, mTeamWriter);
        JsonArray mYears = new JsonArray();
        mYears.add(new JsonPrimitive(2015));
        mInfo = new YearsParticipatedWriter.YearsParticipatedInfo("frc1124", mYears);
        when(mDb.getTeamsTable()).thenReturn(mTable);
    }

    @Test
    public void testTeamListWriter() throws BasicModel.FieldNotDefinedException {
        mWriter.write(mInfo);
    }
}