package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.District;

import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;

public class DistrictListWriter extends BaseDbWriter<List<District>> {
    @Inject
    public DistrictListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<District> districts) {
        mDb.getDistrictsTable().add(ImmutableList.copyOf(districts));
    }
}