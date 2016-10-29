package com.thebluealliance.androidclient.database.tables;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(IntegrationRobolectricRunner.class)
public class TeamsTableTest {

    private TeamsTable mTable;
    private List<Team> mTeams;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_TEAMS);
        db.execSQL(Database.CREATE_SEARCH_TEAMS);

        mTable = spy(new TeamsTable(db));
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
        DbTableTestDriver.testAddAndGet(mTable, mTeams.get(0));
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mTeams));
    }

    @Test
    public void testUpdate() {
        Team result = DbTableTestDriver.testUpdate(mTable,
                                                    mTeams.get(0),
                                                    team -> team.setName("Meow"));
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