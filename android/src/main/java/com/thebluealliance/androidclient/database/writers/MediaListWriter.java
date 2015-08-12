package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Media;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MediaListWriter implements Action1<List<Media>> {
    private Database mDb;

    @Inject
    public MediaListWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(List<Media> medias) {
        Schedulers.io().createWorker().schedule(() -> mDb.getMediasTable().add(medias));
    }
}