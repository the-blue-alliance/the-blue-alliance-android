package com.thebluealliance.androidclient.database.writers;

import com.google.gson.Gson;

import com.thebluealliance.androidclient.DefaultTestRunner;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Media;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(DefaultTestRunner.class)
public class MediaWriterTest {

    @Mock Database mDb;
    @Mock MediasTable mTable;
    @Mock Gson mGson;

    private Media mMedia;
    private MediaWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockMediasTable(mDb);
        mMedia = ModelMaker.getModel(Media.class, "media_youtube");
        mWriter = new MediaWriter(mDb);
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mMedia, 0L);

        SQLiteDatabase db = mDb.getWritableDatabase();
        verify(db).insert(Database.TABLE_MEDIAS, null, mMedia.getParams(mGson));
    }
}