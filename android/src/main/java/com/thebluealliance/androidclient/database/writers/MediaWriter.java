package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Media;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class MediaWriter extends BaseDbWriter<Media> {
    @Inject
    public MediaWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(Media media) {
        mDb.getMediasTable().add(media);
    }
}