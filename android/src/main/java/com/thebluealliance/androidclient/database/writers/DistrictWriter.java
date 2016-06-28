package com.thebluealliance.androidclient.database.writers;

import com.squareup.sqlbrite.BriteDatabase;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.District;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class DistrictWriter extends BaseDbWriter<District> {
    @Inject
    public DistrictWriter(Database db, BriteDatabase briteDb) {
        super(db, briteDb);
    }

    @Override
    @WorkerThread
    public void write(District district) {
        mDb.getDistrictsTable().add(district);
    }
}