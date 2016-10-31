package com.thebluealliance.androidclient.database.tables;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.datafeed.maps.AddDistrictTeamKey;
import com.thebluealliance.androidclient.models.DistrictTeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(DefaultTestRunner.class)
public class DistrictTeamsTableTest {
    private DistrictTeamsTable mTable;
    private List<DistrictTeam> mDistrictTeams;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_DISTRICTTEAMS);
        mTable = spy(new DistrictTeamsTable(db));
        AddDistrictTeamKey keyAdder = new AddDistrictTeamKey("ne", 2015);
        mDistrictTeams = ModelMaker.getModelList(DistrictTeam.class, "2015ne_rankings");
        keyAdder.call(mDistrictTeams);
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mDistrictTeams.get(0));
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mDistrictTeams));
    }

    @Test
    public void testUpdate() {
        DistrictTeam result = DbTableTestDriver.testUpdate(mTable,
                                                       mDistrictTeams.get(0),
                                                       dt -> dt.setRank(1124));
        assertNotNull(result);
        assertEquals(1124, result.getRank().intValue());
    }

    @Test
    public void testLastModified() {
        DbTableTestDriver.testLastModified(mTable, mDistrictTeams);
    }

    @Test
    public void testDelete() {
        DbTableTestDriver.testDelete(mTable,
                                     mDistrictTeams,
                                     DistrictTeamsTable.TEAM_KEY + " = ?",
                                     new String[]{"frc1124"},
                                     1);
    }
}