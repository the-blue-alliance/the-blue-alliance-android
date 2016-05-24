package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Award;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class AwardWriter extends BaseDbWriter<Award> {
    @Inject
    public AwardWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(Award award) {
        mDb.getAwardsTable().add(award);
    }
}