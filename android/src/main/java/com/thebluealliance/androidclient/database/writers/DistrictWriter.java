package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.District;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DistrictWriter implements Action1<District> {
    private Database mDb;

    @Inject
    public DistrictWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(District district) {
        Schedulers.io().createWorker().schedule(() ->  mDb.getDistrictsTable().add(district));
    }
}