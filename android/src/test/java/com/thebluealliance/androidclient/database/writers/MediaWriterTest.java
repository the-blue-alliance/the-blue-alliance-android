package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.RobolectricPowerMockTest;
import com.thebluealliance.androidclient.database.BriteDatabaseMocker;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.DatabaseMocker;
import com.thebluealliance.androidclient.database.tables.MediasTable;
import com.thebluealliance.androidclient.datafeed.framework.ModelMaker;
import com.thebluealliance.androidclient.models.Media;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Config(manifest = Config.NONE)
@PrepareForTest(BriteDatabase.class)
public class MediaWriterTest extends RobolectricPowerMockTest {

    Database mDb;
    BriteDatabase mBriteDb;
    MediasTable mTable;

    private Media mMedia;
    private MediaWriter mWriter;

    @Before
    public void setUp() {
        mDb = mock(Database.class);
        mBriteDb = BriteDatabaseMocker.mockDatabase();
        mTable = DatabaseMocker.mockMediasTable(mDb, mBriteDb);
        mMedia = ModelMaker.getModel(Media.class, "media_youtube");
        mWriter = new MediaWriter(mDb, mBriteDb);
    }

    @Test
    public void testEventListWriter() {
        mWriter.write(mMedia);

        verify(mBriteDb).insert(Database.TABLE_MEDIAS, mMedia.getParams(), SQLiteDatabase.CONFLICT_IGNORE);
    }
}