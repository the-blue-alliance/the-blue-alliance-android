package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Award;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class AwardWriter implements Action1<Award> {
    private Database mDb;

    @Inject
    public AwardWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(Award award) {
        Schedulers.io().createWorker().schedule(() -> mDb.getAwardsTable().add(award));
    }
}