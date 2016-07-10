package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Media;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class MediaWriter extends BaseDbWriter<Media> {
    @Inject
    public MediaWriter(Database db, BriteDatabase briteDb) {
        super(db, briteDb);
    }

    @Override
    @WorkerThread
    public void write(Media media) {
        mDb.getMediasTable().add(media);
    }
}