package com.thebluealliance.androidclient.database.tables;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.IntegrationRobolectricRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DbTableTestDriver;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Media;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(IntegrationRobolectricRunner.class)
public class MediasTableTest {

    private MediasTable mTable;
    private List<Media> mMedias;

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        db.execSQL(Database.CREATE_MEDIAS);
        mTable = spy(new MediasTable(db));
        mMedias = ModelMaker.getModelList(Media.class, "media_frc254_2014");
    }

    @Test
    public void testNullValues() {
        DbTableTestDriver.testNullValues(mTable);
    }

    @Test
    public void testAddAndGet() {
        DbTableTestDriver.testAddAndGet(mTable, mMedias.get(0));
    }

    @Test
    public void testAddAndGetList() {
        DbTableTestDriver.testAddAndGetList(mTable, ImmutableList.copyOf(mMedias));
    }

    @Test
    public void testUpdate() {
        Media result = DbTableTestDriver.testUpdate(mTable,
                                                    mMedias.get(0),
                                                    media -> media.setDetails("meow"));
        assertNotNull(result);
        assertEquals("meow", result.getDetails());
    }

    @Test
    public void testLastModified() {
        DbTableTestDriver.testLastModified(mTable, mMedias);
    }

    @Test
    public void testDelete() {
        DbTableTestDriver.testDelete(mTable,
                                     mMedias,
                                     MediasTable.TYPE + " = ?",
                                     new String[]{"imgur"},
                                     1);
    }
}