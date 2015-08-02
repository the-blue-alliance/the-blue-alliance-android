package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Award;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class AwardListWriter implements Action1<List<Award>> {
    private Database mDb;

    @Inject
    public AwardListWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(List<Award> awards) {
        Schedulers.io().createWorker().schedule(() -> mDb.getAwardsTable().add(awards));
    }
}