package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTestBase;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Media;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class MediaListWriterTest extends RobolectricPowerMockTestBase {

    Database mDb;
    BriteDatabase mBriteDb;
    MediasTable mTable;

    private List<Media> mMedias;
    private MediaListWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mTable = DatabaseMocker.mockMediasTable(mDb, mBriteDb);
        mMedias = ModelMaker.getModelList(Media.class, "media_frc254_2014");
        mWriter = new MediaListWriter(mDb, mBriteDb);
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mMedias);

        for (Media media : mMedias) {
            verify(mBriteDb).insert(Database.TABLE_MEDIAS, media.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
        }
    }
}