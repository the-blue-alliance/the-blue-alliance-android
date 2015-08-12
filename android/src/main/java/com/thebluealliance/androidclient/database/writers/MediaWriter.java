package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Media;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MediaWriter implements Action1<Media> {
    private Database mDb;

    @Inject
    public MediaWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(Media media) {
        Schedulers.io().createWorker().schedule(() -> mDb.getMediasTable().add(media));
    }
}