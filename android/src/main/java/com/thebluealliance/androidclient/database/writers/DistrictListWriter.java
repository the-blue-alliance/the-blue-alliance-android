package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.District;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DistrictListWriter implements Action1<List<District>> {
    private Database mDb;

    @Inject
    public DistrictListWriter(Database db) {
        mDb = db;
    }

    @Override
    public void call(List<District> districts) {
        Schedulers.io().createWorker()
          .schedule(() -> mDb.getDistrictsTable().add(ImmutableList.copyOf(districts)));
    }
}