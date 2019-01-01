package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.TeamsTable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(DefaultTestRunner.class)
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
        List<Integer> mYears = new ArrayList<>();
        mYears.add(2015);
        mInfo = new YearsParticipatedWriter.YearsParticipatedInfo("frc1124", mYears);
        when(mDb.getTeamsTable()).thenReturn(mTable);
    }

    @Test
    public void testTeamListWriter()  {
        mWriter.write(mInfo, 0L);
    }
}