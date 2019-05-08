package com.thebluealliance.androidclient.database.writers;

import androidx.annotation.WorkerThread;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.District;

import javax.inject.Inject;

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