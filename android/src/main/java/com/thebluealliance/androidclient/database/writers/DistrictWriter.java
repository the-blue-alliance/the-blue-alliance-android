package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;

import javax.inject.Inject;

import androidx.annotation.WorkerThread;
import thebluealliance.api.model.District;

public class DistrictWriter extends BaseDbWriter<District> {
    @Inject
    public DistrictWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(District district, Long lastModified) {
        mDb.getDistrictsTable().add(district, lastModified);
    }
}