package com.thebluealliance.androidclient.database.writers;

import androidx.annotation.WorkerThread;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Award;

import javax.inject.Inject;

public class AwardWriter extends BaseDbWriter<Award> {
    @Inject
    public AwardWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(Award award, Long lastModified) {
        mDb.getAwardsTable().add(award, lastModified);
    }
}