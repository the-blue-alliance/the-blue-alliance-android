package com.thebluealliance.androidclient.database.writers;

import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.di.TBAAndroidModule;
import com.thebluealliance.androidclient.models.Media;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class MediaListWriterTest {

    @Mock Database mDb;
    @Mock MediasTable mTable;

    private List<Media> mMedias;
    private MediaListWriter mWriter;
    private Gson mGson;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mTable = DatabaseMocker.mockMediasTable(mDb);
        mMedias = ModelMaker.getModelList(Media.class, "media_frc254_2014");
        mWriter = new MediaListWriter(mDb);
        mGson = TBAAndroidModule.getGson();
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mMedias);

        SQLiteDatabase db = mDb.getWritableDatabase();
        for (Media media : mMedias) {
            verify(db).insert(Database.TABLE_MEDIAS, null, media.getParams(mGson));
        }
    }
}