package com.thebluealliance.androidclient.database.writers;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Match;

import android.support.annotation.WorkerThread;

import javax.inject.Inject;

public class MatchWriter extends BaseDbWriter<Match> {
    @Inject
    public MatchWriter(Database db) {
        super(db);
    }

    @Override
    @WorkerThread
    public void write(Match match) {
        mDb.getMatchesTable().add(match);
    }
}