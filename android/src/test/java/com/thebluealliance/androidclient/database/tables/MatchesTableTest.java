package com.thebluealliance.androidclient.database.tables;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Match;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(DefaultTestRunner.class)
public class MatchesTableTest {

    private MatchesTable mTable;
    private List<Match> mMatches;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_MATCHES);
        mTable = spy(new MatchesTable(db));
        mMatches = ModelMaker.getModelList(Match.class, "2016nytr_matches");
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mMatches.get(0));
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mMatches));
    }

    @Test
    public void testUpdate() {
        Match result = DbTableTestDriver.testUpdate(mTable,
                                                    mMatches.get(0),
                                                    match -> match.setSetNumber(2));
        assertNotNull(result);
        assertEquals(2, result.getSetNumber().intValue());
    }

    @Test
    public void testLastModified() {
        DbTableTestDriver.testLastModified(mTable, mMatches);
    }

    @Test
    public void testDelete() {
        DbTableTestDriver.testDelete(mTable,
                                     mMatches,
                                     MatchesTable.KEY + " = ?",
                                     new String[]{"2016nytr_qm1"},
                                     1);
    }
}