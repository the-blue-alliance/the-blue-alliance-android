package com.thebluealliance.androidclient.database.writers;

import androidx.annotation.WorkerThread;

import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.models.Match;

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