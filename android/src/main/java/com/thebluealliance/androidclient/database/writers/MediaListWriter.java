package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Media;

import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;

public class MediaListWriter extends BaseDbWriter<List<Media>> {
    @Inject
    public MediaListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<Media> medias) {
        mDb.getMediasTable().add(ImmutableList.copyOf(medias));
    }
}