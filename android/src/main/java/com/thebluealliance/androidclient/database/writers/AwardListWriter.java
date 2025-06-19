package com.thebluealliance.androidclient.database.writers;

import com.google.common.collect.ImmutableList;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Award;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.WorkerThread;

public class AwardListWriter extends BaseDbWriter<List<Award>> {
    @Inject
    public AwardListWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(List<Award> awards) {
        mDb.getAwardsTable().add(ImmutableList.copyOf(awards));
    }
}