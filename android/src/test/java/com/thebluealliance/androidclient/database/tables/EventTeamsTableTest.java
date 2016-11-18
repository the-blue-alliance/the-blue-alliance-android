package com.thebluealliance.androidclient.database.tables;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.models.EventTeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(DefaultTestRunner.class)
public class EventTeamsTableTest {
    private EventTeamsTable mTable;
    private List<EventTeam> mEventTeams;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_EVENTTEAMS);
        mTable = spy(new EventTeamsTable(db));
        mEventTeams = new ArrayList<>();
        EventTeam et1 = new EventTeam();
        et1.setKey("2015necmp_frc1124");
        et1.setTeamKey("frc1124");
        et1.setEventKey("2015necmp");
        et1.setYear(2015);
        et1.setCompWeek(7);

        EventTeam et2 = new EventTeam();
        et2.setKey("2015necmp_frc254");
        et2.setTeamKey("frc254");
        et2.setEventKey("2015necmp");
        et2.setYear(2015);
        et2.setCompWeek(7);

        mEventTeams.add(et1);
        mEventTeams.add(et2);
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mEventTeams.get(0));
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mEventTeams));
    }

    @Test
    public void testUpdate() {
        EventTeam result = DbTableTestDriver.testUpdate(mTable,
                                                    mEventTeams.get(0),
                                                    et -> et.setCompWeek(96));
        assertNotNull(result);
        assertEquals(96, result.getCompWeek().intValue());
    }

    @Test
    public void testLastModified() {
        DbTableTestDriver.testLastModified(mTable, mEventTeams);
    }

    @Test
    public void testDelete() {
        DbTableTestDriver.testDelete(mTable,
                                     mEventTeams,
                                     EventTeamsTable.TEAMKEY + " = ?",
                                     new String[]{"frc1124"},
                                     1);
    }
}