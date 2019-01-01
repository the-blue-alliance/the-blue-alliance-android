package com.thebluealliance.androidclient.database.tables;

import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.HttpModule;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(DefaultTestRunner.class)
public class TeamsTableTest {

    private TeamsTable mTable;
    private List<Team> mTeams;
    private Gson mGson;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_TEAMS);
        db.execSQL(Database.CREATE_SEARCH_TEAMS);

        mGson = HttpModule.getGson();
        mTable = spy(new TeamsTable(db, mGson));
        mTeams = ModelMaker.getModelList(Team.class, "2015necmp_teams");
        for (int i = 0; i < mTeams.size(); i++) {
            mTeams.get(i).setYearsParticipated("[2015, 2016]");
        }
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mTeams.get(0), mGson);
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mTeams));
    }

    @Test
    public void testUpdate() {
        Team result = DbTableTestDriver.testUpdate(mTable,
                                                    mTeams.get(0),
                                                    team -> team.setName("Meow"),
                                                   mGson);
        assertNotNull(result);
        assertEquals("Meow", result.getName());
    }

    @Test
    public void testLastModified() {
        DbTableTestDriver.testLastModified(mTable, mTeams);
    }

    @Test
    public void testDelete() {
        DbTableTestDriver.testDelete(mTable,
                                     mTeams,
                                     TeamsTable.KEY + " = ?",
                                     new String[]{"frc1124"},
                                     1);
    }
}