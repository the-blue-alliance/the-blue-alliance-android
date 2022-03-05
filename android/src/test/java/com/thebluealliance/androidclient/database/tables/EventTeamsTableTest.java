package com.thebluealliance.androidclient.database.tables;

import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.models.EventTeam;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(AndroidJUnit4.class)
public class EventTeamsTableTest {
    private TeamAtEventStatus mStatus;
    private Gson mGson;
    private EventTeamsTable mTable;
    private List<EventTeam> mEventTeams;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_EVENTTEAMS);
        mStatus = ModelMaker.getModel(TeamAtEventStatus.class, "2015necmp_frc1124_status");
        mGson = TBAAndroidModule.getGson();
        mTable = spy(new EventTeamsTable(db, mGson));
        mEventTeams = new ArrayList<>();
        EventTeam et1 = new EventTeam();
        et1.setKey("2015necmp_frc1124");
        et1.setTeamKey("frc1124");
        et1.setEventKey("2015necmp");
        et1.setYear(2015);

        EventTeam et2 = new EventTeam();
        et2.setKey("2015necmp_frc254");
        et2.setTeamKey("frc254");
        et2.setEventKey("2015necmp");
        et2.setYear(2015);

        mEventTeams.add(et1);
        mEventTeams.add(et2);
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mEventTeams.get(0), mGson);
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mEventTeams));
    }

    @Test
    public void testUpdate() {
        EventTeam result = DbTableTestDriver.testUpdate(mTable,
                                                    mEventTeams.get(0),
                                                    et -> et.setStatus(mStatus),
                                                        mGson);
        assertNotNull(result);
        assertNotNull(result.getStatus());
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