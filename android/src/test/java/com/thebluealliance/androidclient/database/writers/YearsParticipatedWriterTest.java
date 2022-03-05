package com.thebluealliance.androidclient.database.writers;

import static org.mockito.Mockito.when;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.TeamsTable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
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